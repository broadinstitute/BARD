package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/27/12
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
class UnitTree {

    private static final int LABEL_MAX_SIZE = 128
    private static final int ABBREVIATION_MAX_SIZE = 20
    private static final int FULL_PATH_MAX_SIZE = 2000
    private static final int DESCRIPTION_MAX_SIZE = 1000

    UnitTree parent
    Element element
    String label
    String abbreviation
    String description
    Boolean leaf
    String fullPath

    static constraints = {
        parent(nullable: true)
        element()
        label(blank: false, maxSize: LABEL_MAX_SIZE)
        abbreviation(nullable: true, maxSize: ABBREVIATION_MAX_SIZE)
        description(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        leaf()
        fullPath(nullable: true, blank: false, maxSize: FULL_PATH_MAX_SIZE)
    }

    static mapping = {
        id(column: 'NODE_ID', generator: 'assigned')
        version(false)
        parent(column: 'PARENT_NODE_ID')
        element(column: 'UNIT_ID')
        label(column: 'UNIT')
        leaf(column: 'IS_LEAF', type: 'yes_no')
    }


}
