package db.experiment

import db.registration.Assay

class Experiment {

	String experimentName
	Date runDateFrom
	Date runDateTo
	Date holdUntilDate
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.experiment.ExperimentStatus experimentStatus
	db.experiment.Project project
	Assay assay

	static hasMany = [resultContextItems: db.experiment.ResultContextItem,
	                  results: db.experiment.Result]
	static belongsTo = [Assay, db.experiment.ExperimentStatus, db.experiment.Project]

	static mapping = {
		id column: "Experiment_ID"
	}

	static constraints = {
		experimentName maxSize: 256
        experimentStatus nullable: false
		runDateFrom nullable: true, maxSize: 10
		runDateTo nullable: true, maxSize: 10
		holdUntilDate nullable: true, maxSize: 10
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
