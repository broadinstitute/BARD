package bard.db.registration

import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.DocumentType
import bard.db.enums.ReadyForExtraction
import bard.db.enums.hibernate.AssayStatusEnumUserType
import bard.db.enums.hibernate.AssayTypeEnumUserType
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType
import bard.db.experiment.Experiment
import bard.db.model.AbstractContextOwner

class Assay extends AbstractContextOwner {
    public static final int ASSAY_NAME_MAX_SIZE = 1000
    private static final int ASSAY_VERSION_MAX_SIZE = 10
    private static final int DESIGNED_BY_MAX_SIZE = 100
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int ASSAY_SHORT_NAME_MAX_SIZE = 250


    AssayStatus assayStatus = AssayStatus.DRAFT
    String assayShortName
    String assayName
    String assayVersion
    String designedBy
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY
    AssayType assayType = AssayType.REGULAR

    String modifiedBy
    // grails auto-timestamp
    Date dateCreated
    Date lastUpdated

    Set<Experiment> experiments = [] as Set<Experiment>
    Set<Measure> measures = [] as Set<Measure>
    List<AssayContext> assayContexts = [] as List<AssayContext>
    Set<AssayDocument> assayDocuments = [] as Set<AssayDocument>

    // if this is set, then don't automatically update readyForExtraction when this entity is dirty
    // this is needed to change the value to anything except "Ready"
    boolean disableUpdateReadyForExtraction = false

    static hasMany = [
            experiments: Experiment,
            measures: Measure,
            assayContexts: AssayContext,
            assayDocuments: AssayDocument
    ]

    static constraints = {
        assayStatus()
        assayShortName(maxSize: ASSAY_SHORT_NAME_MAX_SIZE, blank: false)
        assayName(maxSize: ASSAY_NAME_MAX_SIZE, blank: false)
        assayVersion(maxSize: ASSAY_VERSION_MAX_SIZE, blank: false)
        designedBy(nullable: true, maxSize: DESIGNED_BY_MAX_SIZE)
        readyForExtraction(nullable: false)
        // TODO we can use enum mapping for this http://stackoverflow.com/questions/3748760/grails-enum-mapping
        // the ' - ' is this issue in this case
        assayType(nullable: false)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static mapping = {
        id(column: "ASSAY_ID", generator: "sequence", params: [sequence: 'ASSAY_ID_SEQ'])
        assayStatus(type: AssayStatusEnumUserType)
        readyForExtraction(type: ReadyForExtractionEnumUserType)
        assayType(type: AssayTypeEnumUserType)
        assayContexts(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'true', cascade: 'all-delete-orphan')
    }

    static transients = ['assayContextItems', 'publications', 'externalURLs', 'comments', 'protocols', 'otherDocuments', 'descriptions',"disableUpdateReadyForExtraction"]



    List<AssayDocument> getPublications() {
        final List<AssayDocument> documents = assayDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_PUBLICATION } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getExternalURLs() {
        final List<AssayDocument> documents = assayDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_EXTERNAL_URL } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getComments() {
        final List<AssayDocument> documents = assayDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_COMMENTS } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }
    List<AssayDocument> getDescriptions() {
        final List<AssayDocument> documents = assayDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_DESCRIPTION } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }
    List<AssayDocument> getProtocols() {
        final List<AssayDocument> documents = assayDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_PROTOCOL } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayDocument> getOtherDocuments() {
        final List<AssayDocument> documents = assayDocuments.findAll { it.documentType == DocumentType.DOCUMENT_TYPE_OTHER } as List<AssayDocument>
        documents.sort { p1, p2 -> p1.id.compareTo(p2.id) }
        return documents
    }

    List<AssayContextItem> getAssayContextItems() {
        Set<AssayContextItem> assayContextItems = new HashSet<AssayContextItem>()
        for (AssayContext assayContext : this.assayContexts) {
            assayContextItems.addAll(assayContext.assayContextItems)
        }
        return assayContextItems as List<AssayContextItem>
    }

    /**
     * duck typing to look like project
     * @return assayDocuments
     */
    Set<AssayDocument> getDocuments() {
        this.assayDocuments
    }
    /**
     *  duck typing to look like project
     * @return assayName
     */
    String getName() {
        this.assayName
    }

    List<AssayContext> getContexts() {
        this.assayContexts
    }


    Collection<Measure> getRootMeasures() {
        return measures.findAll { it.parentMeasure == null }
    }

    /**
     * @return a list of Measures without parents sorted by displayLabel case insensitive
     */
    List<Measure> getRootMeasuresSorted() {
        return measures.findAll { it.parentMeasure == null }.sort(new MeasureCaseInsensitiveDisplayLabelComparator())
    }

    boolean allowsNewExperiments() {
        return (assayStatus != AssayStatus.RETIRED && assayType != AssayType.TEMPLATE && measures.size() > 0)
    }
}