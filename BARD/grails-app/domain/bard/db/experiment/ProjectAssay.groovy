package bard.db.experiment

import bard.db.registration.Assay

class ProjectAssay {

    static expose = 'project-assay'

	Stage stage
	Integer sequenceNo
	Float promotionThreshold
	String promotionCriteria
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	Project project
	Assay assay

	static belongsTo = [Assay, Project]

	static mapping = {
        stage column: "stage"
	}

	static constraints = {
		sequenceNo nullable: true
		promotionThreshold nullable: true
		promotionCriteria nullable: true, maxSize: 1000
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
        project nullable: false
        assay nullable: false
	}
}
