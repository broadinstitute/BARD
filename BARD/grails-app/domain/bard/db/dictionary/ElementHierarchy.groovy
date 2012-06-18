package bard.db.dictionary

class ElementHierarchy {

    Element parentElement
    Element childElement
    String relationshipType
    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static mapping = {
        id column: "element_hierarchy_id", generator: "assigned"
    }

    static constraints = {
        parentElement nullable: true
        childElement nullable: false
        relationshipType nullable: false
        lastUpdated nullable: true
        modifiedBy nullable: true, maxSize: 40
    }
}
