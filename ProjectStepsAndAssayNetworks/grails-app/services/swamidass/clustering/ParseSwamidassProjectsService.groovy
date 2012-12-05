package swamidass.clustering

import bard.db.experiment.Experiment
import bard.db.project.Project
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import depositor.neighbor.Relation
import project.ProjectService
import registration.AssayService
import bard.db.project.ProjectExperiment
import org.apache.commons.lang.time.StopWatch
import bard.db.registration.ExternalSystem

class ParseSwamidassProjectsService {

    AssayService assayService
    ProjectService projectService

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
                List<Long> AIDs = values[1].split(',').collect { new Long(it.trim() - 'AID')} //e.g., AID1,AID5,AID7
                assert AIDs.size() >= 1, "We expect at least one AID in each row of the BUNDLE column: ${values[1]}"

                //Get all assays by aids
                List<Assay> assays = AIDs.collect {Long aid ->
                    List<Assay> foundAssays = assayService.findByPubChemAid(aid)
                    assert foundAssays.size() <= 1, "We expect one assay the most when searching by aid: ${foundAssays.dump()}"
                    if (!foundAssays) {
                        Log.logger.error("We couldn't find a matching assay for AID=${aid}")
                        return null
                    }
                    return foundAssays.first() //and only one
                }
                assays.removeAll([null])//remove all the null items - the ones we couldn't find a matching Assay to an AID
//                assert assays, "We expect to have at least one valid assay in each bundle"
                Bundle bundle = new Bundle(bid: BID, assays: assays)
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

    //Update existing projects in CAP with new depositors assays (experiments).
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
}
