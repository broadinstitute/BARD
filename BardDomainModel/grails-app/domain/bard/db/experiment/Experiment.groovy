package bard.db.experiment

import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.enums.ReadyForExtraction

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
    ReadyForExtraction readyForExtraction = ReadyForExtraction.Pending
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20


    // TODO results can appearently be very large 10 million rows
    Set<Result> results = [] as Set<Result>
    Set<ExperimentContextItem> experimentContextItems = [] as Set<ExperimentContextItem>
    Set<ProjectStep> projectSteps = [] as Set<ProjectStep>
    Set<ExternalReference> externalReferences = [] as Set<ExternalReference>

	static hasMany = [experimentContextItems: ExperimentContextItem,
			results: Result,
            projectSteps:ProjectStep,
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
        readyForExtraction(maxSize: READY_FOR_EXTRACTION_MAX_SIZE, nullable: false)
    }
}
