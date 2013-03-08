package bard.db.project

import bard.db.experiment.Experiment
import bard.db.dictionary.Element

class ProjectService {
    /**
     * remove experiment from project, if experiment has context, remove them. remove all steps associated with
     * this experiment, and step contexts
     * @param experiment
     * @param project
     */
    void removeExperimentFromProject(Experiment experiment, Project project) {
        def projectExperiment = ProjectExperiment.findByExperimentAndProject(experiment, project)
        if (!projectExperiment) throw new UserFixableException("Can not find association between experiment " + experiment.id + " and project " + project.id)

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
        if (!fromProjectExperiment || !toProjectExperiment)
            throw new UserFixableException("Experiment " + fromExperiment.id + " and / or Experiment " + toExperiment.id + " are / is not associated with project " + project.id)
        def projectStep = ProjectStep.findByPreviousProjectExperimentAndNextProjectExperiment(fromProjectExperiment, toProjectExperiment)
        if (!projectStep)
            throw new UserFixableException("Experiment " + fromExperiment.id + " and Experiment " + toExperiment.id + " are not linked")
        deleteProjectStep(projectStep, true)
    }

    /**
     * Associate an experiment with a project if it is not already associated
     * @param experiment
     * @param project
     */
    void addExperimentToProject(Experiment experiment, Project project, Element stage) {
        if (isExperimentAssociatedWithProject(experiment, project))
            throw new UserFixableException("Experiement " + experiment.id + " is already associated with Project " + project.id)

        ProjectExperiment pe = new ProjectExperiment(experiment: experiment, project: project, stage: stage)
        project.addToProjectExperiments(pe)
        experiment.addToProjectExperiments(pe)
        pe.save()
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
        if (!fromExperiment || !toExperiment) {
            throw new UserFixableException("Either or both experiment you were trying to link does not exist in the system.")
        }
        if (fromExperiment.id == toExperiment.id) {
            throw new UserFixableException("Link between same experiments is not allowed.")
        }
        if (!isExperimentAssociatedWithProject(fromExperiment, project) ||
            !isExperimentAssociatedWithProject(toExperiment, project))
            throw new UserFixableException("Experiment " + fromExperiment.id + " or experiment " + toExperiment.id + " is not associated with project " + project.id)
        ProjectExperiment peFrom = ProjectExperiment.findByProjectAndExperiment(project, fromExperiment)
        ProjectExperiment peTo = ProjectExperiment.findByProjectAndExperiment(project, toExperiment)
        ProjectStep ps = ProjectStep.findByPreviousProjectExperimentAndNextProjectExperiment(peFrom, peTo)
        if (ps) // do not want to add one already exist
            throw new UserFixableException(("Link between " + fromExperiment.id + " and " + toExperiment.id + " does exist, can not be added again."))
        // check if there will be a cycle by adding this link, if there is, return, otherwise, add
        if (!isDAG(project, peFrom, peTo))
            throw new UserFixableException("Link " + fromExperiment.id + " and " + toExperiment.id + " results a cycle in the graph.")
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

    /**
     * Determine if the connected portion of projectexperiment graph is still a DAG (Directed acyclic graph) by adding a link between two ProjectExperiment.
     * Assume the connected portion of original graph should always be a DAG.
     * So start from the toNode, check if there will be a path to fromNode, if there is, return false, otherwise, return true.
     * NOTE: Later on, if we find out original graph can not be guaranteed to be a DAG due to data problem, this method need to be change to use
     * a more general cycle detection algorithm.
     * @param project
     * @param experiment
     * @return
     */
    boolean isDAG(Project project, ProjectExperiment fromPe, ProjectExperiment toPe) {
        List<Long> visiting = []
        List<Long> visited = []
        visiting.add(toPe.id)
        boolean isDag = true
        while (visiting.size() > 0) {
            Long current = visiting.remove(0)
            if (fromPe.id == current)
                return false
            if (visited.contains(current))
                continue
            processNode(current, visiting)
            visited.add(current)
        }
        return isDag
    }

    /**
     * Process current node by adding outgoing nodes connected with current nodes to the visiting queue
     * @param currentId
     * @param visiting
     */
    void processNode(Long currentId, List<Long> visiting) {
        ProjectExperiment pe = ProjectExperiment.findById(currentId)
        pe.followingProjectSteps.each{
            ProjectStep step ->
            visiting.add(step.nextProjectExperiment.id)
        }
    }
}