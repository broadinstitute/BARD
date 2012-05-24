package db.registration

import db.dictionary.ResultType
import db.dictionary.Unit

class Measure {

    static expose = 'measure'

	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.registration.MeasureContext measureContext
	Unit entryUnit
	ResultType resultType

	static belongsTo = [db.registration.MeasureContext]

	static mapping = {
		id column: "Measure_ID"
        entryUnit column: "entry_unit"
	}

	static constraints = {
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        resultType nullable: false
        measureContext nullable: false
        entryUnit nullable: true
	}
}
