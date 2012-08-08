package bard.db.registration

import bard.db.experiment.Experiment

class Assay {

    static expose = 'assay'

    private static final int ASSAY_STATUS_MAX_SIZE = 20
    private static final int ASSAY_NAME_MAX_SIZE = 1000
    private static final int ASSAY_VERSION_MAX_SIZE = 10
    private static final int DESIGNED_BY_MAX_SIZE = 100
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40

    String assayStatus = 'Pending'
    String assayName
    String assayVersion
    String designedBy
    String readyForExtraction = 'Pending'
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
        assayStatus(maxSize: ASSAY_STATUS_MAX_SIZE, blank: false, inList: ["Pending", "Active", "Superseded", "Retired"])
        assayName(maxSize: ASSAY_NAME_MAX_SIZE, blank: false)
        assayVersion(maxSize: ASSAY_VERSION_MAX_SIZE, blank: false, matches: /\d+/)   // does this need to look like a number
        designedBy(nullable: true, maxSize: DESIGNED_BY_MAX_SIZE)
        readyForExtraction(maxSize: READY_FOR_EXTRACTION_MAX_SIZE, blank: false, inList: ["Pending","Ready", "Started", "Complete"])
        assayType(inList: ['Regular', 'Panel - Array', 'Panel - Group'])

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
