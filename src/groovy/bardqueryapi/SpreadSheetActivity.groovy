package bardqueryapi

import bard.core.HillCurveValue

/**
 *
 */
class SpreadSheetActivity {
    Long experimentId
    Long eid
    Long cid
    Long sid
    Double potency
    ActivityOutcome activityOutcome
    HillCurveValue hillCurveValue
    // TODO this is a hack to get multiple readouts displayed in showExperimentResult for demo; FIXME
    List<HillCurveValue> readouts = []




    Double interpretHillCurveValue() {
        Double retValue = Double.NaN

        if (this.hillCurveValue) {
            retValue = (this.hillCurveValue.slope == null) ? Double.NaN : this.hillCurveValue.slope
        }

        retValue
    }


}
