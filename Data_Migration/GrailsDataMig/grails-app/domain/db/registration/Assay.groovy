package db.registration

import db.experiment.Experiment
import db.experiment.ProjectAssay

class Assay {

    static expose = 'assay'

	String assayName
	String assayVersion
	String description
	String designedBy
	Date dateCreated
	Date lastUpdated
	String modifiedBy
	db.registration.AssayStatus assayStatus

	static hasMany = [experiments: Experiment,
	                  externalAssays: db.registration.ExternalAssay,
	                  measureContextItems: db.registration.MeasureContextItem,
	                  measureContexts: db.registration.MeasureContext,
	                  projectAssays: ProjectAssay,
	                  protocols: db.registration.Protocol]

	static mapping = {
		id column: "Assay_ID"
	}

	static constraints = {
		assayName nullable: false, maxSize: 128
		assayVersion maxSize: 10
		description nullable: true, maxSize: 1000
		designedBy nullable: true, maxSize: 100
		dateCreated maxSize: 19
		lastUpdated nullable: true, maxSize: 19
		modifiedBy nullable: true, maxSize: 40
	}
}
