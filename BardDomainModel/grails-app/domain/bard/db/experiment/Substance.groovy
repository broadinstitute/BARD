package bard.db.experiment

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
		id( column: 'SUBSTANCE_ID', generator: 'assigned' )
	}

	static constraints = {
		compoundId nullable: true
		smiles nullable: true, maxSize: 4000
		molecularWeight( nullable: true, scale: 3)
		substanceType( blank: false, maxSize: 20, inList: ['small molecule', 'protein', 'peptide', 'antibody', 'cell', 'oligonucleotide'])
		dateCreated(nullable: false)
		lastUpdated nullable: true
		modifiedBy nullable: true, maxSize: 40
	}
}
