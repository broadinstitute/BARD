package bard.db.experiment

import acl.CapPermissionService
import bard.db.enums.DocumentType
import bard.db.enums.ReadyForExtraction
import bard.db.enums.ExperimentStatus
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.ProjectExperiment
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.registration.MeasureCaseInsensitiveDisplayLabelComparator

class Experiment extends AbstractContextOwner {

    public static final int EXPERIMENT_NAME_MAX_SIZE = 1000
    private static final int MODIFIED_BY_MAX_SIZE = 40
    public static final int DESCRIPTION_MAX_SIZE = 1000
    def capPermissionService
    String experimentName
    ExperimentStatus experimentStatus = ExperimentStatus.DRAFT
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY
    Assay assay

    Date runDateFrom
    Date runDateTo
    Date holdUntilDate

    String description

    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy
    Long id;

    Integer confidenceLevel = 1

    List<ExperimentContext> experimentContexts = []
    Set<ProjectExperiment> projectExperiments = [] as Set
    Set<ExternalReference> externalReferences = [] as Set
    Set<ExperimentMeasure> experimentMeasures = [] as Set
    Set<ExperimentFile> experimentFiles = [] as Set
    Set<ExperimentDocument> experimentDocuments = [] as Set<ExperimentDocument>

    // if this is set, then don't automatically update readyForExtraction when this entity is dirty
    // this is needed to change the value to anything except "Ready"
    boolean disableUpdateReadyForExtraction = false

    static transients = ['experimentContextItems','disableUpdateReadyForExtraction']
//    static transients = ['fullyValidateContextItems','assayContextItems', 'publications', 'externalURLs', 'comments', 'protocols', 'otherDocuments', 'descriptions', "disableUpdateReadyForExtraction"]


    List<ExperimentContextItem> getExperimentContextItems() {
        Set<ExperimentContextItem> experimentContextItems = new HashSet<ExperimentContextItem>()
        for (ExperimentContext experimentContext : this.experimentContexts) {
            experimentContextItems.addAll(experimentContext.experimentContextItems)
        }
        return experimentContextItems as List<ExperimentContextItem>
    }
    static hasMany = [experimentContexts: ExperimentContext,
            experimentMeasures: ExperimentMeasure,
            externalReferences: ExternalReference,
            projectExperiments: ProjectExperiment,
            experimentFiles: ExperimentFile,
            experimentDocuments: ExperimentDocument]

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

    @Override
    void removeContext(AbstractContext context) {
        removeFromExperimentContexts(context)
    }

    @Override
    AbstractContext createContext(Map properties) {
        ExperimentContext context = new ExperimentContext(properties)
        addToExperimentContexts(context)
        return context
    }

    def afterInsert() {
        Experiment.withNewSession {
            capPermissionService?.addPermission(this)
        }
    }

    List<ExperimentDocument> getPublications() {
        final List<ExperimentDocument> documents = experimentDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_PUBLICATION } as List<ExperimentDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ExperimentDocument> getExternalURLs() {
        final List<ExperimentDocument> documents = experimentDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_EXTERNAL_URL } as List<ExperimentDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ExperimentDocument> getComments() {
        final List<ExperimentDocument> documents = experimentDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_COMMENTS } as List<ExperimentDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ExperimentDocument> getDescriptions() {
        final List<ExperimentDocument> documents = experimentDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION } as List<ExperimentDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ExperimentDocument> getProtocols() {
        final List<ExperimentDocument> documents = experimentDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_PROTOCOL } as List<ExperimentDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<ExperimentDocument> getOtherDocuments() {
        final List<ExperimentDocument> documents = experimentDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_OTHER } as List<ExperimentDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    Set<ExperimentDocument> getDocuments() {
        this.experimentDocuments
    }

    Collection<ExperimentMeasure> getRootMeasures() {
        return experimentMeasures.findAll { it.parent == null }
    }
    /**
     * @return a list of Measures without parents sorted by displayLabel case insensitive
     */
    List<ExperimentMeasure> getRootMeasuresSorted() {
        return experimentMeasures.findAll { it.parent == null }.sort(new MeasureCaseInsensitiveDisplayLabelComparator())
    }


}