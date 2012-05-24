package db.dictionary

class Unit {

    static expose = 'unit'

	String unit
	String description
    db.dictionary.Element element
    Long nodeId
    Long parentNodeId

	static hasMany = [unitConversionsForFromUnit: db.dictionary.UnitConversion,
	                  unitConversionsForToUnit: db.dictionary.UnitConversion]

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
