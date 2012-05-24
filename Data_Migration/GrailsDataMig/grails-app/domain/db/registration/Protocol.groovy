package db.registration

class Protocol {

    static expose = 'protocol'

	String protocolName
	byte[] protocolDocument
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.registration.Assay assay

	static belongsTo = [db.registration.Assay]

	static mapping = {
		id column: "Protocol_ID"
        protocolDocument sqlType: "longblob"
	}

	static constraints = {
		protocolName maxSize: 500
		protocolDocument nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
