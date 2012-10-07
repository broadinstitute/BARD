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

    /**
     *
     * @param o
     * @return
     */
    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof SpreadSheetActivityStorage)) return false

        SpreadSheetActivityStorage that = (SpreadSheetActivityStorage) o

        if (cid != that.cid) return false
        if (eid != that.eid) return false
        if (id != that.id) return false
        if (molSpreadSheetCell != that.molSpreadSheetCell) return false
        if (sid != that.sid) return false
        if (version != that.version) return false

        return true
    }

    /**
     *
     * @return
     */
    int hashCode() {
        int result
        result = (eid != null ? eid.hashCode() : 0)
        result = 31 * result + (cid != null ? cid.hashCode() : 0)
        result = 31 * result + (sid != null ? sid.hashCode() : 0)
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (molSpreadSheetCell != null ? molSpreadSheetCell.hashCode() : 0)
        return result
    }

    /**
     *
     * @param spreadSheetActivity
     */
    public SpreadSheetActivityStorage(SpreadSheetActivity spreadSheetActivity){
        this.sid = spreadSheetActivity.sid
        this.eid = eid
        this.cid = cid
        this.activityOutcome = spreadSheetActivity.activityOutcome
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
