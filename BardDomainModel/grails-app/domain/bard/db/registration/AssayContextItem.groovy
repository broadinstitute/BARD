package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.ExpectedValueType
import bard.db.enums.Status
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

    //If we have any experiments associated with an assay, we should prevent some classes of edits.
    // Specifically:
    //Users should not be able to delete any context items with
    // attribute type = free, list or range if there is one or
    // more experiments associated with the assay.

    //If the user attempts such a delete, we should report an error along the lines of,
    // "Cannot remove attribute which is specified as part of the experiment
    // while this assay has experiments."
    //see https://www.pivotaltracker.com/story/show/51248965
    static boolean canDeleteContextItem(AssayContextItem assayContextItem) {

        AssayContext assayContext = assayContextItem.assayContext ?: AssayContextItem.findById(assayContextItem.id).assayContext
        final Assay assay = assayContext.assay

        if (assay.experiments) {
            return safeToDeleteContextItem(assayContextItem)
        }
        return true
    }
    /**
     * Allowing user to delete duplicate non Fixed items, so Experiment time reported values if multiple items for a given
     * attributeElement exist.  If there's only 1, than no delete option presented.
     *
     * <a href="https://www.pivotaltracker.com/story/show/60861600">see pivotal story that caused latest change</a>
     *
     * @param assayContextItem the item to potentially allow delete
     * @return true for Fixed items or true if there are more than 1 Free, Range or List items for a given attributeElement
     */
    protected static boolean safeToDeleteContextItem(AssayContextItem assayContextItem) {
        if (assayContextItem.attributeType == AttributeType.Fixed) {
            return true
        } else {  // all the non Fixed, Experiment time constraint items
            final ArrayList<AssayContextItem> allItems = assayContextItem.assayContext.assay.assayContextItems
            final ArrayList<AssayContextItem> nonFixedItems = allItems.findAll { AssayContextItem aci -> aci.attributeType != AttributeType.Fixed }
            final Map<Element, List<AssayContextItem>> attributeElementToItemMap = nonFixedItems.groupBy { AssayContextItem aci -> aci.attributeElement }
            return attributeElementToItemMap.get(assayContextItem.attributeElement).size() > 1
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
        //Do not validate context items if this is false
        if (!this?.assayContext?.assay?.fullyValidateContextItems) {
            return
        }
        if (attributeElement) {
            // special condition to allow for creating assays with required contexts and context items
            // as long as all the value columns are null we'll skip any of the standard validation rules
            // if any of the value columns are non null than all the standard validation rules apply
            if (assayContext.assay.assayStatus != Status.APPROVED && valueType == valueType.NONE && allValueColumnsAreNull()) {
                return
            }
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
                    freeTypeConstraints(errors)
                    break
                default:
                    throw new RuntimeException("Unknown attributeType: $attributeType")
                    break
            }
        }
    }

    public boolean allValueColumnsAreNull() {
        [valueElement, extValueId, qualifier, valueNum, valueMin, valueMax, valueDisplay].every{ it == null}
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
