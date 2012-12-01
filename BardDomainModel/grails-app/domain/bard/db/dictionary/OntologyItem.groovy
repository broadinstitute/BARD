package bard.db.dictionary

class OntologyItem {

    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int ITEM_REFERENCE_MAX_SIZE = 20

    Ontology ontology
    Element element
    String itemReference

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static belongsTo = [ontology: Ontology]

    static constraints = {

        ontology(nullable: false)
        element(nullable: true)
        itemReference( nullable: true, blank: false, maxSize: ITEM_REFERENCE_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static mapping = {
        id(column: 'ONTOLOGY_ITEM_ID', generator: 'sequence', params: [sequence: 'ONTOLOGY_ITEM_ID_SEQ'])
    }
}
