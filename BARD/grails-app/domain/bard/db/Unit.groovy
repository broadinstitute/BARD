package bard.db

class Unit {

	String unit
	String description

	static hasMany = [elements: Element,
	                  measures: Measure,
	                  resultTypes: ResultType,
	                  results: Result,
	                  unitConversionsForFromUnit: UnitConversion,
	                  unitConversionsForToUnit: UnitConversion]

	// TODO you have multiple hasMany references for class(es) [UnitConversion] 
	//      so you'll need to disambiguate them with the 'mappedBy' property:
	static mappedBy = [unitConversionsForFromUnit: "unitByFromUnit",
	                   unitConversionsForToUnit: "unitByToUnit"]

	static mapping = {
		id name: "unit", generator: "assigned"
		version false
	}

	static constraints = {
		unit maxSize: 100
		description maxSize: 1000
	}
}
