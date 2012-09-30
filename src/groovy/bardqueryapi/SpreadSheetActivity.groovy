package bardqueryapi

import bard.core.HillCurveValue
import molspreadsheet.SpreadSheetActivityStorage

/**
 * Since there is no experimentAdapter I had to make a method to open up the experiment
 */
public class SpreadSheetActivity {
    Long eid
    Long cid
    Long sid
    Double potency
    ActivityOutcome activityOutcome
    HillCurveValue hillCurveValue

    List<HillCurveValue> readouts = [] // TODO this is a hack to get multiple displayed in showExperimentResult for demo; FIXME

    public SpreadSheetActivity() {

    }



    SpreadSheetActivityStorage toSpreadSheetActivityStorage() {
        SpreadSheetActivityStorage spreadSheetActivityStorage = new SpreadSheetActivityStorage()
        spreadSheetActivityStorage.sid = sid
        spreadSheetActivityStorage.eid = eid
        spreadSheetActivityStorage.cid = cid
        spreadSheetActivityStorage.activityOutcome = this.activityOutcome
        spreadSheetActivityStorage.potency = this.potency

        if (hillCurveValue != null) {
            spreadSheetActivityStorage.hillCurveValueId = hillCurveValue.id
            spreadSheetActivityStorage.hillCurveValueSInf = hillCurveValue.sinf
            spreadSheetActivityStorage.hillCurveValueS0 = hillCurveValue.s0
            spreadSheetActivityStorage.hillCurveValueSlope = hillCurveValue.slope
            spreadSheetActivityStorage.hillCurveValueCoef = hillCurveValue.coef
            spreadSheetActivityStorage.hillCurveValueConc = hillCurveValue.conc
            spreadSheetActivityStorage.hillCurveValueResponse = hillCurveValue.response
        }
        spreadSheetActivityStorage
    }

    /**
     *
     * @param eid
     * @param cid
     * @param sid
     * @param hillCurveValue
     */
    public SpreadSheetActivity(Long eid,
                               Long cid,
                               Long sid,
                               Double potency,
                               ActivityOutcome activityOutcome,
                               HillCurveValue hillCurveValue) {
        this.eid = eid
        this.cid = cid
        this.sid = sid
        this.activityOutcome = activityOutcome
        this.potency = potency
        this.hillCurveValue = hillCurveValue
    }

    public String toString() {
        final List<String> stringBuilder = []
        stringBuilder.add("CID: ${this.cid}")
        stringBuilder.add("EID: ${this.eid}")
        stringBuilder.add("SID: ${this.sid}")
        stringBuilder.add("Potency: ${this.potency}")
        stringBuilder.add("OutCome: ${this.activityOutcome}")
        stringBuilder.add("Hill Curve Value:\n ${this.hillCurveValue}")
        return stringBuilder.join("\n").toString()
    }



    public Double interpretHillCurveValue() {
        Double retValue = Double.NaN

        if (this.hillCurveValue) {
            retValue = (this.hillCurveValue.getSlope() == null) ? Double.NaN : this.hillCurveValue.getSlope()
        }

        retValue
    }


}
