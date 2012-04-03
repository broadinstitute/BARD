package bard.db.model

class AssayStatus {

	String status
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [assaies: Assay]

	static mapping = {
		id column: "Assay_status_ID"
	}

	static constraints = {
		status maxSize: 20, unique: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
