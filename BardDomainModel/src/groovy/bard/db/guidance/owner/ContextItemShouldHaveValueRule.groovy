package bard.db.guidance.owner

import bard.db.dictionary.Element
import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceRule
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType

/**
 * Created by ddurkin on 1/28/14.
 */
class ContextItemShouldHaveValueRule implements GuidanceRule {

    private final Assay assay

    private final AssayContext assayContext

    ContextItemShouldHaveValueRule(Assay assay) {
        this.assay = assay
    }

    ContextItemShouldHaveValueRule(AssayContext assayContext) {
        this.assayContext = assayContext
    }

    @Override
    List<Guidance> getGuidance() {
        final List<Guidance> guidanceList = []
        for (AssayContextItem item : getContextItems()) {
            if (isFixedAndAllValuesNull(item)) {
                guidanceList.add(new DefaultGuidanceImpl(getErrorMsg(item.attributeElement.label)))
            }
        }
        return guidanceList
    }

    private List<AssayContextItem> getContextItems() {
        final List<AssayContextItem> contextItems = []
        if (assay) {
            contextItems.addAll(assay.contexts.assayContextItems.flatten())
        } else {
            contextItems.addAll(assayContext.assayContextItems)
        }
        contextItems
    }

    public static String getErrorMsg(String attributeLabel) {
        "Please specify a value for '${attributeLabel}'."
    }

    public static boolean isFixedAndAllValuesNull(AssayContextItem item) {
        item.attributeType==AttributeType.Fixed && item.allValueColumnsAreNull()
    }
}
