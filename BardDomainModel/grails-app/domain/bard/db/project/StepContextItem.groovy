package bard.db.project

import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:03 AM
 * To change this template use File | Settings | File Templates.
 */
class StepContextItem extends AbstractContextItem {

    StepContext stepContext

    static belongsTo = [stepContext: StepContext]

    static mapping = {
        table('STEP_CONTEXT_ITEM')
        id(column: 'STEP_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'STEP_CONTEXT_ITEM_ID_SEQ'])
        valueElement(column: "value_id", fetch: 'join')
        attributeElement(column: "attribute_id", fetch: 'join')
        qualifier(column: "qualifier", sqlType: "char", length: 2)
    }

    static transients = ['context']

    @Override
    AbstractContext getContext() {
        return stepContext
    }

    @Override
    void setContext(AbstractContext context) {
        this.context = context
    }
}
