package bard.db.registration

class AssayDocument {

    static expose = 'assay-document'

	String documentName
	String documentType
	String documentContent
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Assay assay

	static belongsTo = [assay: Assay]

	static mapping = {
		id column: "ASSAY_DOCUMENT_ID", generator: "assigned"
        documentContent type: "text", sqlType: 'clob'
	}

	static constraints = {
		documentName maxSize: 500
		documentType maxSize: 20
		documentContent nullable: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}

}
