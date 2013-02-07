package bard.db.experiment

class Substance {

	Integer compoundId
	String smiles
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [results: Result]

	static mapping = {
		id( column: 'SUBSTANCE_ID', generator: 'assigned' )
	}

	static constraints = {
		compoundId nullable: true
		smiles nullable: true, maxSize: 4000
		dateCreated(nullable: false)
		lastUpdated nullable: true
		modifiedBy nullable: true, maxSize: 40
	}
}
