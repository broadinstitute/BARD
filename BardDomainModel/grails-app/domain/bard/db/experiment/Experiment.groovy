package bard.db.experiment

import bard.db.enums.ReadyForExtraction
import bard.db.model.AbstractContextOwner
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.project.ProjectStep
import bard.db.project.ProjectExperiment

class Experiment  extends AbstractContextOwner{

    private static final int EXPERIMENT_NAME_MAX_SIZE = 1000
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

    // TODO results can apparently be very large 10 million rows
    List<ExperimentContext> experimentContexts = []
    Set<ProjectExperiment> projectExperiments = [] as Set
    Set<ExternalReference> externalReferences = [] as Set
    Set<ExperimentMeasure> experimentMeasures = [] as Set
    Set<Result> results = [] as Set

    static hasMany = [experimentContexts: ExperimentContext,
            experimentMeasures: ExperimentMeasure,
            externalReferences: ExternalReference,
            projectExperiments: ProjectExperiment,
            results: Result]

    static mapping = {
        id(column: "EXPERIMENT_ID", generator: "sequence", params: [sequence: 'EXPERIMENT_ID_SEQ'])
        experimentContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    static constraints = {
        experimentName(blank: false, maxSize: EXPERIMENT_NAME_MAX_SIZE)
        experimentStatus(nullable: false)
        readyForExtraction( nullable: false)
        assay()

        runDateFrom(nullable: true)
        runDateTo(nullable: true)
        holdUntilDate(nullable: true)

        description(nullable: true, blank: false, maxSize: DESCRIPTION_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    String getDisplayName() {
        return id + "-" + experimentName
    }

    @Override
    List getContexts() {
        return experimentContexts;
    }
}