package bard.db.model

class Protocol {

	String protocolName
	byte[] protocolDocument
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Assay assay

	static belongsTo = [Assay]

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
