package bard.db

class Experiment {

	String experimentName
	Date runDate
	Date holdUntilDate
	String description
	ExperimentStatus experimentStatus
	Project project
	Laboratory laboratory
	Assay assay

	static hasMany = [resultContextItems: ResultContextItem,
	                  results: Result]
	static belongsTo = [Assay, ExperimentStatus, Laboratory, Project]

	static mapping = {
		id column: "Experiment_ID"
		version false
	}

	static constraints = {
		experimentName maxSize: 256
		runDate nullable: true, maxSize: 19
		holdUntilDate nullable: true, maxSize: 10
		description maxSize: 1000
	}
}
