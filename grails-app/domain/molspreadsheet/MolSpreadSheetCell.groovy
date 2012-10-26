package molspreadsheet

import bard.core.HillCurveValue
import results.ExperimentalValue
import results.ExperimentalValueType
import results.ExperimentalValueUnit

class MolSpreadSheetCell {
    // static final int SPREAD_SHEET_PRECISION = 3

    static hasOne = [spreadSheetActivityStorage: SpreadSheetActivityStorage]

    static belongsTo = [molSpreadSheetData: MolSpreadSheetData]

    Boolean activity = true
    MolSpreadSheetCellType molSpreadSheetCellType = MolSpreadSheetCellType.unknown
    String strInternalValue = "null"
    BigDecimal numInternalValue = 0.0
    Integer intInternalValue = 0
    MolSpreadSheetCellUnit molSpreadSheetCellUnit = MolSpreadSheetCellUnit.unknown
    String supplementalInternalValue = null
    SpreadSheetActivityStorage spreadSheetActivityStorage


    static constraints = {
        activity()
        molSpreadSheetCellType(blank: false)
        strInternalValue(nullable: false)
        numInternalValue(nullable: false)
        intInternalValue(nullable: false)
        molSpreadSheetCellUnit(blank: false)
        supplementalInternalValue()
        spreadSheetActivityStorage()
    }

    /**
     *  non image, no units  specified
     * @param value
     * @param molSpreadSheetCellType
     */
    MolSpreadSheetCell(String value, MolSpreadSheetCellType molSpreadSheetCellType, SpreadSheetActivityStorage spreadSheetActivityStorage = null) {
        this.spreadSheetActivityStorage = spreadSheetActivityStorage
        this.molSpreadSheetCellType = molSpreadSheetCellType
        if ((this.molSpreadSheetCellType == MolSpreadSheetCellType.numeric) ||
                (this.molSpreadSheetCellType == MolSpreadSheetCellType.percentageNumeric) ||
                (this.molSpreadSheetCellType == MolSpreadSheetCellType.greaterThanNumeric) ||
                (this.molSpreadSheetCellType == MolSpreadSheetCellType.lessThanNumeric)) {
            if ("NaN".equals(value)) {
                activity = false;
                numInternalValue = new BigDecimal(0)
            } else {
                numInternalValue = new BigDecimal(value)
            }
        } else if (this.molSpreadSheetCellType == MolSpreadSheetCellType.string) {
            strInternalValue = new String(value)
        } else if (this.molSpreadSheetCellType == MolSpreadSheetCellType.identifier) {
            if ("NaN".equals(value)) {
                activity = false;
                intInternalValue = 0
            } else {
                intInternalValue = new Integer(value)
            }
        } else {
            log.error "We should never see mole spreadsheet type ${this.molSpreadSheetCellType} in this three parameter constructor"
        }
    }

    /**
     *  numeric elements with unit specifications
     * @param value
     * @param molSpreadSheetCellType
     * @param molSpreadSheetCellUnit
     */
    MolSpreadSheetCell(String value, MolSpreadSheetCellType molSpreadSheetCellType, MolSpreadSheetCellUnit molSpreadSheetCellUnit, SpreadSheetActivityStorage spreadSheetActivityStorage = null) {
        this.spreadSheetActivityStorage = spreadSheetActivityStorage
        this.molSpreadSheetCellType = molSpreadSheetCellType
        if ((this.molSpreadSheetCellType == MolSpreadSheetCellType.numeric) ||
                (this.molSpreadSheetCellType == MolSpreadSheetCellType.percentageNumeric) ||
                (this.molSpreadSheetCellType == MolSpreadSheetCellType.greaterThanNumeric) ||
                (this.molSpreadSheetCellType == MolSpreadSheetCellType.lessThanNumeric) ||
                (this.molSpreadSheetCellType == MolSpreadSheetCellType.numeric)) {
            if ("NaN".equals(value)) {
                activity = false;
                numInternalValue = new BigDecimal(0)
            } else {
                numInternalValue = new BigDecimal(value)
            }
            this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
        } else {
            log.error "We should never see mole spreadsheet type ${this.molSpreadSheetCellType} in this four parameter constructor"
        }
    }



    MolSpreadSheetCell( SpreadSheetActivity spreadSheetActivity ) {
        if ((spreadSheetActivity.cid == null)  ||
                (spreadSheetActivity.sid == null)  ||
                (spreadSheetActivity.eid == null)  )   {
            assert false," missing critical values. ${spreadSheetActivity.dump()}"
        }
        this.molSpreadSheetCellType = MolSpreadSheetCellType.numeric
        this.molSpreadSheetCellUnit =  MolSpreadSheetCellUnit.Molar

        this.spreadSheetActivityStorage = new SpreadSheetActivityStorage( eid: spreadSheetActivity.eid,
                                                                          cid:  spreadSheetActivity.cid,
                                                                          sid:spreadSheetActivity.sid,
                                                                          activityOutcome: spreadSheetActivity.activityOutcome )
        for (HillCurveValue hillCurveValue in spreadSheetActivity.hillCurveValueList) {
             HillCurveValueHolder hillCurveValueHolder =  new HillCurveValueHolder(  identifier: hillCurveValue.id,
                     s0: hillCurveValue.s0,
                    sInf: hillCurveValue.sInf,
                    slope: hillCurveValue.slope,
                    coef: hillCurveValue.coef,
                    conc: hillCurveValue.conc,
                    response: hillCurveValue.response)
            if (!this.spreadSheetActivityStorage.columnNames.contains(hillCurveValue.id))
                this.spreadSheetActivityStorage.columnNames << hillCurveValue.id
            hillCurveValueHolder.subColumnIndex  =  this.spreadSheetActivityStorage.columnNames.indexOf(hillCurveValue.id)
            this.spreadSheetActivityStorage.hillCurveValueHolderList  << hillCurveValueHolder
        }

    }




    /**
     * ctor for images
     * @param value1
     * @param value2
     * @param molSpreadSheetCellType
     */
    MolSpreadSheetCell(String value1, String value2, MolSpreadSheetCellType molSpreadSheetCellType, SpreadSheetActivityStorage spreadSheetActivityStorage = null) {
        this.molSpreadSheetCellType = molSpreadSheetCellType
        this.spreadSheetActivityStorage = spreadSheetActivityStorage
        if (this.molSpreadSheetCellType == MolSpreadSheetCellType.image) {
            strInternalValue = new String(value1?:"")
            supplementalInternalValue = new String(value2?:"")
        } else {
            log.error "We should never see mole spreadsheet type ${this.molSpreadSheetCellType} in this four parameter constructor specialized for images"
        }
    }

    /**
     *
     * @return
     */
    Map<String, String> mapForMolecularSpreadsheet() {
        Map<String, String> returnValue = [:]
        if (molSpreadSheetCellType == MolSpreadSheetCellType.image) {
            returnValue = retrieveValues()
        } else {
            returnValue["value"] = toString()
        }
        returnValue
    }


    /**
     *
     * @return
     */
    Map<String, String> mapForMolecularSpreadsheet(int subColumn) {
        Map<String, String> returnValue = [:]

        returnValue["value"] = spreadSheetActivityStorage?.toString(subColumn)

        returnValue
    }





    /**
     *
     * @return
     */
    Map<String, String> retrieveValues() {
        return ["name": strInternalValue, "smiles": supplementalInternalValue]
    }



    @Override
    String toString() {
        String returnValue = null
        if ((molSpreadSheetCellType == MolSpreadSheetCellType.lessThanNumeric) ||
                (molSpreadSheetCellType == MolSpreadSheetCellType.greaterThanNumeric) ||
                (molSpreadSheetCellType == MolSpreadSheetCellType.percentageNumeric) ||
                (molSpreadSheetCellType == MolSpreadSheetCellType.numeric)) {
            ExperimentalValue experimentalValue = new ExperimentalValue(numInternalValue,
                    ExperimentalValueUnit.convert(molSpreadSheetCellUnit),
                    ExperimentalValueType.convert(molSpreadSheetCellType), activity)
            returnValue = experimentalValue.toString()
        } else if (molSpreadSheetCellType == MolSpreadSheetCellType.identifier) {
            returnValue = intInternalValue
        } else if (molSpreadSheetCellType == MolSpreadSheetCellType.string) {
            returnValue = strInternalValue
        }
        returnValue

    }


}



