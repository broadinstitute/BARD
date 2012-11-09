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

    String modifiedBy
    // grails auto-timestamp
    Date dateCreated
    Date lastUpdated

    static belongsTo = [assayContext: AssayContext]
    static constraints = {
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }


}
