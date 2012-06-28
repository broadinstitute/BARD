package bard.db.registration

import bard.db.experiment.Experiment

class Assay {

    static expose = 'assay'

	String assayName
	String assayVersion
	//String description
	String designedBy
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	String assayStatus = 'Pending'
    String assayType = 'Regular'
	String readyForExtraction = 'Ready'


	static hasMany = [experiments: Experiment,
			measureContextItems: MeasureContextItem,
			measures : Measure,
			measureContexts: MeasureContext,
			assayDocuments: AssayDocument]

	static mapping = {
		id( column: "Assay_ID", generator: "sequence", params: [sequence:'ASSAY_ID_SEQ'])
	}

	static constraints = {
		assayName nullable: false, maxSize: 128
		assayVersion maxSize: 10
		//description nullable: true, maxSize: 1000
		designedBy nullable: true, maxSize: 100
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
		assayStatus maxSize: 20, nullable: false, inList: ["Pending", "Active", "Superseded", "Retired"]
        assayType inList: ['Regular', 'Panel - Array', 'Panel - Group']
		readyForExtraction maxSize: 20, nullable: false, inList: [ "Ready", "Started", "Complete" ]
	}
}
