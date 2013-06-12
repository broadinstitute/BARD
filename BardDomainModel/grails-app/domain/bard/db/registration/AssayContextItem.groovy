package bard.db.registration

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

    def beforeDelete() {
        AssayContextItem.withNewSession {
            validateContextItemBeforeDelete(this)
        }
    }
    //If we have any experiments associated with an assay, we should prevent some classes of edits.
    // Specifically:
    //Users should not be able to delete any context items with
    // attribute type = free, list or range if there is one or
    // more experiments associated with the assay.

    //If the user attempts such a delete, we should report an error along the lines of,
    // "Cannot remove attribute which is specified as part of the experiment
    // while this assay has experiments."
    //see https://www.pivotaltracker.com/story/show/51248965
    static void validateContextItemBeforeDelete(AssayContextItem assayContextItem) {
        AssayContext assayContext = assayContextItem.assayContext
        final Assay assay = assayContext.assay

        if (assay.experiments) {
           rejectDeletionOfContextItem(assayContextItem)
        }
    }
    static void rejectDeletionOfContextItem(AssayContextItem assayContextItem){
        if (assayContextItem.attributeType == AttributeType.Free ||
                assayContextItem.attributeType == AttributeType.List ||
                assayContextItem.attributeType == AttributeType.Range) {
            assayContextItem.errors.rejectValue("attributeType",'assayContextItem.label.cannotdelete')
        }
    }

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
