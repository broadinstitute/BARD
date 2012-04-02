package bard.db

class Project {

	String projectName
	String groupType
	String description

	static hasMany = [experiments: Experiment,
	                  projectAssaies: ProjectAssay]

	static mapping = {
		id column: "Project_ID"
		version false
	}

	static constraints = {
		projectName maxSize: 256
		groupType maxSize: 20
		description maxSize: 1000
	}
}
