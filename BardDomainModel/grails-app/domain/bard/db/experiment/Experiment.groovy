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

    Set<Result> results = [] as Set<Result>
    Set<ExperimentContextItem> experimentContextItem = [] as Set<ExperimentContextItem>
    Set<ProjectExperiment> projectExperiments = [] as Set<ProjectExperiment>
    Set<ExternalReference> externalReferences = [] as Set<ExternalReference>

	static hasMany = [experimentContextItem: ExperimentContextItem,
			results: Result,
            projectExperiments:ProjectExperiment,
            externalReferences:ExternalReference]

	static belongsTo = [Assay]

	static mapping = {
        id(column: "EXPERIMENT_ID", generator: "sequence", params: [sequence: 'EXPERIMENT_ID_SEQ'])
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
