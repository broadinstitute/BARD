package bard.dm.cars.domainspreadsheetmapping

import bard.db.registration.ExternalSystem

import bard.dm.Log
import bard.db.project.Project

import bard.db.registration.ExternalReference
import bard.dm.cars.spreadsheet.CarsProject
import bard.dm.cars.spreadsheet.exceptions.ExternalReferenceMissingProjectException
import bard.dm.cars.spreadsheet.exceptions.MultipleProjectsForProjectUidException

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/2/12
 * Time: 12:29 PM
 * To change this template use File | Settings | File Templates.
 */
class ProjectMapperBuilder {
    private  final ExternalSystem carsExternalSystem
    private  final ExternalSystem pubChemExternalSystem

    private static final int carsExternalSystemId = 2
    private static final String carsExternalAssayReferencePrefix = "project_UID="

    private static final int pubChemExternalSystemId = 1
    private  static final String pubChemExternalAssayReferencePrefix = "aid="

    private static final String projectGroupType = "Project"

    private String username

    ProjectMapperBuilder(String username) {
        this.username = username
        carsExternalSystem = ExternalSystem.findById(carsExternalSystemId)
        pubChemExternalSystem = ExternalSystem.findById(pubChemExternalSystemId)
    }

    /**
     *
     * @param carsProjectColl
     * @param username for use when creating domain objects - goes in "modified by" field
     * @return
     */
    List<ProjectPair> buildProjectPairs(Collection<CarsProject> carsProjectColl)
    throws MultipleProjectsForProjectUidException, ExternalReferenceMissingProjectException {
        Log.logger.info("mapping domain to spreadsheet for projects")

        List<ProjectPair> projectPairList = initialBuildAndMapExperiments(carsProjectColl)

        findOrBuildDomainProjects(projectPairList)

        return projectPairList
    }

    private void findOrBuildDomainProjects(List<ProjectPair> projectPairList)
    throws MultipleProjectsForProjectUidException, ExternalReferenceMissingProjectException {
        Log.logger.info("finding domain projects")


        for (ProjectPair projectPair : projectPairList) {
            String externalAssayReference = "$carsExternalAssayReferencePrefix${projectPair.carsProject.projectUid}"

            List<ExternalReference> externalReferenceList = ExternalReference.findAllByExtAssayRef(externalAssayReference)

            if (externalReferenceList.size() > 0) {
                Set<Project> projectSet = new HashSet<Project>()
                for (ExternalReference externalReference : externalReferenceList) {
                    if (externalReference.project) {
                        projectSet.add(externalReference.project)
                    }
                }

                if (projectSet.size() == 1) {
                    projectPair.project = projectSet.iterator().next()
                } else if (projectSet.size() > 1) {
                    String message = "found multiple projects via multiple external references that match ${externalAssayReference}"
                    Log.logger.error("ProjectMapperBuilder findOrBuildDomainProjects $message")
                    throw new MultipleProjectsForProjectUidException(message)
                } else {
                    String message = "found external_reference entry that matches ${externalAssayReference} but there is no project assigned"
                    Log.logger.error("ProjectMapperBuilder findOrBuildDomainProjects $message")
                    throw new ExternalReferenceMissingProjectException(message)
                }
            } else {
                createNewProject(projectPair)
            }
        }
    }

    private void createNewProject(ProjectPair projectPair) {
        String projectName = projectPair.carsProject.grantTitle ? projectPair.carsProject.grantTitle : projectPair.carsProject.carsExperimentList.iterator().next().assayName

        projectPair.project = new Project(name: projectName, dateCreated: (new Date()),
                groupType: projectGroupType, modifiedBy: username)
        assert projectPair.project.save()

        ExternalReference carsExtRef = new ExternalReference(dateCreated: (new Date()), modifiedBy: username)
        carsExtRef.externalSystem = carsExternalSystem
        carsExtRef.extAssayRef = carsExternalAssayReferencePrefix + projectPair.carsProject.projectUid
        carsExtRef.project = projectPair.project
        assert carsExtRef.save()

        println("projectPair.carsProject.summaryAid ${projectPair.carsProject.summaryAid}")
        if (projectPair.carsProject.summaryAid) {
            ExternalReference pubChemExtRef = new ExternalReference(dateCreated: (new Date()), modifiedBy: username)
            pubChemExtRef.externalSystem = pubChemExternalSystem
            pubChemExtRef.extAssayRef = pubChemExternalAssayReferencePrefix + projectPair.carsProject.summaryAid
            pubChemExtRef.project = projectPair.project
            assert pubChemExtRef.save()
        }

        projectPair.project.externalReferences.add(carsExtRef)
    }

    private List<ProjectPair> initialBuildAndMapExperiments(Collection<CarsProject> carsProjectColl) {
        println("mapping experiments")

        List<ProjectPair> result = new LinkedList<ProjectPair>()

        final ExperimentMapper experimentMapper = new ExperimentMapper()

        int i = 0
        for (CarsProject carsProject : carsProjectColl) {
            Log.logger.info("mapping db to spreadsheet for project " + carsProject.projectUid)

            ProjectPair projectPair = new ProjectPair()
            projectPair.carsProject = carsProject

            experimentMapper.addExperimentPairsToProjectPair(projectPair)

            result.add(projectPair)

            i++
            if ((i%20) == 0) {
                println("mapped experiments for " + i)
            }
        }

        return result
    }
}

