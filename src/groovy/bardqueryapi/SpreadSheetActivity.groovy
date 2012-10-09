package bardqueryapi

import bard.core.HillCurveValue

/**
 *
 */
public class SpreadSheetActivity {
    Long experimentId
    Long eid
    Long cid
    Long sid
    Double potency
    ActivityOutcome activityOutcome
    HillCurveValue hillCurveValue

    List<HillCurveValue> readouts = [] // TODO this is a hack to get multiple readouts displayed in showExperimentResult for demo; FIXME




    public Double interpretHillCurveValue() {
        Double retValue = Double.NaN

        if (this.hillCurveValue) {
            retValue = (this.hillCurveValue.slope == null) ? Double.NaN : this.hillCurveValue.slope
        }

        retValue
    }


}
