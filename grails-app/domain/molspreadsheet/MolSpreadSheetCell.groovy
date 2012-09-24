package molspreadsheet

import bardqueryapi.MolSpreadSheetCellType
import bardqueryapi.MolSpreadSheetCellUnit

class MolSpreadSheetCell {

    static hasOne = [ spreadSheetActivityStorage : SpreadSheetActivityStorage ]

    static belongsTo = [ molSpreadSheetData : MolSpreadSheetData ]

    static final int SPREAD_SHEET_PRECISION = 3
    Boolean activity = true
    MolSpreadSheetCellType molSpreadSheetCellType = MolSpreadSheetCellType.unknown
    Object objInternalValue
    String strInternalValue = "null"
    BigDecimal numInternalValue = 0.0
    Integer intInternalValue = 0
    MolSpreadSheetCellUnit molSpreadSheetCellUnit = MolSpreadSheetCellUnit.unknown
    String supplementalInternalValue = null
    SpreadSheetActivityStorage spreadSheetActivityStorage


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
                objInternalValue = value
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
        switch (this.molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.percentageNumeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.greaterThanNumeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.lessThanNumeric:
                if ("NaN".equals(value)) {
                    activity = false;
                    numInternalValue = new BigDecimal(0)
                } else
                    numInternalValue = new BigDecimal(value)
                this.molSpreadSheetCellUnit = molSpreadSheetCellUnit
                break;
            case MolSpreadSheetCellType.identifier:
                assert "identifier should Not go through the constructor with unit type"
                break;
            case MolSpreadSheetCellType.string:
                assert "string should Not go through the constructor with unit type"
                break;
            case MolSpreadSheetCellType.image:
                assert "Images should Not go through the constructor with unit type"
                break;
            default:
                objInternalValue = value
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

    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        switch (molSpreadSheetCellType) {
            case MolSpreadSheetCellType.numeric:
                if (!activity)
                    stringBuilder.append("(no activity)")
                else
                    stringBuilder.append("${numInternalValue.toEngineeringString()}")
                break;
            case MolSpreadSheetCellType.percentageNumeric:
                if (!activity)
                    stringBuilder.append("(no activity)")
                else
                    stringBuilder.append("${numInternalValue.toEngineeringString()} %")
                break;
            case MolSpreadSheetCellType.greaterThanNumeric:
                if (!activity)
                    stringBuilder.append("(no activity)")
                else
                    stringBuilder.append("> ${numInternalValue.toEngineeringString()}")
                break;
            case MolSpreadSheetCellType.lessThanNumeric:
                if (!activity)
                    stringBuilder.append("(no activity)")
                else
                    stringBuilder.append("< ${numInternalValue.toEngineeringString()}")
                break;
            case MolSpreadSheetCellType.identifier:
                stringBuilder.append("${intInternalValue}")
                break;
            case MolSpreadSheetCellType.string:
                stringBuilder.append("${strInternalValue}")
                break;
            case MolSpreadSheetCellType.image:
                assert "Images should not be retrieved using toString"
                stringBuilder.append("${strInternalValue}")
                break;
            default:
                stringBuilder.append("${objInternalValue}")
        }
        if ( (molSpreadSheetCellUnit != MolSpreadSheetCellUnit.unknown) &&
                (activity) )
            stringBuilder.append(" ${molSpreadSheetCellUnit.toString()}")
        stringBuilder.toString()
    }

    LinkedHashMap<String, String> retrieveValues() {
        def returnValue = new LinkedHashMap<String, String>()
        returnValue.put("name", strInternalValue)
        returnValue.put("smiles", supplementalInternalValue)
        returnValue
    }


    static String imageConvert(String name, String smiles) {
        String retVal =
            """<img alt="${smiles}" title="${name}"
  src="\${createLink(controller: 'chemAxon', action: 'generateStructureImage', params: [smiles: '${smiles}', width: 150, height: 120])}"/>"""
        retVal
    }

    static constraints = {
    }


}



