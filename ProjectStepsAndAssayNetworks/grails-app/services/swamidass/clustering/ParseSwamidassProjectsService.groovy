package swamidass.clustering

import bard.db.experiment.Experiment
import bard.db.project.Project

import bard.db.registration.ExternalReference
import depositor.neighbor.Relation
import project.ProjectService
import registration.AssayService
import bard.db.project.ProjectExperiment
import org.apache.commons.lang.time.StopWatch
import bard.db.registration.ExternalSystem
import javax.sql.DataSource
import groovy.sql.Sql
import groovy.sql.GroovyRowResult
import bard.db.project.ProjectStep

class ParseSwamidassProjectsService {

    AssayService assayService
    ProjectService projectService
    DataSource dataSource

    public void buildBundles(File projectDirectory, Cluster cluster) {
        File bundlesFile = new File(projectDirectory, 'bundles')
        assert bundlesFile.exists(), "${bundlesFile.canonicalPath} doesn't exist"

        //Parse and build all the bundle-nodes (BIDs)
        bundlesFile.withReader {Reader reader ->
            reader.readLine() //the header line
            reader.eachLine {String line ->
                String[] values = line.trim().split('\t')
                assert values.size() == 2, "we must have a value in each one of the BID/BUNDLE columns of the bundle file"
                Integer BID = new Integer(values[0].trim())
                List<String> AIDs = values[1].split(',').collect { 'aid=' + (it.trim() - 'AID')} //e.g., AID1,AID5,AID7
                assert AIDs.size() >= 1, "We expect at least one AID in each row of the BUNDLE column: ${values[1]}"

                Bundle bundle = new Bundle(bid: BID, pubchemAIDs: AIDs)
                cluster.addToBundles(bundle)
            }
        }
    }

    public void buildBundleRelations(File projectDirectory, Cluster cluster) {
        File workflowFlie = new File(projectDirectory, 'workflow.csv')
        assert workflowFlie.exists(), "${workflowFlie.canonicalPath} doesn't exist"

        //Parse and build all the bundle-nodes (BIDs)
        workflowFlie.withReader {Reader reader ->
            reader.readLine() //the header line
            reader.eachLine {String line ->
                String[] values = line.trim().split('\t')
                assert values.size() == 4, "we must have a value in each one of the NODE/CHILD/NODE_SIZE/OVERLAP columns of the workflow.csv file"
                Integer parentBID = new Integer(values[0].trim())
                Integer childBID = null
                if (values[1].trim().isNumber()) {
                    childBID = new Integer(values[1].trim())
                }
                else {
                    assert values[1].trim() == '?', "childBID must either be a number of '?' (a leaf node): ${values[1]}"
                }
                Integer parentTestedCompounds = new Integer(values[2].trim())
                Integer parentChildOverlappingCompounds = null
                if (values[3].trim().isNumber()) {
                    parentChildOverlappingCompounds = new Integer(values[3].trim())
                }
                else {
                    assert values[3].trim() == '?', "parentChildOverlappingCompounds must either be a number of '?' (a leaf node): ${values[3]}"
                }

                //Find bundleParent and bundleChild
                Bundle parentBundle = cluster.bundles.find {Bundle bundle ->
                    return (bundle.bid == parentBID)
                }
                assert parentBundle, "bundleParent must exist"
                Bundle childBundle
                if (childBID) { //if childBID is '?' leave childBundle null
                    childBundle = cluster.bundles.find {Bundle bundle ->
                        return (bundle.bid == childBID)
                    }
                    assert childBundle, "bundleChild must exist"
                }
                //Build the BundleRelation
                BundleRelation bundleRelation = new BundleRelation(parentBundle: parentBundle,
                        childBundle: childBundle,
                        parentTestedCompounds: parentTestedCompounds,
                        parentChildOverlappingCompounds: parentChildOverlappingCompounds)
                cluster.addToBundleRelations(bundleRelation)
            }
        }
    }


    List<Project> findProjectByPubChemAID(Long aid) {
        return projectService.findProjectByPubChemAid(aid)
    }

    //Update existing projects in CAP with new depositors pubchemAIDs (experiments).
    public void updateExistingProjectFromDepositorNeighbor() {
        StopWatch sw = new StopWatch()
        sw.start()

        Integer totalProjects = Project.count()
        Integer count = 1
        Project.list().each {Project project ->
            Long projectAID = findProjectAID(project)
            if (!projectAID) {
                Log.logger.error("(${count++}/${totalProjects}) We couldn't find a summary AID for project: ${project.id}")
                return
            }
            def criteria = Relation.createCriteria()
            List<Relation> projectRelations = criteria.listDistinct {
                eq('parentId', projectAID)
            }
            Log.logger.info("(${count++}/${totalProjects} ${sw}) for project ${project.id} (AID=${projectAID}), found ${projectRelations.size()} children in the RELATION table: ${projectRelations.collect {Relation relation -> relation.child}}")

            //Find all children Experiments by PubChem AID
            List<Experiment> childrenExperiments = projectRelations.collect {Relation relation -> findExperimentByPubChemAID(relation.childId)}
            childrenExperiments.removeAll([null]) //remove all null (not-found) experiments
            //Find all the missing experiments that project doesn't have yet. We will need to add them later on.
//            childrenExperiments.removeAll {Experiment childExperiment ->
//                return project.projectExperiments*.experimentId.contains(childExperiment.id)}
            childrenExperiments.removeAll(project.projectExperiments*.experiment)
            if (childrenExperiments) {
                Log.logger.info("\t-> We found the following missing project experiments: ${childrenExperiments*.externalReferences*.extAssayRef}")
                //Create a projectExperiment for each missing one and add it to the project
                childrenExperiments.each {Experiment experiment ->
                    Date now = new Date()
                    ProjectExperiment projectExperiment = new ProjectExperiment(project: project, experiment: experiment, dateCreated: now)
                    project.addToProjectExperiments(projectExperiment)
                }
                assert project.save(flush: true)
                Log.logger.info("\t-> Adding all missing experiments to the project")
            }
            else {
                Log.logger.info("\t-> No missing experiments found")
            }
        }
        sw.stop()
        Log.logger.info("Total processing time: ${sw}")
    }

    //Create new projects (and their experiments) from the PubChem Summary AID file.
    public void createNewProjectFromPubChemSummaryAIDs(File summaryAIDsFile) {
        List<Long> newProjectIDs = []
        summaryAIDsFile.withReader {Reader reader ->

            reader.eachLine {String line ->
                //Find the project given a summary AID
                String summaryAID = line.trim()
                List<Project> projects = findProjectByPubChemAID(new Long(summaryAID))
                assert projects.size() <= 1, "There should be at most one project given a Summary AID: ${summaryAID}; found: ${projects*.id}"
                if (projects.size() == 1) {
                    Log.logger.info("Found project (${projects.first().id}) for summary-AID=${summaryAID}; no need to do anyhing")
                    return
                }

                //Create a new project with a new ExternalReference
                Date now = new Date()
                Project newProject = new Project(projectName: 'TBD',
                        groupType: 'TBD',
                        description: 'TBD',
                        dateCreated: now)
                ExternalReference newExternalReference = new ExternalReference(externalSystem: ExternalSystem.findBySystemName('PubChem'),
                        experiment: null,
                        //project: newProject,
                        dateCreated: now)
                newProject.addToExternalReferences(newExternalReference)
                newProject.save(failOnError: true, flush: true)
                newProjectIDs << newProject.id
                Log.logger.info("Creating a new project: ${newProject.id}")
            }
        }
        Log.logger.info("Completed. The following new projects were created: ${newProjectIDs}")
    }

    private Long findProjectAID(Project project) {
        List<Long> projectAIDs = project.externalReferences.collect {ExternalReference externalReference ->
            String aid = (externalReference.extAssayRef - 'aid=') - 'project_UID='
            if (!aid.isNumber()) return null
            return new Long(aid)
        }
        projectAIDs.removeAll([null])
        if (projectAIDs) {
            assert projectAIDs.size() == 1, "project ${project.id} should be connected to one and only one PubChem AID (${projectAIDs})"
            return projectAIDs.first()
        }
        return null
    }

    private Long findExperimentAID(Experiment experiment) {
        List<Long> experimentAIDs = experiment.externalReferences.collect {ExternalReference externalReference ->
            String aid = externalReference.extAssayRef - 'aid='
            if (!aid.isNumber()) return null
            return new Long(aid)
        }
        experimentAIDs.removeAll([null])
        if (experimentAIDs) {
            assert experimentAIDs.size() == 1, "experiment ${experiment.id} should be connected to one and only one PubChem AID (${experimentAIDs})"
            return experimentAIDs.first()
        }
        return null
    }

    private Experiment findExperimentByPubChemAID(Long aid) {
        def criteria = Experiment.createCriteria()
        List<Experiment> experiments = criteria.listDistinct {
            externalReferences {
                eq('extAssayRef', "aid=${aid}")
            }
        }
        if (experiments.size() > 1) {
            Log.logger.error("\t-> Multiple experiments found for given a PubChem AID: ${aid}")
        } else if (experiments.size() == 1) {
            return experiments.first()
        } else {
            Log.logger.error("\t-> We couldn't find an experiment given a PubChem AID: ${aid}")
        }
        return null
    }

    /**
     * 1. For each project in CAP:
     * 2.   For each projectExperiment in project:
     * 3.       Find if the experiment is a parent in the Swamidass model
     * 4.           If a parent, check if the child exists in the current project as well.
     * 5.               If yes, create a new ProjectStep
     * 6.Since both the parent and the child projects HAVE to exist in the project context, it's enough to check only the parent->child path and we don't have to check the other direction (child->parent).
     */
    public void createProjectStepsFromSwamidassModel() {
        StopWatch sw = new StopWatch()
        sw.start()

        Integer totalProjects = Project.count()
        Integer count = 1
        Project.list().each {Project project ->
            Log.logger.info("(${count++}/${totalProjects}\t${sw}) Processing project ${project.id}")
            //Iterate over all project's experiments
            project.projectExperiments.toList().sort{a, b -> a.id <=> b.id}.each {ProjectExperiment parentProjectExperiment ->
                Long parentExperimentAID = findExperimentAID(parentProjectExperiment.experiment)
                if (!parentExperimentAID) {
                    Log.logger.error("\tWe couldn't find a summary AID for experiment: ${experiment.id}")
                    return
                }

                //Find all the children in the Swamidass model given a parent experiment
                List<SwamidassParentChildDTO> foundChildrenDTOs = getSwamidassAssayChildrenByParentAID(parentExperimentAID)
                //Iterate over all the remaining project experiment to see if any is a child in the Swamidass model.
                project.projectExperiments.toList().sort{a, b -> a.id <=> b.id}.each {ProjectExperiment candidateChild ->
                    if (candidateChild == parentProjectExperiment) return

                    List<SwamidassParentChildDTO> foundChildrenIntersectionWithProjectExperiment = findDTOsWithProjectExperiment(foundChildrenDTOs, candidateChild)
                    if (foundChildrenIntersectionWithProjectExperiment) {
                        //Create and persist a new projectStep
                        String clusterNames = foundChildrenIntersectionWithProjectExperiment*.clusterName.join(',')
                        ProjectStep newProjectStep = new ProjectStep(edgeName: "Discovered by Swamidass clustering (${clusterNames})",
                                previousProjectExperiment: parentProjectExperiment,
                                nextProjectExperiment: candidateChild,
                                dateCreated: new Date())

                        newProjectStep.save(failOnError: true, flush: true)
                        Log.logger.info("""Successfully created a new project-step: \
parent-experinemt=${parentProjectExperiment.experiment.id} (aid=${parentExperimentAID}); \
child-experiment=${candidateChild.experiment.id} (aid=${findExperimentAID(candidateChild.experiment)}); clusters=($clusterNames)""")
                    }
                }
            }
        }
        Log.logger.info("Total processing time: ${sw}")
    }

    private List<SwamidassParentChildDTO> getSwamidassAssayChildrenByParentAID(Long aid) {
        def db = new Sql(this.dataSource)
        String queryString = 'select * from swamidass_aids_heirarchy_view v where v.parent_aid = :PubchemAID and v.child_aid != :PubchemAID'
        String queryParam = "aid=${aid.toString()}"
        List<SwamidassParentChildDTO> parentChildList = db.rows(queryString, [PubchemAID: queryParam]).collect { GroovyRowResult rowResult ->
            new SwamidassParentChildDTO(
                    parentAID: (rowResult['PARENT_AID'] - 'aid=') as long,
                    childAID: (rowResult['CHILD_AID'] - 'aid=') as Long,
                    clusterName: rowResult['CLUSTER_NAME'] as String)
        }

        return parentChildList
    }

    private List<SwamidassParentChildDTO> findDTOsWithProjectExperiment(List<SwamidassParentChildDTO> dtos, ProjectExperiment projectExperiment) {
        final long projectExperimentAID = findExperimentAID(projectExperiment.experiment)
        assert projectExperimentAID, "We must be able to resolve the experiment to an AID: ${projectExperiment.id}"

        List<SwamidassParentChildDTO> foundChildDTOs = dtos.findAll {SwamidassParentChildDTO dto -> dto.childAID == projectExperimentAID}
        return foundChildDTOs
    }
}

class SwamidassParentChildDTO {
    Long parentAID
    Long childAID
    String clusterName
}