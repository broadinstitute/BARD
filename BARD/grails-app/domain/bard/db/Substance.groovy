package bard.db

class Substance {

	Integer compoundId
	String smiles
	BigDecimal molecularWeight
	String substanceType

	static hasMany = [results: Result]

	static mapping = {
		id column: "Substance_ID"
		version false
	}

	static constraints = {
		compoundId nullable: true
		smiles nullable: true, maxSize: 4000
		molecularWeight nullable: true, scale: 3
		substanceType maxSize: 20
	}
}
