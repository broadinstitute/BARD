package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 2/5/13
 * Time: 1:24 PM
 * To change this template use File | Settings | File Templates.
 */
class StageDescriptor extends Descriptor<StageDescriptor> {
    public static String ROOT_PREFIX = "assay stage> "

    static mapping = {
        table('STAGE_TREE')
        id(column: 'NODE_ID', generator: 'assigned')
        version(false)
        leaf(column: 'IS_LEAF', type: 'yes_no')
        externalURL(column: 'EXTERNAL_URL')
        parent(column: 'PARENT_NODE_ID')
        fullPath(column: 'FULL_PATH')
        label(column: 'STAGE')
    }

    String getDisplayPath() {
        if (fullPath.startsWith(ROOT_PREFIX)) {
            return fullPath.substring(ROOT_PREFIX.length())
        }
        return fullPath
    }
}
