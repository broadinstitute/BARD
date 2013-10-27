package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/27/12
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
class StatsModifierTree {

    private static final int ELEMENT_STATUS_MAX_SIZE = 20
    private static final int LABEL_MAX_SIZE = 128
    private static final int FULL_PATH_MAX_SIZE = 3000
    private static final int DESCRIPTION_MAX_SIZE = 1000

    StatsModifierTree parent
    Element element
    Boolean leaf
    String fullPath
    String label
    String elementStatus
    String description

    static constraints = {
        parent(nullable: true)
        element()
        leaf()
        fullPath(nullable: true, blank: false, maxSize: FULL_PATH_MAX_SIZE)
        elementStatus(maxSize: ELEMENT_STATUS_MAX_SIZE)
        label(maxSize: LABEL_MAX_SIZE)
        description(nullable: true, blank: false, maxSize: DESCRIPTION_MAX_SIZE)
    }

    static mapping = {
        id(column: 'NODE_ID', generator: 'assigned')
        version(false)
        fullPath(column: 'FULL_PATH')
        parent(column: 'PARENT_NODE_ID')
        element(column: 'ELEMENT_ID')
        leaf(column: 'IS_LEAF', type: 'yes_no')
        elementStatus(column: 'ELEMENT_STATUS')
        label(column: 'LABEL')
    }

}
