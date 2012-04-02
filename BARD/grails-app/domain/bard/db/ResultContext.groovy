package bard.db

class ResultContext {

	String contextName

	static hasMany = [resultContextItems: ResultContextItem,
	                  results: Result]

	static mapping = {
		id column: "Result_Context_ID"
		version false
	}

	static constraints = {
		contextName nullable: true, maxSize: 125
	}
}
