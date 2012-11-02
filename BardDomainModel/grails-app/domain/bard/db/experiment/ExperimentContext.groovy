package bard.db.experiment

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/1/12
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentContext {
    private static final int CONTEXT_NAME_MAX_SIZE = 128
    private static final int CONTEXT_GROUP_MAX_SIZE = 256
    private static final int MODIFIED_BY_MAX_SIZE = 40

    Experiment experiment
    String contextName
    String contextGroup

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

//    List<ExperimentContextItem> experimentContextItems = []

    static belongsTo = [experiment: Experiment]

//    static hasMany = [experimentContextItems: ExperimentContextItem]

    static mapping = {
        table('EXPRMT_CONTEXT')
        id(column: "EXPRMT_CONTEXT_ID", generator: "sequence", params: [sequence: 'EXPRMT_CONTEXT_ID_SEQ'])
//        experimentContextItems(indexColumn: [name: 'DISPLAY_ORDER'], lazy: 'false')
    }

    static constraints = {
        experiment()
        contextName(nullable: true, blank: false, maxSize: CONTEXT_NAME_MAX_SIZE)
        contextGroup(nullable: true, blank: false, maxSize: CONTEXT_GROUP_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

}
