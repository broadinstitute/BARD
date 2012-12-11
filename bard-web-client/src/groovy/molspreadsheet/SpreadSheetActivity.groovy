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

    public void activityToSpreadSheetActivity(final Activity activity, final List<String> resultTypeNames) {
        this.cid = activity.cid
        this.eid = activity.eid
        this.sid = activity.sid
        this.addPotency(activity)
        this.addOutCome(activity)
        final List<Readout> readouts = activity.readouts
        readOutsToHillCurveValues(readouts, resultTypeNames)
    }

    void addPotency(final Activity activity) {
        if (activity.potency) {
            this.potency = new Double(activity.potency)
        }
    }

    void addOutCome(final Activity activity) {
        if (activity.outcome != null) {
            this.activityOutcome = ActivityOutcome.findActivityOutcome(activity.outcome.intValue())
        }
    }

    void readOutsToHillCurveValues(final List<Readout> readouts, final List<String> resultTypeNames) {
        if (readouts) {
            for (Readout readout : readouts) {
                readOutToHillCurveValue(resultTypeNames, readout)
            }
        }
    }

    void readOutToHillCurveValue(final List<String> resultTypeNames, final Readout readout) {
        final HillCurveValue hillCurveValue = readout.toHillCurveValue()
        if (hillCurveValue) {
            if (hillCurveValue.id) {
                if (!resultTypeNames.contains(hillCurveValue.id)) {
                    resultTypeNames.add(hillCurveValue.id)
                }
                this.hillCurveValueList << hillCurveValue
            }
        }

    }

}
