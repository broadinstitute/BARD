package bard.db.guidance.context.item

import bard.db.guidance.DefaultGuidanceImpl
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceReporter
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import org.apache.commons.lang3.BooleanUtils

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/9/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
class ValidBiologyValueGuidanceRule implements GuidanceReporter {

    private static final List<String> VALID_BIOLOGY_VALUES = ['biological process',
            'molecular interaction',
            'molecular target',
            'macromolecule',
            'antibody',
            'gene',
            'nucleotide',
            'peptide',
            'protein-DNA complex',
            'protein-reporter complex',
            'protein']

    private static final String BIOLOGY_LABEL = 'biology'

    private static final String VALID_BIOLOGY_VALUE_MESSAGE = "A biology attribute should have one of the following (${VALID_BIOLOGY_VALUES.join(',')}) values"

    AbstractContext context

    ValidBiologyValueGuidanceRule(AbstractContext context) {
        this.context = context
    }

    @Override
    List<Guidance> getGuidance() {
        final List<Guidance> guidanceList = []
        for (AbstractContextItem aci in context.contextItems) {
            if (aci.attributeElement.label == BIOLOGY_LABEL) {
                if (BooleanUtils.isFalse(aci.valueElement?.label in VALID_BIOLOGY_VALUES)) {
                    guidanceList.add(new DefaultGuidanceImpl(VALID_BIOLOGY_VALUE_MESSAGE))
                }
            }
        }
        guidanceList
    }

}
