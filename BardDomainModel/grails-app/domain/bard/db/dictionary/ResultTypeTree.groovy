package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/27/12
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
class ResultTypeTree {

    private static final int FULL_PATH_MAX_SIZE = 3000

    ResultTypeTree parent
    Element element
    Boolean leaf
    String fullPath
    Element baseUnit

    static constraints = {
        parent(nullable: true)
        element()
        leaf()
        fullPath(nullable: true, blank: false, maxSize: FULL_PATH_MAX_SIZE)
        baseUnit(nullable:true)
    }

    static mapping = {
        id(column: 'NODE_ID', generator: 'assigned')
        version(false)
        parent(column: 'PARENT_NODE_ID')
        element(column: 'RESULT_TYPE_ID')
        leaf(column: 'IS_LEAF', type: 'yes_no')

    }

}
