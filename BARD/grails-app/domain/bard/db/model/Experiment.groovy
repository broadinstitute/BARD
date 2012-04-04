package bard.db.model

class Experiment {

	String experimentName
	Date runDateFrom
	Date runDateTo
	Date holdUntilDate
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	ExperimentStatus experimentStatus
	Project project
	Laboratory laboratory
	Assay assay

	static hasMany = [resultContextItems: ResultContextItem,
	                  results: Result]
	static belongsTo = [Assay, ExperimentStatus, Laboratory, Project]

	static mapping = {
		id column: "Experiment_ID"
        laboratory column: "source_id"
	}

	static constraints = {
		experimentName maxSize: 256
		runDateFrom nullable: true, maxSize: 10
		runDateTo nullable: true, maxSize: 10
		holdUntilDate nullable: true, maxSize: 10
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
