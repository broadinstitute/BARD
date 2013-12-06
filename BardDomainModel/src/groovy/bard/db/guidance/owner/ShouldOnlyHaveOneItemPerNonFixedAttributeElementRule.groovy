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
class ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule implements GuidanceRule {

    final Assay assay
    final AssayContext assayContext

    ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule(Assay assay) {
        this.assay = assay
    }

    ShouldOnlyHaveOneItemPerNonFixedAttributeElementRule(AssayContext assayContext) {
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
            if (nonFixedItems.size() > 1) {
                if (this.assayContext == null) { // report assay wide issue
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
        "The attribute '${attributeLabel}' should only appear in one context item for any value the will be provided with an experiment."
    }
}
