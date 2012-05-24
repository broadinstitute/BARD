package db.dictionary

class Element {

    static expose = 'element'

    static api = [
            excludedFields: [ "treeRoots", "ontologyItems", "childElementRelationships", "parentElementRelationships" ],
            list : { params -> Element.list(params) },
            count: { params -> Element.count() }
    ]

	String label
	String description
	String abbreviation
	String acronym
	String synonyms
    String externalURL
	Date dateCreated = new Date()
	Date lastUpdated = new Date()
	String modifiedBy
	db.dictionary.ElementStatus elementStatus
	db.dictionary.Unit unit

	static hasMany = [treeRoots: db.dictionary.TreeRoot,
            ontologyItems: db.dictionary.OntologyItem,
            childElementRelationships: db.dictionary.ElementHierarchy,
            parentElementRelationships: db.dictionary.ElementHierarchy]

    static mappedBy = [childElementRelationships: "childElement",
            parentElementRelationships: "parentElement"]

    static mapping = {
		id column: "Element_ID"
        unit column: "unit"
        externalURL column: "external_url"
    }

	static constraints = {
		label maxSize: 128
		description nullable: true, maxSize: 1000
		abbreviation nullable: true, maxSize: 20
		acronym nullable: true, maxSize: 20
		synonyms nullable: true, maxSize: 1000
        externalURL nullable: true, maxSize: 1000
        dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        unit nullable: true
        elementStatus nullable: false
	}
}
