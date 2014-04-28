/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package molspreadsheet

import bardqueryapi.ActivityOutcome
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import bard.core.rest.spring.experiment.ActivityData

class SpreadSheetActivityStorage {

    static belongsTo = [molSpreadSheetCell: MolSpreadSheetCell]
    static hasMany = [hillCurveValueHolderList: HillCurveValueHolder]

    Long eid
    Long cid
    Long sid
    ActivityOutcome activityOutcome
    List<HillCurveValueHolder> hillCurveValueHolderList = []
    List<Double> columnNames = []
    Double potency
    String qualifier = ""
    String responseUnit = ''
    List<ActivityData> childElements = []
    String dictionaryDescription = ''
    String dictionaryLabel = ''
    int dictionaryId = 0

    static mapping = {
        table(name: 'MOL_SS_ACTIVITY_STORAGE')
        id(generator: 'sequence', params: [sequence: 'MOL_SS_ACTIVITY_STORAGE_ID_SEQ'])
        molSpreadSheetCell(column: "MOL_SS_CELL")
        hillCurveValueHolderList(column: "MOL_SS_ACTIVITY_STORAGE_ID", indexColumn: [name: "MOL_SS_ACT_STORAGE_LIST_IDX", type: Integer])
    }
    static constraints = {
        eid(nullable: true)
        cid(nullable: true)
        sid(nullable: true)
        activityOutcome(nullable: true)
        potency(nullable: true)
        responseUnit(nullable: true)
        dictionaryDescription(nullable: true)
        dictionaryLabel(nullable: true)
    }



    SpreadSheetActivityStorage() {

    }


    void setQualifier(MolSpreadSheetCellType molSpreadSheetCellType) {
        if (molSpreadSheetCellType == MolSpreadSheetCellType.greaterThanNumeric)
            qualifier = ">"
        else if (molSpreadSheetCellType == MolSpreadSheetCellType.lessThanNumeric)
            qualifier = "<"
    }
    /**
     *  This seems to be used only in unit tests
     * @param spreadSheetActivity
     */
    SpreadSheetActivityStorage(SpreadSheetActivity spreadSheetActivity) {
        this.sid = spreadSheetActivity.sid
        this.eid = spreadSheetActivity.eid
        this.cid = spreadSheetActivity.cid
        this.activityOutcome = spreadSheetActivity.activityOutcome
        this.potency = spreadSheetActivity.potency
    }

    /**
     *
     * @param spreadSheetActivityStorage
     * @param experimentIndex
     */
    SpreadSheetActivityStorage(SpreadSheetActivityStorage spreadSheetActivityStorage, int experimentIndex) {
        this.eid = spreadSheetActivityStorage.eid
        this.cid = spreadSheetActivityStorage.cid
        this.sid = spreadSheetActivityStorage.sid
        this.activityOutcome = spreadSheetActivityStorage.activityOutcome
        this.potency = spreadSheetActivityStorage.potency
        this.responseUnit = spreadSheetActivityStorage.responseUnit
        this.childElements = spreadSheetActivityStorage.childElements
        if (experimentIndex < spreadSheetActivityStorage.hillCurveValueHolderList?.size()) {
            HillCurveValueHolder hillCurveValueHolder = spreadSheetActivityStorage.hillCurveValueHolderList[experimentIndex]
            hillCurveValueHolder.qualifier = spreadSheetActivityStorage.qualifier
            this.hillCurveValueHolderList = [hillCurveValueHolder]
        }
        if (experimentIndex < spreadSheetActivityStorage.hillCurveValueHolderList?.size())
            this.columnNames = [spreadSheetActivityStorage.columnNames[experimentIndex]]
        this.dictionaryDescription = spreadSheetActivityStorage.dictionaryDescription
        this.dictionaryLabel = spreadSheetActivityStorage.dictionaryLabel
        this.dictionaryId = spreadSheetActivityStorage.dictionaryId
    }


    String printUnits(String resultValueHolder = '') {
        String returnValue
        if ((resultValueHolder == '--')||
            (resultValueHolder == 'NA')){ // if we have a null value and we don't want to print out any units for it
            returnValue = ''
        } else {
            if (responseUnit == null)
                returnValue = ''
            else if (responseUnit == 'percent')
                returnValue = '%'
            else if (responseUnit == 'um')
                returnValue = 'uM'
            else
                returnValue = responseUnit.toString()
        }
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


}
