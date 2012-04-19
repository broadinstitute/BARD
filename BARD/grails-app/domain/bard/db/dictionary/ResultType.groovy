package bard.db.dictionary

import bard.db.experiment.Result
import bard.db.registration.Measure
import bard.db.util.Unit

class ResultType {

	String resultTypeName
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ElementStatus elementStatus
	Unit unit
	ResultType parentResultType

	static hasMany = [measures: Measure,
	                  ontologyItems: OntologyItem,
	                  childResultTypes: ResultType,
	                  results: Result]
	static belongsTo = [ElementStatus, Unit]

	static mapping = {
		id column: "Result_Type_ID"
        unit column: "base_unit"
        elementStatus column: "result_type_status_id"
	}

	static constraints = {
		resultTypeName maxSize: 128
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
