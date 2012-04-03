package bard.db.model

class ResultStatus {

	String status
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [results: Result]

	static mapping = {
		id column: "Result_Status_ID"
	}

	static constraints = {
		status maxSize: 20
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
