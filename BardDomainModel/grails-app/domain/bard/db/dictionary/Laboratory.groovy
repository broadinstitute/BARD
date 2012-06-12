package bard.db.dictionary

class Laboratory {

	String laboratory
    Element element
	String description
    Laboratory parent
    String laboratoryStatus

    static hasMany = [children: Laboratory]

    static mapping = {
		id column: "node_id", generator: "assigned"
        element column: "laboratory_id"
        parent column: "parent_node_id"
        version false
	}

	static constraints = {
        laboratory maxSize: 128
        parent nullable: true
        element nullable: false
		description nullable: true, maxSize: 1000
        laboratoryStatus maxSize: 20, nullable: false, inList: ["Pending", "Published", "Deprecated", "Retired"]

    }
}
