package bard.db.dictionary

class Element {

    String label
    String description
    String abbreviation
    String synonyms
    String externalURL
    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy
    String elementStatus
    Unit unit
    String readyForExtraction = 'Pending'

    static hasMany = [treeRoots: TreeRoot,
            ontologyItems: OntologyItem,
            childElementRelationships: ElementHierarchy,
            parentElementRelationships: ElementHierarchy]

    static mappedBy = [childElementRelationships: "childElement",
            parentElementRelationships: "parentElement"]

    static mapping = {
        id column: "Element_ID", generator: 'assigned'
        unit column: "unit"
        externalURL column: "external_url"
    }

    static constraints = {
        label maxSize: 129
        description nullable: true, maxSize: 1000
        abbreviation nullable: true, maxSize: 20
        synonyms nullable: true, maxSize: 1000
        externalURL nullable: true, maxSize: 1000
        dateCreated maxSize: 19
        lastUpdated nullable: true, maxSize: 19
        modifiedBy nullable: true, maxSize: 40
        unit nullable: true
        elementStatus maxSize: 20, nullable: false, inList: [
                "Pending",
                "Published",
                "Deprecated",
                "Retired"
        ]
        readyForExtraction maxSize: 20, nullable: false, inList: [
                "Pending",
                "Ready",
                "Started",
                "Complete"
        ]
    }
}
