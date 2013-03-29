package bard.db.registration

import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem

class AssayContextItem extends AbstractContextItem<AssayContext> {

    AttributeType attributeType
    AssayContext assayContext

    static belongsTo = [assayContext: AssayContext]

    static mapping = {
        id(column: 'ASSAY_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'ASSAY_CONTEXT_ITEM_ID_SEQ'])
        valueElement(column: "value_id", fetch: 'join')
        attributeElement(column: "attribute_id", fetch: 'join')
        qualifier(column: "qualifier", sqlType: "char", length: 2)
    }

    static transients = ['context']

    @Override
    AssayContext getContext() {
        return assayContext
    }

    @Override
    void setContext(AssayContext context) {
        this.assayContext = context
    }
}
