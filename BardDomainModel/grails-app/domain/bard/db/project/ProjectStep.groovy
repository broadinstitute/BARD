package bard.db.project

import bard.db.experiment.Experiment

class ProjectStep {
    private static final int MODIFIED_BY_MAX_SIZE = 40
    Project project
    Experiment experiment
    Experiment precedingExperiment // this current Experiment is a follow on to the preceding Experiment
    String description

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    Set<StepContext> stepContexts = [] as Set

    static hasMany = [stepContexts:StepContext]

    static belongsTo = [project:Project]

    static constraints = {
        project()
        experiment()
        precedingExperiment(nullable: true)
        description(nullable: true)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        id(column: "PROJECT_STEP_ID", generator: "sequence", params: [sequence: 'PROJECT_STEP_ID_SEQ'])
        precedingExperiment column: "FOLLOWS_EXPERIMENT_ID"
    }
}
