package db.experiment

class Project {

    static expose = 'project'

	String projectName
	db.experiment.GroupType groupType
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [experiments: db.experiment.Experiment,
	                  projectAssays: db.experiment.ProjectAssay]

	static mapping = {
		id column: "Project_ID"
	}

	static constraints = {
		projectName maxSize: 256
		groupType maxSize: 20
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
