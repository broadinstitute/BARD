package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/29/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
class BardDictionaryDescriptor extends Descriptor<BardDictionaryDescriptor> {
    public static String ROOT_PREFIX = "BARD Dictionary > "

    static mapping = {
        table('DICTIONARY_TREE')
        id(column: 'NODE_ID', generator: 'assigned')
        version(false)
        leaf(column: 'IS_LEAF', type: 'yes_no')
        externalURL(column: 'EXTERNAL_URL')
        parent(column: 'PARENT_NODE_ID')
        fullPath(column: 'FULL_PATH')
    }

    String getDisplayPath() {
        if (fullPath.startsWith(ROOT_PREFIX)) {
            return fullPath.substring(ROOT_PREFIX.length())
        }
        return fullPath
    }
}
