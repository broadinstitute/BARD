package bard.db.registration

import bard.db.dictionary.Unit
import bard.db.dictionary.ResultType
import bard.db.dictionary.Element

class Measure {

    static expose = 'measure'

	Date dateCreated
	Date lastUpdated
	String modifiedBy
	MeasureContext measureContext
	Unit entryUnit
	//ResultType resultType
	Element element
	Assay assay

	static belongsTo = [Assay]

	static mapping = {
		id column: "Measure_ID", generator: "assigned"
        entryUnit column: "entry_unit"
//		resultType column: 'result_type_id'
//		resultType ignoreNotFound : true
		element column: 'result_type_id'
	}

	static constraints = {
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
//        resultType nullable: false
		element nullable: false
        measureContext nullable: false
        entryUnit nullable: true
	}
}
