package db.experiment

class ExperimentStatus {

	String status
	String capability
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [experiments: db.experiment.Experiment]

	static mapping = {
		id column: "Experiment_Status_ID"
	}

	static constraints = {
		status maxSize: 20
		capability nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
