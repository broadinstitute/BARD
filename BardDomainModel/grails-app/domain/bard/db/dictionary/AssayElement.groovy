package bard.db.dictionary

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/22/12
 * Time: 2:58 PM
 * To change this template use File | Settings | File Templates.
 */
class AssayElement extends AbstractElement {
    static mapping = {
        id(column: 'ELEMENT_ID', generator: 'sequence', params: [sequence: 'ELEMENT_ID_SEQ'])
        externalURL(column: 'external_url')
    }
}
