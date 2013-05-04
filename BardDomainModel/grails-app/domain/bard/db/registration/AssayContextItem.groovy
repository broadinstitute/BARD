package bard.db.registration

import bard.db.dictionary.Element
import bard.db.model.AbstractContextItem
import groovy.transform.TypeChecked
import org.springframework.validation.Errors

class AssayContextItem extends AbstractContextItem<AssayContext> {

    AttributeType attributeType = AttributeType.Fixed
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

    @Override
    @TypeChecked
    protected void valueValidation(Errors errors) {
        if (attributeElement) {
            switch (attributeType) {
                case AttributeType.Free:
                    freeTypeConstraints(errors)
                    break;
                case AttributeType.Range:
                    rangeConstraints(errors)
                    break
                case AttributeType.Fixed:
                case AttributeType.List:
                    super.valueValidation(errors, false)
                    break
                default:
                    throw new RuntimeException("unknow attributeType: $attributeType")
                    break
            }
        }
    }

    protected freeTypeConstraints(Errors errors) {
        if (rejectNotNullFields(['valueDisplay', 'extValueId', 'valueElement', 'qualifier', 'valueNum', 'valueMin', 'valueMax'], errors)) {
            errors.reject('assayContextItem.attributeType.free.required.fields')
        }
    }

    public AssayContextItem clone(AssayContext newContext) {
        AssayContextItem newItem = new AssayContextItem(
                attributeType: attributeType,
                attributeElement: attributeElement,
                valueElement: valueElement,
                extValueId: extValueId,
                qualifier: qualifier,
                valueNum: valueNum,
                valueMin: valueMin,
                valueMax: valueMax,
                valueDisplay: valueDisplay)
        newContext.addToAssayContextItems(newItem)
        return newItem;
    }

}
