package bard.db

class AssayStatus {

	String status

	static hasMany = [assaies: Assay]

	static mapping = {
		id column: "Assay_status_ID"
		version false
	}

	static constraints = {
		status maxSize: 20, unique: true
	}
}
