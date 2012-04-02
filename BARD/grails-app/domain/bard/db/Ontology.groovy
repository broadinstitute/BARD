package bard.db

class Ontology {

	String ontologyName
	String abbreviation
	String systemUrl

	static hasMany = [ontologyItems: OntologyItem]

	static mapping = {
		id column: "Ontology_ID"
		version false
	}

	static constraints = {
		ontologyName maxSize: 256
		abbreviation nullable: true, maxSize: 20
		systemUrl nullable: true, maxSize: 1000
	}
}
