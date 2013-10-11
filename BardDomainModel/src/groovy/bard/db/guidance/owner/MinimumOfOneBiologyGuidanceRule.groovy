package bard.db.guidance.owner

import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceRule
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner
import groovy.transform.TypeChecked

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 2:42 PM
 *
 * Assay should have at least 1 context that defines biology, they can have more than 1 but need at least 1
 */
class MinimumOfOneBiologyGuidanceRule implements GuidanceRule {

    private static final String BIOLOGY_LABEL = 'biology'
    private static final String ONE_BIOLOGY_ATTRIBUTE_REQUIRED = "There should be at least 1 context with an item where the attribute is 'biology'."
    AbstractContextOwner owner

    MinimumOfOneBiologyGuidanceRule(AbstractContextOwner owner) {
        this.owner = owner
    }

    @Override
    @TypeChecked
    List<Guidance> getGuidance() {
        final List<Guidance> guidance = []
//        List<AbstractContextItem> itemsWithAttributeOfBiology = []
//        for (AbstractContext context in owner.contexts) {
//            for (AbstractContextItem item in context.contextItems) {
//                if (item.attributeElement.label == BIOLOGY_LABEL) {
//                    itemsWithAttributeOfBiology.add(item)
//                }
//            }
//        }
//        if (itemsWithAttributeOfBiology.isEmpty()) {
//            guidance.add(new DefaultGuidanceImpl(ONE_BIOLOGY_ATTRIBUTE_REQUIRED))
//        }
        guidance
    }
}
