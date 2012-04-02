package bard.db

class ExternalSystem {

	String systemName
	String owner
	String systemUrl

	static hasMany = [externalAssaies: ExternalAssay]

	static mapping = {
		id column: "External_System_ID"
		version false
	}

	static constraints = {
		systemName maxSize: 128
		owner nullable: true, maxSize: 128
		systemUrl nullable: true, maxSize: 1000
	}
}
