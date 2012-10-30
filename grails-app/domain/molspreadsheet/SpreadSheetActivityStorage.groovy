package molspreadsheet

import bardqueryapi.ActivityOutcome

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class SpreadSheetActivityStorage {

    static belongsTo = [molSpreadSheetCell: MolSpreadSheetCell]
    static hasMany = [ hillCurveValueHolderList : HillCurveValueHolder  ]

    Long eid
    Long cid
    Long sid
    ActivityOutcome activityOutcome
    List<HillCurveValueHolder> hillCurveValueHolderList  =  []
    List<Double> columnNames  = []
    Double potency

    static constraints = {
        eid(nullable: true)
        cid(nullable: true)
        sid(nullable: true)
        activityOutcome(nullable: true)
        potency(nullable: true)
    }



    SpreadSheetActivityStorage() {

    }

    /**
     *
     * @param spreadSheetActivityStorage
     * @param experimentIndex
     */
    SpreadSheetActivityStorage(SpreadSheetActivityStorage spreadSheetActivityStorage, int experimentIndex ) {
        this.eid =  spreadSheetActivityStorage.eid
        this.cid =  spreadSheetActivityStorage.cid
        this.sid =  spreadSheetActivityStorage.sid
        this.activityOutcome =  spreadSheetActivityStorage.activityOutcome
        this.potency =  spreadSheetActivityStorage.potency
        if (experimentIndex < spreadSheetActivityStorage.hillCurveValueHolderList?.size())
           this.hillCurveValueHolderList =  [ spreadSheetActivityStorage.hillCurveValueHolderList[experimentIndex]  ]
        if (experimentIndex < spreadSheetActivityStorage.hillCurveValueHolderList?.size())
            this.columnNames =  [ spreadSheetActivityStorage.columnNames[experimentIndex]  ]
    }

    /**
     *
     * @param subColumn
     * @return
     */
    public String toString(int subColumn) {
        String returnValue = "unknown"
        if ((hillCurveValueHolderList!=null) && (subColumn<=hillCurveValueHolderList.size()))
            returnValue = hillCurveValueHolderList[subColumn].toString()
        returnValue
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
        this.eid = spreadSheetActivity.eid
        this.cid = spreadSheetActivity.cid
        this.activityOutcome = spreadSheetActivity.activityOutcome
        this.potency = spreadSheetActivity.potency
    }

}
