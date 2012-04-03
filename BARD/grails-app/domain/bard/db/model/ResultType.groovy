package bard.db.model

class ResultType {

	String resultTypeName
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ElementStatus elementStatus
	Unit unit
	ResultType resultType

	static hasMany = [measures: Measure,
	                  ontologyItems: OntologyItem,
	                  resultTypes: ResultType,
	                  results: Result]
	static belongsTo = [ElementStatus, Unit]

	static mapping = {
		id column: "Result_Type_ID"
	}

	static constraints = {
		resultTypeName maxSize: 128
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
