package bard.db.experiment

import bard.db.model.AbstractContextItem
import grails.validation.Validateable

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
@Validateable
class ResultContextItem extends AbstractContextItem {

    Result result
    Long id;

//    static belongsTo = [result: Result]
//    static mapping = {
//        table('RSLT_CONTEXT_ITEM')
//        id(column: 'RSLT_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'RSLT_CONTEXT_ITEM_ID_SEQ'])
//        valueElement(column: "value_id", fetch: 'join')
//        attributeElement(column: "attribute_id", fetch: 'join')
//        qualifier(column: "qualifier", sqlType: "char", length: 2)
//    }
}
