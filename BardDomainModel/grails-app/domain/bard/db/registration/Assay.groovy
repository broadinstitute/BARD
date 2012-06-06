package bard.db.registration

import bard.db.experiment.Experiment

import bard.db.experiment.ProjectAssay

class Assay {

    static expose = 'assay'

    String assayName
    String assayVersion
    String description
    String designedBy
    Date dateCreated
    Date lastUpdated
    String modifiedBy
    String assayStatus = 'Pending'
    String readyForExtraction = 'Ready'


    static hasMany = [experiments: Experiment,
            externalAssays: ExternalAssay,
            measureContextItems: MeasureContextItem,
            measureContexts: MeasureContext,
            projectAssays: ProjectAssay,
            protocols: Protocol]

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
        assayStatus maxSize: 20, nullable: false, inList: ["Pending", "Active", "Superseded", "Retired"]
        readyForExtraction maxSize: 20, nullable: false, inList: [ "Ready", "Started", "Complete" ]
    }
}
