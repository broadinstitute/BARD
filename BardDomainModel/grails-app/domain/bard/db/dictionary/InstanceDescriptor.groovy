package bard.db.dictionary

class InstanceDescriptor {

    static expose = 'instance-descriptor'

	InstanceDescriptor parent
	Element element
	String label
	String description
	String abbreviation
	String synonyms
	String externalURL
	Unit unit
	String elementStatus

	static hasMany = [children: InstanceDescriptor]

	static mapping = {
		id column: "node_id", generator: "assigned"
		parent column: "parent_node_id"
		unit column: "unit"
		externalURL column: "external_url"
		version false
	}

	static constraints = {
		parent nullable: true
        element nullable: false
		label maxSize: 128
		description nullable: true, maxSize: 1000
		abbreviation nullable: true, maxSize: 20
		synonyms nullable: true, maxSize: 1000
		externalURL nullable: true, maxSize: 1000
		unit nullable: true
		elementStatus maxSize: 20, nullable: false, inList: ["Pending", "Published", "Deprecated", "Retired"]
	}
}
