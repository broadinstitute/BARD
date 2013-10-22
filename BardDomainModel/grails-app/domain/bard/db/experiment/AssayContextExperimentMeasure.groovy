package bard.db.experiment

import bard.db.registration.AssayContext

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/9/12
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextExperimentMeasure {


    private static final int MODIFIED_BY_MAX_SIZE = 40

    AssayContext assayContext
    ExperimentMeasure experimentMeasure

    Date dateCreated = new Date()
    // grails auto-timestamp
    Date lastUpdated
    String modifiedBy

    static belongsTo = [assayContext: AssayContext]

    static mapping = {
        table('ASSAY_CTXT_EXP_MEASURE')
        id(column: 'ASSAY_CTXT_EXP_MEASURE_ID', generator: 'sequence', params: [sequence: 'ASSAY_CTXT_EXP_MEASURE_ID_SEQ'])
    }
    static constraints = {
        assayContext(nullable: false)
        experimentMeasure(nullable: false)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }


}
