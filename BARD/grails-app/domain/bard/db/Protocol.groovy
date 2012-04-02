package bard.db

class Protocol {

	String protocolName
	byte[] protocolDocument
	Assay assay

	static belongsTo = [Assay]

	static mapping = {
		id column: "Protocol_ID"
		version false
	}

	static constraints = {
		protocolName maxSize: 500
		protocolDocument nullable: true
	}
}
