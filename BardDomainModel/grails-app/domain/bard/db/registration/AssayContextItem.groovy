package bard.db.registration

import bard.db.enums.ExpectedValueType
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
                case AttributeType.Fixed: // so with Fixed and List all the standard validations apply
                case AttributeType.List:
                    super.valueValidation(errors, true)
                    break
                case AttributeType.Range: // with Range a specified range is the only valid state
                    if (attributeElement.expectedValueType == ExpectedValueType.NUMERIC) {
                        rangeConstraints(errors)
                    } else {
                        errors.reject('assayContextItem.invalid.attributeTypeAndAttributeExpectedValueCombo')
                    }
                    break
                case AttributeType.Free:
                    if (attributeElement.expectedValueType in [ExpectedValueType.NUMERIC, ExpectedValueType.FREE_TEXT]) {
                        freeTypeConstraints(errors)
                    } else {
                        errors.reject('assayContextItem.invalid.attributeTypeAndAttributeExpectedValueCombo')
                    }
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
