package bard.db.project

import bard.db.experiment.Experiment
import bard.db.registration.Assay

class ProjectService {
    /**
     * remove experiment from project, if experiment has context, remove them. remove all steps associated with
     * this experiment, and step contexts
     * @param experiment
     * @param project
     */
    void removeExperimentFromProject(Experiment experiment, Project project) {
        def projectExperiment = ProjectExperiment.findByExperimentAndProject(experiment, project)
        if (!projectExperiment) return

        deleteProjectStepsByProjectExperiment(projectExperiment)
        deleteExperimentContextsByProjectExperiment(projectExperiment)

        projectExperiment.delete(flush: true)
    }

    /**
     * delete contexts associated with given a projectexperiment
     * @param projectExperiment
     */
    private void deleteExperimentContextsByProjectExperiment(ProjectExperiment projectExperiment) {
        def projectExperimentContexts = ProjectExperimentContext.findByProjectExperiment(projectExperiment)

        if (projectExperimentContexts) {
            projectExperiment.projectExperimentContexts.removeAll(projectExperimentContexts)
            projectExperimentContexts.each {it.delete()}
        }
    }

    /**
     * delete projectsteps having given projectexperiment as start point or end point
     * @param projectExperiment
     */
    private void deleteProjectStepsByProjectExperiment(ProjectExperiment projectExperiment) {
        // projectExperiment is start point of steps
        def projectSteps = ProjectStep.findAllByPreviousProjectExperiment(projectExperiment)

        if (projectSteps) {
            projectExperiment.followingProjectSteps.removeAll(projectSteps)
            deleteProjectSteps(projectSteps)
        }
        // projectExperiment is end point of steps
        projectSteps = ProjectStep.findAllByNextProjectExperiment(projectExperiment)
        if (projectSteps) {
            projectExperiment.precedingProjectSteps.removeAll(projectSteps)
            deleteProjectSteps(projectSteps)
        }
    }

    /**
     * delete projectstep and their contexts given a list of projectsteps
     * @param projectSteps
     */
    private void deleteProjectSteps(List<ProjectStep> projectSteps) {
        projectSteps.each { ProjectStep projectStep ->
            deleteProjectStep(projectStep, false)
        }
    }

    private void deleteProjectStep(ProjectStep projectStep, boolean isFlushTrue) {
        def stepContexts = StepContext.findByProjectStep(projectStep)
        if (stepContexts) {
            projectStep.stepContexts.removeAll(stepContexts)
            stepContexts.each { StepContext stepContext ->
                stepContext.delete()
            }
        }
        projectStep.delete(flush: isFlushTrue)
    }

    /**
     * remove edge
     * @param fromExperiment
     * @param toExperiment
     * @param project
     */
    void removeEdgeFromProject(Experiment fromExperiment, Experiment toExperiment, Project project) {
        def fromProjectExperiment = ProjectExperiment.findByExperimentAndProject(fromExperiment, project)
        def toProjectExperiment = ProjectExperiment.findByExperimentAndProject(toExperiment, project)
        if (!fromProjectExperiment || !toProjectExperiment) return
        def projectStep = ProjectStep.findByPreviousProjectExperimentAndNextProjectExperiment(fromProjectExperiment, toProjectExperiment)
        if (!projectStep) return
        deleteProjectStep(projectStep, true)
    }

    /**
     * Associate an experiment with a project if it is not already associated
     * @param experiment
     * @param project
     */
    void addExperimentToProject(Experiment experiment, Project project) {
        if (!isExperimentAssociatedWithProject(experiment, project)) {
            ProjectExperiment pe = new ProjectExperiment(experiment: experiment, project: project)
            project.addToProjectExperiments(pe)
            experiment.addToProjectExperiments(pe)
            pe.save()
        }
        project.save()
        experiment.save()
    }

    /**
     * Add link between two experiments that are associated with the project. The candidate link can not be duplicate with any existing one.
     * The candidate link can not result any cycle of graph.
     * @param fromExperiment
     * @param toExperiment
     * @param project
     */
    void linkExperiment(Experiment fromExperiment, Experiment toExperiment, Project project){
        if (!isExperimentAssociatedWithProject(fromExperiment, project) ||
            !isExperimentAssociatedWithProject(toExperiment, project))
            return
        ProjectExperiment peFrom = ProjectExperiment.findByProjectAndExperiment(project, fromExperiment)
        ProjectExperiment peTo = ProjectExperiment.findByProjectAndExperiment(project, toExperiment)
        ProjectStep ps = ProjectStep.findByPreviousProjectExperimentAndNextProjectExperiment(peFrom, peTo)
        if (ps) // do not want to add one already exist
            return
        // TODO: check if there will be a circle if we add this link, if there is, return, otherwise, add
        ProjectStep newPs = new ProjectStep(previousProjectExperiment: peFrom, nextProjectExperiment: peTo)
        newPs.save(flush: true)
        peFrom.addToFollowingProjectSteps(newPs)
        peTo.addToPrecedingProjectSteps(newPs)
    }

    /**
     * Check if an experiment is associated with a project or not
     * @param experiment
     * @param project
     * @return
     */
    boolean isExperimentAssociatedWithProject(Experiment experiment, Project project ) {
        boolean isAssociated = false
        experiment.projectExperiments.each{
            ProjectExperiment pe ->
            if (pe.project.id == project?.id)
                isAssociated = true
        }
        return isAssociated
    }
}
