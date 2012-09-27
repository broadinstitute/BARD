package molspreadsheet

import bardqueryapi.ActivityOutcome
import bardqueryapi.SpreadSheetActivity

class SpreadSheetActivityStorage {

    static belongsTo = [molSpreadSheetCell : MolSpreadSheetCell]

    Long eid
    Long cid
    Long sid
    ActivityOutcome activityOutcome
    Double potency
    String hillCurveValueId
    Double  hillCurveValueSInf
    Double  hillCurveValueS0
    Double  hillCurveValueSlope
    Double  hillCurveValueCoef
    List<Double>  hillCurveValueConc
    List<Double>  hillCurveValueResponse

    public SpreadSheetActivityStorage(){

    }
    public SpreadSheetActivityStorage(SpreadSheetActivity spreadSheetActivity){
        this.sid = spreadSheetActivity.sid
        this.eid = eid
        this.cid = cid
        this.outcome = spreadSheetActivity.outcome
        this.potency = spreadSheetActivity.potency

        if (spreadSheetActivity.hillCurveValue) {
            this.hillCurveValueId = spreadSheetActivity.hillCurveValue.id
            this.hillCurveValueSInf = spreadSheetActivity.hillCurveValue.sinf
            this.hillCurveValueS0 = spreadSheetActivity.hillCurveValue.s0
            this.hillCurveValueSlope = spreadSheetActivity.hillCurveValue.slope
            this.hillCurveValueCoef = spreadSheetActivity.hillCurveValue.coef
            this.hillCurveValueConc = spreadSheetActivity.hillCurveValue.conc
            this.hillCurveValueResponse = spreadSheetActivity.hillCurveValue.response
        }
    }
    static constraints = {
    }
}
