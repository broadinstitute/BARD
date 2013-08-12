package bard.db.guidance.context

import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceReporter
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 2:57 PM
 * For any given Biology Context, there should be
 */
class OneBiologyAttributePerContextRule implements GuidanceReporter {

    private static String BIOLOGY_LABEL = 'biology'
    private static final String ONE_BIOLOGY_ATTRIBUTE = "A Context should only have a single biology attribute."

    AbstractContext context

    OneBiologyAttributePerContextRule(AbstractContext context) {
        this.context = context
    }

    @Override
    List<Guidance> getGuidance() {
        List<Guidance> guidanceList = []
        final List<AbstractContextItem> biologyAttributes = context.contextItems.findAll { AbstractContextItem item -> item.attributeElement.label == BIOLOGY_LABEL }
        if (biologyAttributes && biologyAttributes.size() > 1) {
            guidanceList.add(new DefaultGuidanceImpl(ONE_BIOLOGY_ATTRIBUTE))
        }
        return guidanceList
    }
}
