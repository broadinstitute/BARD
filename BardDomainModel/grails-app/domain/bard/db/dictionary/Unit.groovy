package bard.db.dictionary

class Unit {

	String unit
	String description
    Element element
    Long nodeId
    Long parentNodeId

	static hasMany = [unitConversionsForFromUnit: UnitConversion,
	                  unitConversionsForToUnit: UnitConversion]

	static mappedBy = [unitConversionsForFromUnit: "fromUnit",
	                   unitConversionsForToUnit: "toUnit"]

	static mapping = {
		id name: "unit", generator: "assigned"
        element column: "unit_id"
        version false
	}

	static constraints = {
		unit maxSize: 128
		description nullable: true, maxSize: 1000
        nodeId nullable: false
        parentNodeId nullable: false
	}
}
