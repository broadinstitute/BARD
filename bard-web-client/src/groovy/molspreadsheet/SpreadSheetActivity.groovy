package molspreadsheet

import bard.core.HillCurveValue
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.Readout
import bardqueryapi.ActivityOutcome

/**
 *
 */
class SpreadSheetActivity {
    Long experimentId
    Long eid = 0L
    Long cid = 0L
    Long sid = 0L
    Double potency
    ActivityOutcome activityOutcome = ActivityOutcome.UNSPECIFIED
    List<HillCurveValue> hillCurveValueList = []

    public void activityToSpreadSheetActivity(Activity activity) {
        this.cid = activity.cid
        this.eid = activity.eid
        this.sid = activity.sid
        if (activity.potency != null) {
            this.potency = new Double(activity.potency)
        }
        if (activity.outcome != null) {
            this.activityOutcome = ActivityOutcome.findActivityOutcome(activity.outcome.intValue())
        }
        final List<Readout> readouts = activity.readouts
        if (readouts) {
            for (Readout readout : readouts) {

                final HillCurveValue hillCurveValue = readout.toHillCurveValue()
                this.hillCurveValueList << hillCurveValue
            }
        }
    }

}
