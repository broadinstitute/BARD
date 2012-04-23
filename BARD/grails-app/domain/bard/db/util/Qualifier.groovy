package bard.db.util

class Qualifier {

	String qualifier
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static mapping = {
		id name: "qualifier", generator: "assigned"
        qualifier sqlType: "char", length: 2
	}

	static constraints = {
		qualifier maxSize: 2
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
