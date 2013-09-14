package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 1/17/13
 * Time: 1:01 PM
 * To change this template use File | Settings | File Templates.
 */
class BardDescriptor extends Descriptor<BardDescriptor> {
    public static String ROOT_PREFIX = "BARD> "

    Set<BardDescriptor> children = [] as Set<BardDescriptor>

    static mapping = {
        table('BARD_TREE')
        id(column: 'NODE_ID', generator: 'assigned')
        version(false)
        leaf(column: 'IS_LEAF', type: 'yes_no')
        externalURL(column: 'EXTERNAL_URL')
        parent(column: 'PARENT_NODE_ID')
        fullPath(column: 'FULL_PATH')

    }

    static hasMany = [children: BardDescriptor]

    String getDisplayPath() {
        if (fullPath.startsWith(ROOT_PREFIX)) {
            return fullPath.substring(ROOT_PREFIX.length())
        }
        return fullPath
    }

    List<BardDescriptor> getDescendents() {
        List<BardDescriptor> descendants = []
        for (BardDescriptor child in children) {
            descendants.add(child)
            descendants.addAll(child.getDescendents())
        }
        descendants
    }

    List<BardDescriptor> getDescendentsAndSelf() {
        List<BardDescriptor>  descendentsAndSelf = [this]
        descendentsAndSelf.addAll(getDescendents())
        descendentsAndSelf
    }
}
