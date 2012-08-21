package bard.db.dictionary

class Element {

    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int LABEL_MAX_SIZE = 128
    private static final int DESCRIPTION_MAX_SIZE = 1000
    private static final int ABBREVIATION_MAX_SIZE = 20
    private static final int SYNONYMS_MAX_SIZE = 1000
    private static final int UNIT_MAX_SIZE = 128
    private static final int EXTERNAL_URL_MAX_SIZE = 1000
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20
    private static final int ELEMENT_STATUS_MAX_SIZE = 20


    String elementStatus
    String label
    String description
    String abbreviation
    String synonyms
    Unit unit
    String externalURL
    String readyForExtraction = 'Pending'

    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy

    static hasMany = [treeRoots: TreeRoot,
            ontologyItems: OntologyItem,
            childElementRelationships: ElementHierarchy,
            parentElementRelationships: ElementHierarchy]

    static mappedBy = [childElementRelationships: "childElement",
            parentElementRelationships: "parentElement"]

    static mapping = {
        id(column: 'ELEMENT_ID', generator: 'sequence', params: [sequence: 'ELEMENT_ID_SEQ'])
        unit(column: 'unit')
        externalURL(column: 'external_url')
    }

    static constraints = {
        elementStatus(maxSize: ELEMENT_STATUS_MAX_SIZE, nullable: false, inList: [
                "Pending",
                "Published",
                "Deprecated",
                "Retired"
        ])

        label(unique:true, maxSize: LABEL_MAX_SIZE)
        description(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        abbreviation(nullable: true, maxSize: ABBREVIATION_MAX_SIZE)
        synonyms(nullable: true, maxSize: SYNONYMS_MAX_SIZE)
        unit(nullable: true, maxSize: UNIT_MAX_SIZE)
        externalURL(nullable: true, maxSize: EXTERNAL_URL_MAX_SIZE)

        readyForExtraction(maxSize: READY_FOR_EXTRACTION_MAX_SIZE, nullable: false, inList: [
                "Pending",
                "Ready",
                "Started",
                "Complete"
        ])

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
