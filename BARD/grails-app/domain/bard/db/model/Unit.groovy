package bard.db.model

class Unit {

	String unit
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [elements: Element,
	                  measures: Measure,
	                  resultTypes: ResultType,
	                  results: Result,
	                  unitConversionsForFromUnit: UnitConversion,
	                  unitConversionsForToUnit: UnitConversion]

	// TODO you have multiple hasMany references for class(es) [UnitConversion] 
	//      so you'll need to disambiguate them with the 'mappedBy' property:
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
