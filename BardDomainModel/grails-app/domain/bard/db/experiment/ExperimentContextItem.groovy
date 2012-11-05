package bard.db.experiment

import bard.db.registration.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentContextItem extends AbstractContextItem {

    ExperimentContext experimentContext

    static belongsTo = [experimentContext: ExperimentContext]

    static mapping = {
        id(column: 'EXPRMT_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'EXPRMT_CONTEXT_ITEM_ID_SEQ'])
        experimentContext(column: 'EXPRMT_CONTEXT_ID')
        attributeElement(column: 'ATTRIBUTE_ID')
        valueElement(column: 'VALUE_ID')
        qualifier(column: "QUALIFIER", sqlType: "char", length: 2)
    }
}
