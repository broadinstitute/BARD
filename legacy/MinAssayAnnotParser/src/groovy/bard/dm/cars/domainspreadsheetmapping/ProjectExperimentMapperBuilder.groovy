package bard.dm.cars.domainspreadsheetmapping

import bard.dm.Log
import bard.db.experiment.Experiment
import bard.db.project.ProjectStep
import bard.db.registration.ExternalReference

import bard.db.project.Project
import bard.db.project.ProjectExperiment

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 9/4/12
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */

class ProjectExperimentMapperBuilder {
    private CarsBardMapping carsBardMapping

    private String username

    /**
     * @param carsBardMapping
     * @param username for use when creating domain objects - goes in "modified by" field
     */
    ProjectExperimentMapperBuilder(CarsBardMapping carsBardMapping, String username) {
        this.carsBardMapping = carsBardMapping
        this.username = username
    }

    void mapOrBuildProjectExperiments(Collection<ProjectPair> projectPairColl) {
        Log.logger.info("map or build project steps")

        for (ProjectPair projectPair : projectPairColl) {
            Log.logger.info("\tproject uid: " + projectPair.carsProject.projectUid + " id: " + projectPair.project.id)

            Map<Experiment, ProjectExperiment> experimentExistingProjectExperimentMap =
                buildExperimentExistingProjectExperimentMap(projectPair.project.projectExperiments)

            for (ExperimentPair experimentPair : projectPair.experimentPairList) {

                ProjectExperiment projectExperiment = experimentExistingProjectExperimentMap.get(experimentPair.experiment)
                if (! projectExperiment) {
                    projectExperiment = createAndSaveProjectExperiment(projectPair.project, experimentPair.experiment)
                }

                projectPair.projectExperimentPairSet.add(new ProjectExperimentPair(experimentPair: experimentPair,
                        projectExperiment: projectExperiment))
            }

            for (ExternalReference extRef : projectPair.unmatchedExternalReferences) {
                if (extRef.experiment) {
                    Log.logger.info("\t\tunmatched external reference contains an experiment so a project step is being created linking it to the project.  Note: will not be annotated from CARS (no matching CARS experiment).  experiment id: " + extRef.experiment.id)
                    createAndSaveProjectExperiment(projectPair.project, extRef.experiment)
                }
            }
        }
    }


    private ProjectExperiment createAndSaveProjectExperiment(Project project, Experiment experiment) {
        ProjectExperiment projectExperiment = new ProjectExperiment(project:  project, experiment: experiment,
                dateCreated: new Date(), modifiedBy: username)
        project.projectExperiments.add(projectExperiment)
        experiment.projectExperiments.add(projectExperiment)
        assert projectExperiment.save()

        return projectExperiment
    }


    private static Map<Experiment, ProjectExperiment> buildExperimentExistingProjectExperimentMap(Collection<ProjectExperiment> projectExperimentColl) {
        Map<Experiment, ProjectExperiment> result = new HashMap<Experiment, ProjectExperiment>()

        for (ProjectExperiment projectExperiment : projectExperimentColl) {
            result.put(projectExperiment.experiment, projectExperiment)
        }

        return result
    }

}

