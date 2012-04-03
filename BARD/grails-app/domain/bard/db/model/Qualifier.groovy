package bard.db.model

class Qualifier {

	String qualifier
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [measureContextItems: MeasureContextItem,
	                  resultContextItems: ResultContextItem,
	                  results: Result]

	static mapping = {
		id name: "qualifier", generator: "assigned"
	}

	static constraints = {
		qualifier maxSize: 2
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
