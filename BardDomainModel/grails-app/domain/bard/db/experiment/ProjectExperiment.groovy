package bard.db.experiment

import bard.db.dictionary.Stage

class ProjectExperiment {

    Project project
    Stage stage
    Experiment experiment
    Experiment precedingExperiment // this current Experiment is a follow on to the preceding Experiment
    String description

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static constraints = {
    }
    static mapping = {
        id column: "PROJECT_EXPERIMENT_ID", generator: "assigned"
    }
}
