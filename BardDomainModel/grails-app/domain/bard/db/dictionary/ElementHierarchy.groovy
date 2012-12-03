package bard.db.dictionary

class ElementHierarchy {

    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int RELATIONSHIP_TYPE_MAX_SIZE = 40

    Element parentElement
    Element childElement
    String relationshipType

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static constraints = {
        parentElement(nullable: true)
        childElement(nullable: false)
        relationshipType(nullable: false, blank: false, maxSize: RELATIONSHIP_TYPE_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static mapping = {
        id(column: 'ELEMENT_HIERARCHY_ID', generator: 'sequence', params: [sequence: 'ELEMENT_HIERARCHY_ID_SEQ'])
        parentElement(column: 'PARENT_ELEMENT_ID')
        childElement(column: 'CHILD_ELEMENT_ID')
    }
}
