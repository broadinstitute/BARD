package bard.db.experiment

import acl.CapPermissionService
import bard.db.enums.ReadyForExtraction
import bard.db.enums.ExperimentStatus
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType
import bard.db.model.AbstractContextOwner
import bard.db.project.ProjectExperiment
import bard.db.registration.Assay
import bard.db.registration.ExternalReference

class Experiment extends AbstractContextOwner {

    private static final int EXPERIMENT_NAME_MAX_SIZE = 1000
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int DESCRIPTION_MAX_SIZE = 1000
    def capPermissionService
    String experimentName
    ExperimentStatus experimentStatus = ExperimentStatus.DRAFT
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY
    Assay assay

    Date runDateFrom
    Date runDateTo
    Date holdUntilDate

    String description

    Date dateCreated
    Date lastUpdated = new Date()
    String modifiedBy
    Long id;

    Integer confidenceLevel = 1

    List<ExperimentContext> experimentContexts = []
    Set<ProjectExperiment> projectExperiments = [] as Set
    Set<ExternalReference> externalReferences = [] as Set
    Set<ExperimentMeasure> experimentMeasures = [] as Set
    Set<ExperimentFile> experimentFiles = [] as Set

    // if this is set, then don't automatically update readyForExtraction when this entity is dirty
    // this is needed to change the value to anything except "Ready"
    boolean disableUpdateReadyForExtraction = false

    static transients = ['disableUpdateReadyForExtraction']

    static hasMany = [experimentContexts: ExperimentContext,
            experimentMeasures: ExperimentMeasure,
            externalReferences: ExternalReference,
            projectExperiments: ProjectExperiment,
            experimentFiles: ExperimentFile]

    static mapping = {
        id(column: "EXPERIMENT_ID", generator: "sequence", params: [sequence: 'EXPERIMENT_ID_SEQ'])
        experimentContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
        readyForExtraction(type: ReadyForExtractionEnumUserType)
    }

    static constraints = {
        experimentName(blank: false, maxSize: EXPERIMENT_NAME_MAX_SIZE)
        experimentStatus(nullable: false)
        readyForExtraction(nullable: false)
        assay()

        runDateFrom(nullable: true)
        runDateTo(nullable: true)
        holdUntilDate(nullable: true)

        description(nullable: true, blank: false, maxSize: DESCRIPTION_MAX_SIZE)
        confidenceLevel(nullable: true)
        dateCreated(nullable: false)
        lastUpdated(nullable: false)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    String getDisplayName() {
        return id + "-" + experimentName
    }

    @Override
    List getContexts() {
        return experimentContexts;
    }

    def afterInsert() {
        Experiment.withNewSession {
            capPermissionService?.addPermission(this)
        }
    }
}