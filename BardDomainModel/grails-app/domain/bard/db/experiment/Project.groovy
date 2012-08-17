package bard.db.experiment

import bard.db.registration.ExternalReference

class Project {

	String projectName
	GroupType groupType
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	String readyForExtraction = 'Pending'

	static hasMany = [projectExperiments: ProjectExperiment, externalReferences:ExternalReference]

	static mapping = {
        id(column: "PROJECT_ID", generator: "sequence", params: [sequence: 'PROJECT_ID_SEQ'])
	}

	static constraints = {
		projectName maxSize: 256
		groupType maxSize: 20
		description nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
		readyForExtraction maxSize: 20, nullable: false, inList: [ "Pending","Ready", "Started", "Complete" ]
	}
}
