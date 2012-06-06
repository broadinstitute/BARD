package bard.db.dictionary

class TreeRoot {

    String treeName
    Element element
    String relationshipType
    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    Long version = 0
    String modifiedBy

    static mapping = {
        id column: "tree_root_id", generator: "assigned"
    }

    static constraints = {
        element nullable: false
        relationshipType nullable: true, maxSize: 20
        treeName nullable: false, maxSize: 30
        lastUpdated nullable: true
        modifiedBy nullable: true
    }
}
