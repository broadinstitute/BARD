package bard.db

class ResultStatus {

	String status

	static hasMany = [results: Result]

	static mapping = {
		id column: "Result_Status_ID"
		version false
	}

	static constraints = {
		status maxSize: 20
	}
}
