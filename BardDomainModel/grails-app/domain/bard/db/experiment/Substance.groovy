package bard.db.experiment

class Substance {

	String smiles
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [results: Result]

	static mapping = {
		id( column: 'SUBSTANCE_ID', generator: 'assigned' )
	}

	static constraints = {
		smiles nullable: true, maxSize: 4000
		dateCreated(nullable: false)
		lastUpdated nullable: true
		modifiedBy nullable: true, maxSize: 40
	}
}
