package bard.db

class ExperimentStatus {

	String status
	String capability

	static hasMany = [experiments: Experiment]

	static mapping = {
		id column: "Experiment_Status_ID"
		version false
	}

	static constraints = {
		status maxSize: 20
		capability nullable: true, maxSize: 1000
	}
}
