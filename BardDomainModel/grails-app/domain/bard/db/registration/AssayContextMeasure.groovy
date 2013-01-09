package bard.db.registration

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/9/12
 * Time: 11:58 AM
 * To change this template use File | Settings | File Templates.
 */
class AssayContextMeasure {


    private static final int MODIFIED_BY_MAX_SIZE = 40

    AssayContext assayContext
    Measure measure

    Date dateCreated = new Date()
    // grails auto-timestamp
    Date lastUpdated
    String modifiedBy

    static belongsTo = [assayContext: AssayContext]

    static mapping = {
        id(column: 'ASSAY_CONTEXT_MEASURE_ID', generator: 'sequence', params: [sequence: 'ASSAY_CONTEXT_MEASURE_ID_SEQ'])
        assayContext(lazy: false)
        measure(lazy: false)
    }
    static constraints = {
        assayContext(nullable: false)
        measure(nullable: false)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }


}
