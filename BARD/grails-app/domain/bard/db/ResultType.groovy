package bard.db

class ResultType {

	String resultTypeName
	String description
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
		version false
	}

	static constraints = {
		resultTypeName maxSize: 128
		description nullable: true, maxSize: 1000
	}
}
