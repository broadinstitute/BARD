package bard.db.util

import bard.db.experiment.Result
import bard.db.registration.Measure
import bard.db.dictionary.Element
import bard.db.dictionary.ResultType

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
