package results

import java.text.NumberFormat
import bardqueryapi.MolSpreadSheetCellType
import bardqueryapi.MolSpreadSheetCellUnit

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 9/28/12
 * Time: 6:50 AM
 * To change this template use File | Settings | File Templates.
 */
class ExperimentalValue {
    final String NO_ACTIVITY_STRING = "(no activity)"
    final int DESIRED_PRECISION = 3
    BigDecimal value = 0.0
    ExperimentalValueUnit experimentalValueUnit = ExperimentalValueUnit.unknown  // describes what we hold
    ExperimentalValueUnit insistOnOutputUnits  = ExperimentalValueUnit.unknown   // if not unknown then force output to this unit
    Boolean activity = true
    ExperimentalValueType experimentalValueType = ExperimentalValueType.unknown
    Boolean printUnits = true
    Boolean valueNegative = false



    public ExperimentalValue(BigDecimal value,
                             ExperimentalValueUnit experimentalValueUnit,
                             ExperimentalValueType experimentalValueType,
                             Boolean activity = true ) {
        this.value = value
        this.experimentalValueUnit = experimentalValueUnit
        this.experimentalValueType = experimentalValueType
        this.activity = activity
        if (this.value < 0) {
            this.value = 0 - this.value
            valueNegative = true
        }
    }

    public ExperimentalValue(BigDecimal value,
                             Boolean printUnits ) {
        this.value = value
        this.experimentalValueUnit = ExperimentalValueUnit.Molar
        this.experimentalValueType = ExperimentalValueType.numeric
        this.printUnits = printUnits
        if (this.value < 0) {
            this.value = 0 - this.value
            valueNegative = true
        }
    }


    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        if (!activity)
            stringBuilder.append(NO_ACTIVITY_STRING)
        else if ((experimentalValueType==ExperimentalValueType.lessThanNumeric) ||
                (experimentalValueType==ExperimentalValueType.greaterThanNumeric) ||
                (experimentalValueType==ExperimentalValueType.percentageNumeric) ||
                (experimentalValueType==ExperimentalValueType.numeric)) {
            stringBuilder.append(prepend(experimentalValueType,valueNegative))
            stringBuilder.append("${roundoffToDesiredPrecision(deliverDesiredValue())}")
            stringBuilder.append(append(experimentalValueType))
            if (printUnits)
                stringBuilder.append (experimentalValueUnit.toString())
        } else
            stringBuilder.append(deliverDesiredValue())
        stringBuilder.toString()
    }


    String  roundoffToDesiredPrecision( BigDecimal bigDecimal  )  {
        BigDecimal displayVal =bigDecimal
        Boolean defaultToEngineeringNotation  = false
        NumberFormat numberFormat = NumberFormat.getInstance()
        if (((new BigDecimal("0.01")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("0.1")).compareTo(bigDecimal) > 0)){
            defaultToEngineeringNotation  = true
        } else if (((new BigDecimal("0.1")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("1")).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION )
        } else if (((new BigDecimal("1")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("10")).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION-1 )
        } else if (((new BigDecimal("10")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("100")).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION-2 )
        } else if (((new BigDecimal("100")).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal("1000")).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION-3 )
        } else {      // If someone insists on an output format, or else if the numbers are absurdly big or small
                      //  then give up on trying to be pretty and put that number into engineering format
            defaultToEngineeringNotation  = true
        }
        if (!defaultToEngineeringNotation)
           numberFormat.format(displayVal.doubleValue())
        else {
            displayVal.toEngineeringString()
        }

    }






    BigDecimal deliverDesiredValue() {
        if ( insistOnOutputUnits  != ExperimentalValueUnit.unknown )  // the calling routine wants to set the output units
            insistOnOutputUnit()
        else // pick the optimum units based on the value of the number
            chooseOutputUnit()
        value
    }



    Boolean chooseOutputUnit(){
        Boolean keepGoing  =  (value < 0.1)    ||   (value >= 1000)
        while (keepGoing) {
            if ( ( value < 0.1 )  &&
                 ( experimentalValueUnit.decimalPlacesFromMolar > ExperimentalValueUnit.SmallestPossibleUnit.decimalPlacesFromMolar)){
                performUnitNormalization(  ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar-3) )
            } else if ((value >= 1000)  &&
                    ( experimentalValueUnit.decimalPlacesFromMolar < ExperimentalValueUnit.LargestPossibleUnit.decimalPlacesFromMolar)) {
                performUnitNormalization( ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar+3) )
            }  else
                keepGoing = false
        }
        keepGoing
    }

    Boolean insistOnOutputUnit(){
        Boolean keepGoing = ( insistOnOutputUnits  != ExperimentalValueUnit.unknown ) ? ( experimentalValueUnit != insistOnOutputUnits ) : false
        while (keepGoing) {
            if ( ( experimentalValueUnit.decimalPlacesFromMolar > insistOnOutputUnits.decimalPlacesFromMolar )  &&
                  ( experimentalValueUnit.decimalPlacesFromMolar > ExperimentalValueUnit.SmallestPossibleUnit.decimalPlacesFromMolar)){
                performUnitNormalization( ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar-3) )
            } else if (( experimentalValueUnit.decimalPlacesFromMolar < insistOnOutputUnits.decimalPlacesFromMolar )  &&
                    ( experimentalValueUnit.decimalPlacesFromMolar < ExperimentalValueUnit.LargestPossibleUnit.decimalPlacesFromMolar)) {
                performUnitNormalization(  ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar+3) )
            } else
                keepGoing = false
        }
        keepGoing
    }




    void performUnitNormalization( ExperimentalValueUnit inComingUnit, ExperimentalValueUnit outGoingUnit ) {
        if ((value != null) &&
                (inComingUnit != ExperimentalValueUnit.unknown) &&
                (outGoingUnit != ExperimentalValueUnit.unknown)) {
            int unitSwap = inComingUnit.decimalPlacesFromMolar - outGoingUnit.decimalPlacesFromMolar
            value = value.scaleByPowerOfTen(unitSwap)
            experimentalValueUnit = outGoingUnit
        }
    }


    String prepend(ExperimentalValueType experimentalValueType,Boolean valueNegative) {
        StringBuffer stringBuffer = new StringBuffer()
        switch (experimentalValueType) {
            case ExperimentalValueType.lessThanNumeric:
                stringBuffer.append("< ")
                break;
            case ExperimentalValueType.greaterThanNumeric:
                stringBuffer.append("> ")
                break;
            default:
                stringBuffer.append("")
        }
        if (valueNegative)
            stringBuffer.append("-")
        stringBuffer.toString()
    }

    String append(ExperimentalValueType experimentalValueType) {
        String returnValue
        switch (experimentalValueType) {
            case ExperimentalValueType.percentageNumeric:
                returnValue = " %"
                break;
            default:
                returnValue = ""
        }
        returnValue
    }





}


enum ExperimentalValueUnit {
    Molar("M", 0),
    Millimolar("mM", -3),
    Micromolar("uM", -6),
    Nanomolar("nM", -9),
    Picomolar("pM", -12),
    Femtomolar("fM", -15),
    Attamolar("aM", -18),
    Zeptomolar("zM", -21),
    Yoctomolar("yM", -24),
    unknown("", 0);

    static ExperimentalValueUnit convert(MolSpreadSheetCellUnit molSpreadSheetCellUnit){
        switch (molSpreadSheetCellUnit){
            case MolSpreadSheetCellUnit.Molar :
                return ExperimentalValueUnit.Molar;
            case MolSpreadSheetCellUnit.Millimolar :
                return ExperimentalValueUnit.Millimolar;
            case MolSpreadSheetCellUnit.Micromolar :
                return ExperimentalValueUnit.Micromolar;
            case MolSpreadSheetCellUnit.Nanomolar :
                return ExperimentalValueUnit.Nanomolar;
            case MolSpreadSheetCellUnit.Picomolar :
                return ExperimentalValueUnit.Picomolar;
            case MolSpreadSheetCellUnit.Femtomolar :
                return ExperimentalValueUnit.Femtomolar;
            case MolSpreadSheetCellUnit.Attamolar :
                return ExperimentalValueUnit.Attamolar;
            case MolSpreadSheetCellUnit.Zeptomolar :
                return ExperimentalValueUnit.Zeptomolar;
            case MolSpreadSheetCellUnit.Yoctomolar :
                return ExperimentalValueUnit.Yoctomolar;
            case MolSpreadSheetCellUnit.unknown :
                return ExperimentalValueUnit.unknown;
            default:
                assert false;
        }
    }

    private String value
    private int decimalPlacesFromMolar

    static ExperimentalValueUnit  LargestPossibleUnit =  Molar
    static ExperimentalValueUnit  SmallestPossibleUnit =  Yoctomolar

    ExperimentalValueUnit(String value, int decimalPlacesFromMolar) {
        this.value = value;
        this.decimalPlacesFromMolar = decimalPlacesFromMolar;
    }

    public int getDecimalPlacesFromMolar() {
        decimalPlacesFromMolar
    }

    public String toString() {
        return value;
    }


    public static ExperimentalValueUnit getByDecimalValue(int value) {
        for (final ExperimentalValueUnit element : EnumSet.allOf(ExperimentalValueUnit.class)) {
            if (element.decimalPlacesFromMolar == value) {
                return element;
            }
        }
        return null;
    }


}





enum ExperimentalValueType {
    lessThanNumeric,
    greaterThanNumeric,
    percentageNumeric,
    numeric,
    identifier,
    image,
    string,
    unknown;

static ExperimentalValueType convert(MolSpreadSheetCellType molSpreadSheetCellType){
    switch (molSpreadSheetCellType){
        case MolSpreadSheetCellType.lessThanNumeric :
            return ExperimentalValueType.lessThanNumeric;
        case MolSpreadSheetCellType.greaterThanNumeric :
            return ExperimentalValueType.greaterThanNumeric;
        case MolSpreadSheetCellType.percentageNumeric :
            return ExperimentalValueType.percentageNumeric;
        case MolSpreadSheetCellType.numeric :
            return ExperimentalValueType.numeric;
        case MolSpreadSheetCellType.image :
            return ExperimentalValueType.image;
        case MolSpreadSheetCellType.string :
            return ExperimentalValueType.string;
        case MolSpreadSheetCellType.unknown :
            return ExperimentalValueType.unknown;
        default:
            assert false;
    }
}

}

