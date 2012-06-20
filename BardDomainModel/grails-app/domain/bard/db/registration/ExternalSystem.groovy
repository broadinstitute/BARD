package bard.db.registration

class ExternalSystem {

	String systemName
	String owner
	String systemUrl
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [externalAssays: ExternalAssay]

	static mapping = {
		id column: "External_System_ID", generator: "assigned"
	}

	static constraints = {
		systemName maxSize: 128
		owner nullable: true, maxSize: 128
		systemUrl nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
