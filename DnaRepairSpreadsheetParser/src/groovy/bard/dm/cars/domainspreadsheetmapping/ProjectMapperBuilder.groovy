package bard.dm.cars.domainspreadsheetmapping

import bard.db.registration.ExternalSystem

import bard.dm.Log
import bard.db.experiment.Project
import bard.db.experiment.ProjectStep
import bard.db.registration.ExternalReference
import bard.dm.cars.spreadsheet.CarsProject

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectMapperBuilder {
    private static final ExternalSystem carsExternalSystem = ExternalSystem.findById(2)
    private static final String projectExternalAssayReferencePrefix = "project_UID="

    private static final String projectGroupType = "Project"
    private static final String projectReadyForExtraction = "Pending"

    private String username

    /**
     *
     * @param carsProjectColl
     * @param username for use when creating domain objects - goes in "modified by" field
     * @return
     */
    List<ProjectPair> buildProjectPairs(Collection<CarsProject> carsProjectColl, String username) {
        this.username = username

        Log.logger.info("mapping domain to spreadsheet for projects")

        List<ProjectPair> projectPairList = initialBuildAndMapExperiments(carsProjectColl)

        findOrBuildDomainProjects(projectPairList)

        return projectPairList
    }

    private void findOrBuildDomainProjects(List<ProjectPair> projectPairList) {
        Log.logger.info("finding domain projects")

        Iterator<ProjectPair> projectPairIterator = projectPairList.iterator()

        while (projectPairIterator.hasNext()) {
            ProjectPair projectPair = projectPairIterator.next()

            Log.logger.info("\tproject uid: " + projectPair.carsProject.projectUid)

            if (projectPair.experimentPairList.size() > 0) {
                Set<Project> projectSet = new HashSet<Project>()

                projectPair.experimentPairList.each {ExperimentPair experimentPair ->
                    experimentPair.experiment?.projectSteps?.each {ProjectStep projectStep ->
                        if (projectStep.project) {
                            projectSet.add(projectStep.project)
                        }
                    }

                    if (experimentPair.externalReference?.project) {
                        projectSet.add(experimentPair.externalReference.project)
                    }
                }

                projectPair.unmatchedExternalReferences.each {ExternalReference extRef ->
                    if (extRef.project) {
                        projectSet.add(extRef.project)
                    }
                }

                if (projectSet.size() > 1) {
                    Log.logger.warn("\t\tmultiple domain projects found for aids of cars project - will not process project UID: " + projectPair.carsProject.projectUid)
                    projectPairIterator.remove()
                } else if (projectSet.size() == 1) {
                    projectPair.project = projectSet.iterator().next()
                    Log.logger.info("\t\tdomain project to cars project mapping found - project UID: " + projectPair.carsProject.projectUid + " project ID: " + projectPair.project.id)
                } else {
                    Log.logger.info("\t\tno domain project found for aids of cars project - building domain project project UID: " + projectPair.carsProject.projectUid)
                    createNewProject(projectPair)
                }
            } else {
                createNewProject(projectPair)
            }
        }
    }

    private void createNewProject(ProjectPair projectPair) {
        String projectName = projectPair.carsProject.grantTitle ? projectPair.carsProject.grantTitle : projectPair.carsProject.carsExperimentList.iterator().next().assayName

        projectPair.project = new Project(projectName: projectName, dateCreated: (new Date()),
                readyForExtraction: projectReadyForExtraction, groupType: projectGroupType, modifiedBy: username)
        assert projectPair.project.save()

        ExternalReference extRef = new ExternalReference(dateCreated: (new Date()), modifiedBy: username)
        extRef.externalSystem = carsExternalSystem
        extRef.extAssayRef = projectExternalAssayReferencePrefix + projectPair.carsProject.projectUid
        extRef.project = projectPair.project
        assert extRef.save()

        projectPair.project.externalReferences.add(extRef)
    }

    private List<ProjectPair> initialBuildAndMapExperiments(Collection<CarsProject> carsProjectColl) {
        println("mapping experiments")

        List<ProjectPair> result = new LinkedList<ProjectPair>()

        final ExperimentMapper experimentMapper = new ExperimentMapper()

        carsProjectColl.eachWithIndex {CarsProject carsProject, int i ->
            Log.logger.info("mapping db to spreadsheet for project " + carsProject.projectUid)

            ProjectPair projectPair = new ProjectPair()
            projectPair.carsProject = carsProject

            experimentMapper.addExperimentPairsToProjectPair(projectPair)

            result.add(projectPair)

            if ((i%20) == 0) {
                println("mapped experiments for " + i)
            }
        }

        return result
    }
}

