package bard.db.registration

import bard.db.model.AbstractContextItem
import org.springframework.validation.Errors

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

//    @Override
//    protected void valueValidation(Errors errors) {
//        if (attributeElement) {
//            switch (attributeType) {
//                case AttributeType.Free:
//                    allNullValueConstraints(errors)
//                    break;
//                case AttributeType.Range:
//                    rangeContraints(errors)
//                    break
//                case AttributeType.Fixed:
//                case AttributeType.List:
//                    super.valueValidation(errors, )
//                    break
//                default:
//                    throw new RuntimeException("unknow attributeType: $attributeType")
//                    break
//            }
//        }
//    }

    protected allNullValueConstraints(Errors errors) {
        if (rejectNotNullFields(['valueDisplay', 'extValueId', 'valueElement', 'qualifier', 'valueNum', 'valueMin', 'valueMax'])) {
            errors.reject('assayContextItem.attributeType.required.fields')
        }
    }
}
