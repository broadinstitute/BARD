package bard.db.guidance.owner

import bard.db.dictionary.Element
import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceRule
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType
import groovy.transform.TypeChecked

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 12/4/13
 * Time: 10:55 AM
 * An assay definition should only be allowed to have a single context item per attributeElement that has an attributeType != Fixed
 */
class OneItemPerNonFixedAttributeElementRule implements GuidanceRule {

    final Assay assay
    final AssayContext assayContext

    OneItemPerNonFixedAttributeElementRule(Assay assay) {
        this.assay = assay
    }

    OneItemPerNonFixedAttributeElementRule(AssayContext assayContext) {
        this.assayContext = assayContext
        this.assay = assayContext.assay
    }

    @Override
    List<Guidance> getGuidance() {
        final List<Guidance> guidanceList = []
        final List<AssayContextItem> items = this.assay.contexts.assayContextItems.flatten()
        final Map attributeToItemsMap = items.groupBy { it.attributeElement }

        attributeToItemsMap.each { Element attribute, List<AssayContextItem> itemsForAttribute ->
            final ArrayList<AssayContextItem> nonFixedItems = itemsForAttribute.findAll { it.attributeType != AttributeType.Fixed }
            final String attributeLabel = attribute.label
            final Map<AttributeType,List<AssayContextItem>> attributeTypeToItemsMap = nonFixedItems.groupBy {AssayContextItem aci -> aci.attributeType}
            if (nonFixedItems.size() > 1) {
                if(attributeTypeToItemsMap.containsKey(AttributeType.List) && attributeTypeToItemsMap.size() == 1 ){
                   // multiple List items are ok as long as there are only List items no Range or Free items
                }
                else if (this.assayContext == null) { // report assay wide issue
                    guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(attributeLabel)))
                } else { // only report if an item within this assayContext contains one an offending item
                    if (this.assayContext.contextItems.any { nonFixedItems.contains(it) }) {
                        guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(attributeLabel)))
                    }
                }
            }
        }
        return guidanceList
    }


    public static String getErrorMsg(String attributeLabel) {
        "The attribute '${attributeLabel}' should only appear in one context item for any value that will be provided with an experiment."
    }
}
