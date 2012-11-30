package bard.db.project

class ProjectStep {
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int EDGE_NAME_MAX_SIZE = 128

    ProjectExperiment nextProjectExperiment
    ProjectExperiment previousProjectExperiment

    String edgeName

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    List<StepContext> stepContexts = [] as List

    static hasMany = [stepContexts: StepContext]

    static constraints = {
        nextProjectExperiment()
        previousProjectExperiment()
        edgeName(nullable: true, blank: false, maxSize: EDGE_NAME_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        id(column: "PROJECT_STEP_ID", generator: "sequence", params: [sequence: 'PROJECT_STEP_ID_SEQ'])
        stepContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
        nextProjectExperiment(column: 'NEXT_PROJECT_EXPERIMENT_ID')
        previousProjectExperiment(column: 'PREV_PROJECT_EXPERIMENT_ID')
    }
}
