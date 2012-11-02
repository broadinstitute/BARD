package bard.db.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.registration.Assay
import bard.db.registration.ExternalReference

class Experiment {

    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20
    private static final int EXPERIMENT_NAME_MAX_SIZE = 1000
    private static final int EXPERIMENT_STATUS_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int DESCRIPTION_MAX_SIZE = 1000

    String experimentName
    ExperimentStatus experimentStatus = ExperimentStatus.Pending
    ReadyForExtraction readyForExtraction = ReadyForExtraction.Pending
    Assay assay

    Date runDateFrom
    Date runDateTo
    Date holdUntilDate

    String description

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    // TODO results can appearently be very large 10 million rows
    Set<Result> results = [] as Set<Result>
    List<ExperimentContext> experimentContexts = [] as List<ExperimentContext>
    Set<ProjectStep> projectSteps = [] as Set<ProjectStep>
    Set<ExternalReference> externalReferences = [] as Set<ExternalReference>

    static hasMany = [experimentContexts: ExperimentContext,
            results: Result,
            projectSteps: ProjectStep,
            externalReferences: ExternalReference]

    static belongsTo = [Assay]

    static mapping = {
        id(column: "EXPERIMENT_ID", generator: "sequence", params: [sequence: 'EXPERIMENT_ID_SEQ'])
        experimentContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    static constraints = {
        experimentName(blank: false, maxSize: EXPERIMENT_NAME_MAX_SIZE)
        experimentStatus(nullable: false, maxSize: EXPERIMENT_STATUS_MAX_SIZE)
        readyForExtraction(maxSize: READY_FOR_EXTRACTION_MAX_SIZE, nullable: false)
        assay()

        runDateFrom(nullable: true)
        runDateTo(nullable: true)
        holdUntilDate(nullable: true)

        description(nullable: true, blank: false, maxSize: DESCRIPTION_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}