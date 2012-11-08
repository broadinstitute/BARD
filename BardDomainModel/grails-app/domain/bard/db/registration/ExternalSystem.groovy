package bard.db.registration

class ExternalSystem {

	String systemName
	String owner
	String systemUrl
	Date dateCreated
	Date lastUpdated
	String modifiedBy

    Set<ExternalSystem> externalSystems = [] as Set

	static hasMany = [externalSystems: ExternalReference]

	static mapping = {
		id( column: 'EXTERNAL_SYSTEM_ID', generator: 'sequence', params: [sequence: 'EXTERNAL_SYSTEM_ID_SEQ'])
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
