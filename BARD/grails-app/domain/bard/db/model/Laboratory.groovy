package bard.db.model

class Laboratory {

	String labName
	String abbreviation
	String description
	String location
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [experiments: Experiment]

	static mapping = {
		id column: "Lab_ID"
	}

	static constraints = {
		labName maxSize: 125
		abbreviation nullable: true, maxSize: 20
		description nullable: true, maxSize: 1000
		location nullable: true, maxSize: 250
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
