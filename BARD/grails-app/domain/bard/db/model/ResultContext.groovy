package bard.db.model

class ResultContext {

	String contextName
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [resultContextItems: ResultContextItem,
	                  results: Result]

	static mapping = {
		id column: "Result_Context_ID"
	}

	static constraints = {
		contextName nullable: true, maxSize: 125
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
