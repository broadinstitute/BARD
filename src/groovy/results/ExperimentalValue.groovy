package results

import java.text.NumberFormat

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


    @Override
    String toString() {
        StringBuilder stringBuilder = new StringBuilder()
        if (!activity)
            stringBuilder.append(NO_ACTIVITY_STRING)
        else {

            stringBuilder.append(prepend(experimentalValueType))
            stringBuilder.append("${roundoffToDesiredPrecision(deliverDesiredValue())}")
            stringBuilder.append(append(experimentalValueType))
            if (printUnits)
                stringBuilder.append (experimentalValueUnit.toString())
        }
    }


    String  roundoffToDesiredPrecision( BigDecimal bigDecimal  )  {
   //     BigDecimal displayVal =bigDecimal.setScale(3,RoundingMode.HALF_UP)
        BigDecimal displayVal =bigDecimal
        NumberFormat numberFormat = NumberFormat.getInstance()
        if (((new BigDecimal(0.1)).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal(1)).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION )
            println("F2=${bigDecimal}")
        } else if (((new BigDecimal(1)).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal(10)).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION-1 )
            println("F3=${bigDecimal}")
        } else if (((new BigDecimal(10)).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal(100)).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION-2 )
            println("F4=${bigDecimal}")
        } else if (((new BigDecimal(100)).compareTo(bigDecimal) <= 0) &&
                ((new BigDecimal(1000)).compareTo(bigDecimal) > 0)){
            numberFormat.setMinimumFractionDigits( 0 )
            numberFormat.setMaximumFractionDigits( DESIRED_PRECISION-3 )
            println("F5=${bigDecimal}")
        } else {      //should not happen
            numberFormat.setMinimumFractionDigits( 2 )
            numberFormat.setMaximumFractionDigits( 2 )
            println("F6=${bigDecimal}")
         }
        numberFormat.format(displayVal.doubleValue())
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
                        ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar),ExperimentalValueUnit.getByDecimalValue(experimentalValueUnit.decimalPlacesFromMolar-3) )
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


    String prepend(ExperimentalValueType experimentalValueType) {
        String returnValue
        switch (experimentalValueType) {
            case ExperimentalValueType.lessThanNumeric:
                returnValue = "< "
                break;
            case ExperimentalValueType.greaterThanNumeric:
                returnValue = "> "
                break;
            default:
                returnValue = ""
        }
        returnValue
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
    Zeptomolar("aM", -21),
    unknown("U", 0);

    private String value
    private int decimalPlacesFromMolar

    static ExperimentalValueUnit  LargestPossibleUnit =  Molar
    static ExperimentalValueUnit  SmallestPossibleUnit =  Zeptomolar

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
            if (element.decimalPlacesFromMolar.equals(value)) {
                return element;
            }
        }
        return null;
    }


    public static ExperimentalValueUnit getByValue(String value) {
        for (final ExperimentalValueUnit element : EnumSet.allOf(ExperimentalValueUnit.class)) {
            if (element.toString().equals(value)) {
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
    unknown
}

