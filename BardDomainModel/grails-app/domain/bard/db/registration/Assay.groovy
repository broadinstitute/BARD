package bard.db.registration

import bard.db.experiment.Experiment

class Assay {

    static expose = 'assay'

    String assayStatus = 'Pending'
    String assayName
    String assayVersion
    String designedBy
    String readyForExtraction = 'Ready'
    String assayType = 'Regular'

    String modifiedBy
    // grails auto-timestamp
    Date dateCreated
    Date lastUpdated



    static hasMany = [experiments: Experiment,
            measureContextItems: MeasureContextItem,
            measures: Measure,
            measureContexts: MeasureContext,
            assayDocuments: AssayDocument]

    static mapping = {
        id(column: "Assay_ID", generator: "sequence", params: [sequence: 'ASSAY_ID_SEQ'])

    }

    static constraints = {
        // TODO consider and Enum to replace the inListk
        assayStatus(maxSize: 20, blank: false, inList: ["Pending", "Active", "Superseded", "Retired"])
        assayName(maxSize: 1000, blank: false)
        assayVersion(maxSize: 10, blank: false, matches: /\d+/)   // does this need to look like a number
        designedBy(nullable: true, maxSize: 100)
        readyForExtraction(maxSize: 20, blank: false, inList: ["Ready", "Started", "Complete"])
        assayType(inList: ['Regular', 'Panel - Array', 'Panel - Group'])

        modifiedBy(nullable: true, maxSize: 40)
        dateCreated()
        lastUpdated(nullable: true) // TODO make this not nullable
    }
}
