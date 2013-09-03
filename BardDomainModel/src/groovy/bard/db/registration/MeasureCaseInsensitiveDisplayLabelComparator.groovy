package bard.db.registration

import bard.db.experiment.ExperimentMeasure
import org.apache.commons.lang.builder.CompareToBuilder

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/16/13
 * Time: 12:22 PM
 * To change this template use File | Settings | File Templates.
 */
class MeasureCaseInsensitiveDisplayLabelComparator implements Comparator<ExperimentMeasure> {
    int compare(ExperimentMeasure lhs, ExperimentMeasure rhs) {
        CompareToBuilder ctb = new CompareToBuilder()
        ctb.append(lhs.getDisplayLabel().toUpperCase(), rhs.getDisplayLabel().toUpperCase())
        ctb.toComparison()
    }
}
