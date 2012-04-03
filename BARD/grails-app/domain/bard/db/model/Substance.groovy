package bard.db.model

class Substance {

	Integer compoundId
	String smiles
	BigDecimal molecularWeight
	String substanceType
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [results: Result]

	static mapping = {
		id column: "Substance_ID"
	}

	static constraints = {
		compoundId nullable: true
		smiles nullable: true, maxSize: 4000
		molecularWeight nullable: true, scale: 3
		substanceType maxSize: 20
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
