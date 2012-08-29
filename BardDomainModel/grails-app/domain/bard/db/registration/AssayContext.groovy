package bard.db.registration

class AssayContext {

    private static final int CONTEXT_NAME_MAX_SIZE = 128
    private static final int MODIFIED_BY_MAX_SIZE = 40

    String contextName
    Assay assay

    Set<Measure> measures = [] as Set<Measure>
    List<AssayContextItem> assayContextItems = []

    Date dateCreated
    Date lastUpdated
    String modifiedBy

    static belongsTo = [assay: Assay]

    static hasMany = [assayContextItems: AssayContextItem,
            measures: Measure]

    static mapping = {
        sort("ASSAY_CONTEXT_ID") // default sort order
        id(column: "ASSAY_CONTEXT_ID", generator: "sequence", params: [sequence: 'ASSAY_CONTEXT_ID_SEQ'])
        assayContextItems(indexColumn: [name: 'display_order'])
    }

    static constraints = {
        contextName(maxSize: CONTEXT_NAME_MAX_SIZE, blank: false)
        assay()
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
}
