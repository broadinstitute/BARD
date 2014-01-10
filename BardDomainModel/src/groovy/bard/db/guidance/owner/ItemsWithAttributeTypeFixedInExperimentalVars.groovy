package bard.db.guidance.owner

import bard.db.dictionary.Element
import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.model.AbstractContextOwner.ContextGroup
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType

/**
 * Created with IntelliJ IDEA.
 * User: ycruz
 * Date: 1/8/14
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
class ItemsWithAttributeTypeFixedInExperimentalVars implements GuidanceRule  {

    final Assay assay
    final AssayContext assayContext

    ItemsWithAttributeTypeFixedInExperimentalVars(Assay assay) {
        this.assay = assay
    }

    ItemsWithAttributeTypeFixedInExperimentalVars(AssayContext assayContext) {
        this.assayContext = assayContext
        this.assay = assayContext.assay
    }

    List<Guidance> getGuidance() {

        final List<Guidance> guidanceList = []
        final List<AssayContextItem> items = this.assay.groupExperimentalVariables().value.assayContextItems.flatten()
        final Map attributeToItemsMap = items.groupBy { it.attributeElement }

        attributeToItemsMap.each { Element attribute, List<AssayContextItem> itemsForAttribute ->
            final ArrayList<AssayContextItem> fixedItems = itemsForAttribute.findAll { it.attributeType == AttributeType.Fixed }
            final String attributeLabel = attribute.label
            final Map<AttributeType,List<AssayContextItem>> attributeTypeToItemsMap = fixedItems.groupBy {AssayContextItem aci -> aci.attributeType}
            if (fixedItems.size() > 0) {
                if (this.assayContext.contextItems.any { fixedItems.contains(it) }) {
                    guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(attributeLabel)))
                }
            }
        }
        return guidanceList
    }

    public static String getErrorMsg(String attributeLabel) {
        "For attribute '${attributeLabel}', value in this section should be provided as part of result upload, not statically defined."
    }
}
