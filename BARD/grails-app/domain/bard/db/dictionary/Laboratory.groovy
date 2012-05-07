package bard.db.dictionary

class Laboratory {

	String laboratory
    Element element
	String description
    Laboratory parent


	static mapping = {
		id column: "node_id"
        element column: "laboratory_id"
	}

	static constraints = {
        laboratory maxSize: 128
        element nullable: false
		description nullable: true, maxSize: 1000
	}
}
