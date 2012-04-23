package bard.db.util

class Unit {

	String unit
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [unitConversionsForFromUnit: UnitConversion,
	                  unitConversionsForToUnit: UnitConversion]

	static mappedBy = [unitConversionsForFromUnit: "fromUnit",
	                   unitConversionsForToUnit: "toUnit"]

	static mapping = {
		id name: "unit", generator: "assigned"
	}

	static constraints = {
		unit maxSize: 100
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
