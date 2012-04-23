package bard.db.dictionary

import bard.db.util.Unit

class ResultType {

    static expose = 'result-type'

	String resultTypeName
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ElementStatus elementStatus
	Unit baseUnit
	ResultType parentResultType

	static hasMany = [ontologyItems: OntologyItem,
	                  childResultTypes: ResultType]

	static mapping = {
		id column: "Result_Type_ID"
        baseUnit column: "base_unit"
        elementStatus column: "result_type_status_id"
	}

	static constraints = {
		resultTypeName maxSize: 128
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        baseUnit nullable: true
        parentResultType nullable: true
	}
}
