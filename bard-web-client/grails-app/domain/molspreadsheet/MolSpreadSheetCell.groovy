package molspreadsheet

import bard.core.rest.spring.experiment.CurveFitParameters
import bard.core.rest.spring.experiment.PriorityElement

class MolSpreadSheetCell {
    // static final int SPREAD_SHEET_PRECISION = 3

    static hasOne = [spreadSheetActivityStorage: SpreadSheetActivityStorage]

    static belongsTo = [molSpreadSheetData: MolSpreadSheetData]

    MolSpreadSheetCellActivityOutcome activity =  MolSpreadSheetCellActivityOutcome.Unknown
    MolSpreadSheetCellType molSpreadSheetCellType = MolSpreadSheetCellType.unknown
    String strInternalValue = "null"
    Integer intInternalValue = 0
    String supplementalInternalValue
    SpreadSheetActivityStorage spreadSheetActivityStorage


    static constraints = {
        activity(nullable: false)
        molSpreadSheetCellType(blank: false)
        strInternalValue(nullable: true)
        intInternalValue(nullable: false)
        supplementalInternalValue(nullable: true)
        spreadSheetActivityStorage(nullable: true)
    }

    public MolSpreadSheetCell() {

    }

    public MolSpreadSheetCell(MolSpreadSheetCell molSpreadSheetCellToCopy) {
        this.activity = molSpreadSheetCellToCopy.activity
        this.molSpreadSheetCellType = molSpreadSheetCellToCopy.molSpreadSheetCellType
        this.strInternalValue = molSpreadSheetCellToCopy.strInternalValue
        this.intInternalValue = molSpreadSheetCellToCopy.intInternalValue
        this.supplementalInternalValue = molSpreadSheetCellToCopy.supplementalInternalValue
        this.spreadSheetActivityStorage = null
    }


    public MolSpreadSheetCell(MolSpreadSheetCell molSpreadSheetCellToCopy, int exptIndex) {
        this.activity = molSpreadSheetCellToCopy.activity
        this.molSpreadSheetCellType = molSpreadSheetCellToCopy.molSpreadSheetCellType
        this.strInternalValue = molSpreadSheetCellToCopy.strInternalValue
        this.intInternalValue = molSpreadSheetCellToCopy.intInternalValue
        this.supplementalInternalValue = molSpreadSheetCellToCopy.supplementalInternalValue
        this.spreadSheetActivityStorage = new SpreadSheetActivityStorage(molSpreadSheetCellToCopy.spreadSheetActivityStorage, exptIndex)
    }

    /**
     *  non image, no units  specified
     * @param value
     * @param molSpreadSheetCellType
     */
    MolSpreadSheetCell(String value, MolSpreadSheetCellType molSpreadSheetCellType, SpreadSheetActivityStorage spreadSheetActivityStorage = null) {
        this.spreadSheetActivityStorage = spreadSheetActivityStorage
        this.molSpreadSheetCellType = molSpreadSheetCellType
        if (this.molSpreadSheetCellType == MolSpreadSheetCellType.identifier) {
            if ("NaN".equals(value)) {
                activity = MolSpreadSheetCellActivityOutcome.Unspecified
                intInternalValue = 0
            } else {
                intInternalValue = new Integer(value)
            }
        }
        if (this.molSpreadSheetCellType == MolSpreadSheetCellType.string) {
            strInternalValue = value
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
    }

    /**
     *  This constructor is one of the central means by which backend data is interpreted into a form the molecular spreadsheet can understand
     * @param spreadSheetActivity
     */
    MolSpreadSheetCell(SpreadSheetActivity spreadSheetActivity) {
        this.molSpreadSheetCellType = MolSpreadSheetCellType.numeric
        this.spreadSheetActivityStorage = new SpreadSheetActivityStorage(eid: spreadSheetActivity.eid,
                cid: spreadSheetActivity.cid,
                sid: spreadSheetActivity.sid,
                activityOutcome: spreadSheetActivity.activityOutcome )
        int counter = 0
        for (PriorityElement priorityElement in spreadSheetActivity.priorityElementList) {
            this.spreadSheetActivityStorage.responseUnit = priorityElement.responseUnit
            if (priorityElement.value == "") {       // does this ever happen
                activity = MolSpreadSheetCellActivityOutcome.Unspecified
                intInternalValue = 0
            }   else {
                // Get the units
                 String identifierString = priorityElement.getDictionaryLabel() ?: priorityElement.responseUnit  ?: " "
                // Check for an identifier.  We can ignore "=", since we only care about the identifier the changes something
                if (priorityElement.qualifier == ">") {
                    this.molSpreadSheetCellType =  MolSpreadSheetCellType.greaterThanNumeric
                } else  if (priorityElement.qualifier == "<")  {
                    this.molSpreadSheetCellType =  MolSpreadSheetCellType.lessThanNumeric
                }
                // Check for child elements, and save them if they are available
                if (priorityElement.hasChildElements()) {
                    this.spreadSheetActivityStorage.childElements = priorityElement.childElements
                }
                // Gather up the curve values if they exist
                HillCurveValueHolder hillCurveValueHolder
                if (priorityElement.value == null) {
                    hillCurveValueHolder = new HillCurveValueHolder(identifier: identifierString, slope: Double.NaN)
                }  else {
                    Double value = Double.parseDouble(priorityElement.value)
                    if (value != null) {
                        this.spreadSheetActivityStorage.dictionaryDescription =  priorityElement.getDictionaryDescription() ?: ''
                        this.spreadSheetActivityStorage.dictionaryLabel =  priorityElement.getDictionaryLabel() ?: ''
                        if (priorityElement.concentrationResponseSeries?.curveFitParameters) {
                            CurveFitParameters curveFitParameters = priorityElement.concentrationResponseSeries.curveFitParameters
                            hillCurveValueHolder = new HillCurveValueHolder(
                                    identifier: identifierString,
                                    s0: curveFitParameters.s0,
                                    sInf: curveFitParameters.sInf,
                                    slope: value,
                                    coef: curveFitParameters.hillCoef,
                                    conc: priorityElement.concentrationResponseSeries.concentrationResponsePoints*.testConcentration,
                                    response: priorityElement.concentrationResponseSeries.concentrationResponsePoints*.value,
                                    xAxisLabel: "Log(Concentration) ${priorityElement.testConcentrationUnit}",
                                    yAxisLabel: priorityElement.concentrationResponseSeries?.getYAxisLabel() )
                        } else {
                            hillCurveValueHolder = new HillCurveValueHolder(identifier: identifierString, slope: value)
                        }
                    }
                }
                hillCurveValueHolder.subColumnIndex = this.spreadSheetActivityStorage.columnNames.indexOf(identifierString)
                this.spreadSheetActivityStorage.hillCurveValueHolderList << hillCurveValueHolder
                this.spreadSheetActivityStorage.qualifier = this.molSpreadSheetCellType
                counter++

            }
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
            strInternalValue = value1 ?: ""
            supplementalInternalValue = value2 ?: ""
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
    Map<String, String> retrieveValues() {
        return ["name": strInternalValue, "smiles": supplementalInternalValue]
    }



    @Override
    String toString() {
        String returnValue = null
        if (molSpreadSheetCellType == MolSpreadSheetCellType.identifier) {
            returnValue = intInternalValue
        } else if (molSpreadSheetCellType == MolSpreadSheetCellType.string) {
            returnValue = strInternalValue
        }
        returnValue

    }


}



