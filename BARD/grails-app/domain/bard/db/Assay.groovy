package bard.db

class Assay {

	String assayName
	// assayVersion default is 1
	String assayVersion = "1";
	String description
	String designedBy
	AssayStatus assayStatus

	static hasMany = [experiments: Experiment,
	                  externalAssaies: ExternalAssay,
	                  measureContextItems: MeasureContextItem,
	                  measures: Measure,
	                  projectAssaies: ProjectAssay,
	                  protocols: Protocol]
	static belongsTo = [AssayStatus]

	static mapping = {
		id column: "Assay_ID"
		version false
	}

	static constraints = {
		assayName maxSize: 128
		assayVersion maxSize: 10
		description nullable: true, maxSize: 1000
		designedBy nullable: true, maxSize: 100
	}
}
