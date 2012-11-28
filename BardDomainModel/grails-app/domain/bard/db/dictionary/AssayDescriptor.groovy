package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 9/25/12
 * Time: 10:01 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayDescriptor extends Descriptor<AssayDescriptor> {

    static mapping = {
        table('ASSAY_DESCRIPTOR_TREE')
        id(column: 'NODE_ID', generator: 'assigned')
        version(false)
        leaf(column: 'IS_LEAF', type: 'yes_no')
        bardURI(column: 'BARD_URI')
        externalURL(column: 'EXTERNAL_URL')
        parent(column: 'PARENT_NODE_ID')
    }

}