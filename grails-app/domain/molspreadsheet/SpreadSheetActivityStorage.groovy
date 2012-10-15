package molspreadsheet

import bardqueryapi.ActivityOutcome

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class SpreadSheetActivityStorage {

    static belongsTo = [molSpreadSheetCell: MolSpreadSheetCell]

    Long eid
    Long cid
    Long sid
    ActivityOutcome activityOutcome
    Double potency
    String hillCurveValueId
    Double hillCurveValueSInf
    Double hillCurveValueS0
    Double hillCurveValueSlope
    Double hillCurveValueCoef
    List<Double> hillCurveValueConc
    List<Double> hillCurveValueResponse

    SpreadSheetActivityStorage() {

    }

    /**
     *
     * @param o
     * @return
     */
    boolean equals(Object o) {

        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        SpreadSheetActivityStorage that = (SpreadSheetActivityStorage) o

        return new EqualsBuilder().
                append(this.cid, that.cid).
                append(this.eid, that.eid).
                append(this.sid, that.sid).
                append(this.molSpreadSheetCell, that.molSpreadSheetCell).
                isEquals();
    }

    int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.eid).
                append(this.cid).
                append(this.sid).
                append(this.molSpreadSheetCell?.hashCode()).
                toHashCode();
    }
    /**
     *
     * @param spreadSheetActivity
     */
    SpreadSheetActivityStorage(SpreadSheetActivity spreadSheetActivity) {
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
        eid(nullable: true)
        cid(nullable: true)
        sid(nullable: true)
        activityOutcome(nullable: true)
        potency(nullable: true)
        hillCurveValueId(nullable: true)
        hillCurveValueSInf(nullable: true)
        hillCurveValueS0(nullable: true)
        hillCurveValueSlope(nullable: true)
        hillCurveValueCoef(nullable: true)
    }
}
