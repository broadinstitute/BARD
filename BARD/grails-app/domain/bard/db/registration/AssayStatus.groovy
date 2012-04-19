package bard.db.registration

class AssayStatus {

	String status
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static mapping = {
		id column: "Assay_status_ID"
	}

	static constraints = {
		status maxSize: 20, unique: true
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}

    @Override
    public String toString() {
        return id + " - " + status;
    }
}
