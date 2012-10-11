package results

import bardqueryapi.MolSpreadSheetCellUnit
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

@TestMixin(GrailsUnitTestMixin)
@Unroll
class ExperimentalValueUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test roudnOff to Desired Precision"() {
        given:
        ExperimentalValue experimentalValue = new ExperimentalValue(initialValue, initialUnit, ExperimentalValueType.numeric, true)
        when: "#label"
        String val = experimentalValue.roundoffToDesiredPrecision(initialValue)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert val == stringValue

        where:
        label                    | initialUnit                      | initialValue | stringValue
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.02         | "0.02"

    }

    void "test handling of unit conversions"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(initialValue, initialUnit, ExperimentalValueType.numeric, true)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        println experimentalValue.toString()
        assert experimentalValue.toString() == stringValue

        where:
        label                    | initialUnit                      | initialValue | stringValue
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23         | "1.23uM"
        "converting unit values" | ExperimentalValueUnit.Millimolar | 1.23         | "1.23mM"
        "converting unit values" | ExperimentalValueUnit.Molar      | 1.23         | "1.23M"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123          | "123uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.3         | "12.3uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23         | "1.23uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.123        | "0.123uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.0123       | "12.3nM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.00123      | "1.23nM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.000123     | "0.123nM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.0000123    | "12.3pM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.00000123   | "1.23pM"
    }


    void "test other default ctor"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(initialValue, printUnits)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                                   | printUnits | initialValue | stringValue
        "converting unit values"                | true       | 1            | "1M"
        "converting unit values"                | true       | 1.2          | "1.2M"
        "converting unit values"                | false      | 1.23         | "1.23"
        "converting unit values"                | false      | 1.234        | "1.23"
        "converting unit values negative value" | false      | -1.234       | "-1.23"
    }




    void "test what we do when there is nothing to print"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(47, true)
        experimentalValue.activity = false
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == "(no activity)"
    }




    void "test handling of precision conversions"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(initialValue, initialUnit, ExperimentalValueType.numeric, true)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                    | initialUnit                      | initialValue | stringValue
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1            | "1uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.2          | "1.2uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23         | "1.23uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.234        | "1.23uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.2345       | "1.23uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23456      | "1.23uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12           | "12uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.3         | "12.3uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.34        | "12.3uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.345       | "12.3uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.3456      | "12.3uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123          | "123uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.4        | "123uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.45       | "123uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.456      | "123uM"
    }

    /**
     *  While we try to hold things to a nice, clean decimal representation by shifting the units around,
     *  it is easy to devise corner cases that are outside the range that we can represent in this way.
     *  When this happens, the ExperimentalValues class essentially gives up, and passes the value to
     *  the BigDecimal toEngineeringString method and says "do it you can let this one".  The users
     *  will likely never see any of these results, but just in case the numbers get really weird it's
     *  worth defining what the code should do.
     */
    void "test explicit conversions to unusual units"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(initialValue, initialUnit, ExperimentalValueType.numeric, true)
        experimentalValue.setInsistOnOutputUnits(outputUnit)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                               | initialUnit                      | outputUnit                       | initialValue | stringValue
        "convert big Molar to yoctomolar"   | ExperimentalValueUnit.Molar      | ExperimentalValueUnit.Yoctomolar | 1.2E31       | "12E+54yM"
        "convert tiny yoctomolar to Molar"  | ExperimentalValueUnit.Yoctomolar | ExperimentalValueUnit.Molar      | 1.2E-31      | "120E-57M"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Micromolar | -123456789   | "-123456789uM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Millimolar | -123456789   | "-123456.789mM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Molar      | -123456789   | "-123M"        // small enough to the formatted correctly
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Micromolar | -12345E-9    | "-0.000012345uM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Millimolar | -12345E-9    | "-12.345E-9mM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Molar      | -12345E-9    | "-12.345E-12M"
    }

    void "test toString #label"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(new BigDecimal("2.0"), new Boolean(false))
        experimentalValue.activity = activity
        experimentalValue.experimentalValueType = experimentValueType

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == expectedStringValue

        where:
        label                                                         | activity | experimentValueType                      | expectedStringValue
        "No Activity"                                                 | false    | ExperimentalValueType.greaterThanNumeric | "(no activity)"
        "Experiment Value Type == lessThanNumeric and activity==true" | true     | ExperimentalValueType.lessThanNumeric    | "< 2"
        "No ExperimentalValueType and No activity"                    | false    | null                                     | "(no activity)"
    }

    void "test type conversions"() {
        when:
        MolSpreadSheetCellUnit molSpreadSheetCellUnit = MolSpreadSheetCellUnit.unknown
        assertNotNull(molSpreadSheetCellUnit)
        ExperimentalValueUnit experimentalValueUnit = ExperimentalValueUnit.unknown
        assertNotNull(experimentalValueUnit)

        then: "The resulting search filters size must equal the expected value"
        assert outUnit == ExperimentalValueUnit.convert(inUnit)

        where:
        inUnit                            | outUnit
        MolSpreadSheetCellUnit.Molar      | ExperimentalValueUnit.Molar
        MolSpreadSheetCellUnit.Millimolar | ExperimentalValueUnit.Millimolar
        MolSpreadSheetCellUnit.Micromolar | ExperimentalValueUnit.Micromolar
        MolSpreadSheetCellUnit.Nanomolar  | ExperimentalValueUnit.Nanomolar
        MolSpreadSheetCellUnit.Picomolar  | ExperimentalValueUnit.Picomolar
        MolSpreadSheetCellUnit.Femtomolar | ExperimentalValueUnit.Femtomolar
        MolSpreadSheetCellUnit.Attamolar  | ExperimentalValueUnit.Attamolar
        MolSpreadSheetCellUnit.Zeptomolar | ExperimentalValueUnit.Zeptomolar
        MolSpreadSheetCellUnit.Yoctomolar | ExperimentalValueUnit.Yoctomolar
        MolSpreadSheetCellUnit.unknown    | ExperimentalValueUnit.unknown
    }


}

