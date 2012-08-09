package bard.db.experiment

import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.dictionary.Laboratory

class Experiment {

	String experimentName
	Date runDateFrom
	Date runDateTo
	Date holdUntilDate
	String description
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	String experimentStatus
	Assay assay
	String readyForExtraction = 'Pending'
    Laboratory laboratory

	static hasMany = [resultContextItems: ResultContextItem,
			results: Result,
            projectExperiments:ProjectExperiment,
            externalReferences:ExternalReference]
	static belongsTo = [Assay]

	static mapping = {
		id column: "Experiment_ID", generator: "assigned"
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
		experimentStatus maxSize: 20, nullable: false, inList: ["Pending", "Approved", "Rejected", "Revised"]
		readyForExtraction maxSize: 20, nullable: false, inList: [ "Pending","Ready", "Started", "Complete" ]
        laboratory nullable: true
	}
}
