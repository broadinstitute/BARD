package bard.db.dictionary

class Ontology {

    private static final int ABBREVIATION_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40
    private static final int ONTOLOGY_NAME_MAX_SIZE = 256
    private static final int SYSTEM_URL_MAX_SIZE = 1000

    String ontologyName
    String abbreviation
    String systemUrl

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    Set<OntologyItem> ontologyItems = [] as Set

    static hasMany = [ontologyItems: OntologyItem]

    static constraints = {
        ontologyName(blank: false, maxSize: ONTOLOGY_NAME_MAX_SIZE)
        abbreviation(nullable: true, blank: false, maxSize: ABBREVIATION_MAX_SIZE)
        systemUrl(nullable: true, blank: false, maxSize: SYSTEM_URL_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static mapping = {
        id(column: 'ONTOLOGY_ID', generator: 'sequence', params: [sequence: 'ONTOLOGY_ID_SEQ'])
    }
}
