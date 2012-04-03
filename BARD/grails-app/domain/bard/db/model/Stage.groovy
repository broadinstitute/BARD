package bard.db.model

class Stage {

	String stage
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static mapping = {
		id name: "stage", generator: "assigned"
	}

	static constraints = {
		stage maxSize: 20
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
