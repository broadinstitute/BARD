package bard.db.experiment

import bard.db.dictionary.Element



class ProjectExperiment {

    Project project
    Element stage
    Experiment experiment
    Experiment precedingExperiment // this current Experiment is a follow on to the preceding Experiment
    String description

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static constraints = {
        project()
        stage(nullable: true)
        experiment()
        precedingExperiment(nullable: true)
        description(nullable: true)
        dateCreated()
        lastUpdated(nullable: true) // check on this with Simon, seems like it shouldn't be nullable
        modifiedBy(nullable: true)  // check on this with Simon, seems like it shouldn't be nullable
    }
    static mapping = {
        id(column: "PROJECT_EXPERIMENT_ID", generator: "sequence", params: [sequence: 'PROJECT_EXPERIMENT_ID_SEQ'])
        precedingExperiment column: "FOLLOWS_EXPERIMENT_ID"
    }
}
