package bard.db.registration

import bard.db.util.Unit
import bard.db.dictionary.ResultType

class Measure {

    static expose = 'measure'

	Date dateCreated
	Date lastUpdated
	String modifiedBy
	MeasureContext measureContext
	Assay assay
	Unit entryUnit
	ResultType resultType

	static belongsTo = [Assay, MeasureContext, ResultType, Unit]

	static mapping = {
		id column: "Measure_ID"
        entryUnit column: "entry_unit"
	}

	static constraints = {
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
