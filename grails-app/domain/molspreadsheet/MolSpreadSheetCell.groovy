package molspreadsheet

import bardqueryapi.MolSpreadSheetCellType
import bardqueryapi.MolSpreadSheetCellUnit

import java.math.RoundingMode
import java.text.NumberFormat
import results.ExperimentalValue
import results.ExperimentalValueType
import results.ExperimentalValueUnit

class MolSpreadSheetCell {
    static final int SPREAD_SHEET_PRECISION = 3

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
     *  non image, no known units
     * @param value
     * @param molSpreadSheetCellType
     */
    MolSpreadSheetCell(String value, MolSpreadSheetCellType molSpreadSheetCellType, SpreadSheetActivityStorage spreadSheetActivityStorage = null) {
        this.spreadSheetActivityStorage = spreadSheetActivityStorage
        this.molSpreadSheetCellType = molSpreadSheetCellType
        switch (this.molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.percentageNumeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.greaterThanNumeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.lessThanNumeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                break;
            case MolSpreadSheetCellType.identifier:
                if ("NaN".equals(value)) {
                    activity = false;
                    intInternalValue = new Integer(0)
                } else
                    intInternalValue = new Integer(value)
                break;
            case MolSpreadSheetCellType.string:
                strInternalValue = new String(value)
                break;
            case MolSpreadSheetCellType.image:
                assert "Images should Not go through the two parameter constructor"
                break;
            default:
                log "You should never hit the default trap on MolSpreadSheetCell ctor1"
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
        if ( (this.molSpreadSheetCellType==MolSpreadSheetCellType.numeric) ||
                (this.molSpreadSheetCellType==MolSpreadSheetCellType.percentageNumeric) ||
                (this.molSpreadSheetCellType==MolSpreadSheetCellType.greaterThanNumeric) ||
                (this.molSpreadSheetCellType==MolSpreadSheetCellType.lessThanNumeric) ||
                (this.molSpreadSheetCellType==MolSpreadSheetCellType.numeric) ){
            if ("NaN".equals(value)) {
                activity = false;
                numInternalValue = new BigDecimal(0)
            } else
                numInternalValue = new BigDecimal(value)
            this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
        } else {
            assert "identifier should Not go through the constructor with type = ${this.molSpreadSheetCellType}"
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
        switch (this.molSpreadSheetCellType) {
            case MolSpreadSheetCellType.image:
                strInternalValue = new String(value1)
                supplementalInternalValue = new String(value2)
                break;
            default:
                assert "Non-images should Not go through the three parameter constructor"

        }

    }

    /**
     *
     * @return
     */
    LinkedHashMap<String, String> mapForMolecularSpreadsheet() {
        def returnValue = new LinkedHashMap<String, String>()
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
    LinkedHashMap<String, String> retrieveValues() {
        def returnValue = new LinkedHashMap<String, String>()
        returnValue.put("name", strInternalValue)
        returnValue.put("smiles", supplementalInternalValue)
        returnValue
    }



    @Override
    String toString() {
        String returnValue
        if ((molSpreadSheetCellType==MolSpreadSheetCellType.lessThanNumeric) ||
                (molSpreadSheetCellType==MolSpreadSheetCellType.greaterThanNumeric) ||
                (molSpreadSheetCellType==MolSpreadSheetCellType.percentageNumeric) ||
                (molSpreadSheetCellType==MolSpreadSheetCellType.numeric))    {
        ExperimentalValue experimentalValue = new  ExperimentalValue( numInternalValue,
                                                                     ExperimentalValueUnit.convert(molSpreadSheetCellUnit),
                                                                     ExperimentalValueType.convert(molSpreadSheetCellType),activity)
            returnValue = experimentalValue.toString()
        } else if (molSpreadSheetCellType==MolSpreadSheetCellType.identifier)  {
            returnValue =   intInternalValue
        }
        returnValue

    }




    static String imageConvert(String name, String smiles) {
        String retVal =
            """<img alt="${smiles}" title="${name}"
  src="\${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: '${smiles}', width: 150, height: 120])}"/>"""
        retVal
    }


}



