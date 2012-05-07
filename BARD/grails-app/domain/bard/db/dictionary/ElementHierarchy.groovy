package bard.db.dictionary

class ElementHierarchy {

    Element parentElement
    Element childElement
    String relationshipType
    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy
    Integer version = 0

    static constraints = {
        parentElement nullable: true
        childElement nullable: false
        relationshipType nullable: false
        dateCreated maxSize: 19
        lastUpdated nullable: true, maxSize: 19
        modifiedBy nullable: true, maxSize: 40
    }
}
