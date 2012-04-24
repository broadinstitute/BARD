package bard.db.experiment

class Project {

    static expose = 'project'

	String projectName
	GroupType groupType
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy

	static hasMany = [experiments: Experiment,
	                  projectAssays: ProjectAssay]

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
