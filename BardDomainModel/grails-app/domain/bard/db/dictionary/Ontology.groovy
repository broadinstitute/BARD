package bard.db.dictionary

class Ontology {

    static expose = 'ontology'

	String ontologyName
	String abbreviation
	String systemUrl
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [ontologyItems: OntologyItem]

	static mapping = {
		id column: "Ontology_ID"
	}

	static constraints = {
		ontologyName maxSize: 256
		abbreviation nullable: true, maxSize: 20
		systemUrl nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
