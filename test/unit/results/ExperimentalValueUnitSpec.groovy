package results

import molspreadsheet.MolSpreadSheetCellUnit
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

    void "test performUnitNormalization"() {
        given:  "values the calling routine would request"
        ExperimentalValue experimentalValue = new ExperimentalValue(0.02, ExperimentalValueUnit.Micromolar, ExperimentalValueType.unknown, true)
        final ExperimentalValueUnit originalUnit = experimentalValue.experimentalValueUnit
        experimentalValue.insistOnOutputUnits = ExperimentalValueUnit.unknown

        when: "requesting normalization"
        experimentalValue.performUnitNormalization(ExperimentalValueUnit.unknown, ExperimentalValueUnit.unknown)

        then:  "nothing changes"
        assert originalUnit == experimentalValue.experimentalValueUnit

    }

    void "test insistOnOutputUnit is False"() {
        given:
        ExperimentalValue experimentalValue = new ExperimentalValue(0.02, ExperimentalValueUnit.Micromolar, ExperimentalValueType.unknown, true)
        experimentalValue.insistOnOutputUnits = ExperimentalValueUnit.unknown
        when: "#label"
        Boolean bool = experimentalValue.insistOnOutputUnit()

        then: "The resulting search filters size must equal the expected value"
        assert !bool

    }

    void "test roundOff to Desired Precision"() {
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
        assert experimentalValue.toString() == stringValue

        where:
        label                    | initialUnit                      | initialValue | stringValue
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23         | "1.23 uM"
        "converting unit values" | ExperimentalValueUnit.Millimolar | 1.23         | "1.23 mM"
        "converting unit values" | ExperimentalValueUnit.Molar      | 1.23         | "1.23 M"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123          | "123 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.3         | "12.3 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23         | "1.23 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.123        | "0.123 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.0123       | "12.3 nM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.00123      | "1.23 nM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.000123     | "0.123 nM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.0000123    | "12.3 pM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 0.00000123   | "1.23 pM"
    }


    void "test other default ctor"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(initialValue, printUnits)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                                   | printUnits | initialValue | stringValue
        "converting unit values"                | true       | 1            | "1 M"
        "converting unit values"                | true       | 1.2          | "1.2 M"
        "converting unit values"                | false      | 1.23         | "1.23"
        "converting unit values"                | false      | 1.234        | "1.23"
        "converting unit values negative value" | false      | -1.234       | "-1.23"
    }


    void "test another default ctor"() {
        when: "#label"
        ExperimentalValue experimentalValue = new ExperimentalValue(initialValue)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString() == stringValue

        where:
        label                                   | initialValue  | stringValue
        "converting unit values"                | 1D            | "1"
        "converting unit values"                | 1.2D          | "1.2"
        "converting unit values"                | 1.23D         | "1.23"
        "converting unit values"                | 1.235D        | "1.24"
        "converting unit values negative value" | -1.234D       | "-1.23"
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
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1            | "1 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.2          | "1.2 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23         | "1.23 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.234        | "1.23 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.2345       | "1.23 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 1.23456      | "1.23 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12           | "12 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.3         | "12.3 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.34        | "12.3 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.345       | "12.3 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 12.3456      | "12.3 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123          | "123 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.4        | "123 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.5        | "124 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.6        | "124 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.45       | "123 uM"
        "converting unit values" | ExperimentalValueUnit.Micromolar | 123.456      | "123 uM"
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
        "convert big Molar to yoctomolar"   | ExperimentalValueUnit.Molar      | ExperimentalValueUnit.Yoctomolar | 1.2E31       | "12E+54 yM"
        "convert tiny yoctomolar to Molar"  | ExperimentalValueUnit.Yoctomolar | ExperimentalValueUnit.Molar      | 1.2E-31      | "120E-57 M"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Micromolar | -123456789   | "-123E+6 uM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Millimolar | -123456789   | "-123E+3 mM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Molar      | -123456789   | "-123 M"        // small enough to the formatted correctly
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Micromolar | -12345E-9    | "-0.0000123 uM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Millimolar | -12345E-9    | "-12.3E-9 mM"
        "conversions with negative numbers" | ExperimentalValueUnit.Micromolar | ExperimentalValueUnit.Molar      | -12345E-9    | "-12.3E-12 M"
    }

    void "test toString #label"() {
        given:
        ExperimentalValue experimentalValue = new ExperimentalValue(new BigDecimal("2.0"), Boolean.FALSE)
        experimentalValue.activity = activity
        experimentalValue.experimentalValueType = experimentValueType

        when: "#label"
        final String stringRepresentation = experimentalValue.toString()
        then: "The resulting search filters size must equal the expected value"
        assert stringRepresentation == expectedStringValue

        where:
        label                                                         | activity | experimentValueType                      | expectedStringValue
        "No Activity"                                                 | false    | ExperimentalValueType.greaterThanNumeric | "(no activity)"
        "Experiment Value Type == lessThanNumeric and activity==true" | true     | ExperimentalValueType.lessThanNumeric    | "< 2"
        "No ExperimentalValueType and No activity"                    | true     | null                                     | "2.0"
    }

    void "test type conversions"() {
        when:
        MolSpreadSheetCellUnit molSpreadSheetCellUnit = MolSpreadSheetCellUnit.unknown
        assertNotNull(molSpreadSheetCellUnit)
        ExperimentalValueUnit experimentalValueUnit = ExperimentalValueUnit.unknown
        assertNotNull(experimentalValueUnit)

        then:
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

