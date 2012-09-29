package molspreadsheet

import bardqueryapi.MolSpreadSheetCellType
import bardqueryapi.MolSpreadSheetCellUnit
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import results.ExperimentalValue
import results.ExperimentalValueUnit
import spock.lang.Specification
import spock.lang.Unroll
import results.ExperimentalValueType

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestFor(MolSpreadSheetController)
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MolSpreadSheetCellSpec extends  Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test MolSpreadSheetCell to see if the object can be instantiated"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("0.123",MolSpreadSheetCellType.numeric)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.activity == true

    }



    void "test handling of unit conversions" (){
        when: "#label"
        ExperimentalValue experimentalValue = new  ExperimentalValue(initialValue,initialUnit,ExperimentalValueType.numeric, true)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        println experimentalValue.toString()
        assert experimentalValue.toString()  ==  stringValue

        where:
        label                           | initialUnit                       |  initialValue |   stringValue
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1.23       |   "1.23uM"
        "converting unit values"        | ExperimentalValueUnit.Millimolar |  1.23       |   "1.23mM"
        "converting unit values"        | ExperimentalValueUnit.Molar      |  1.23       |   "1.23M"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  123        |   "123uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  12.3       |   "12.3uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1.23       |   "1.23uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  0.123      |   "0.123uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  0.0123     |   "12.3nM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  0.00123    |   "1.23nM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  0.000123   |   "0.123nM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  0.0000123  |   "12.3pM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  0.00000123 |   "1.23pM"
    }


    void "test other ctor" (){
        when: "#label"
        ExperimentalValue experimentalValue = new  ExperimentalValue(initialValue,printUnits)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString()  ==  stringValue

        where:
        label                           | printUnits    |  initialValue                  | stringValue
        "converting unit values"        | true |  1       |   "1M"
        "converting unit values"        | true |  1.2     |   "1.2M"
        "converting unit values"        | false |  1.23    |   "1.23"
        "converting unit values"        | false |  1.234   |   "1.23"
    }


    void "test handling of precision conversions" (){
        when: "#label"
        ExperimentalValue experimentalValue = new  ExperimentalValue(initialValue,initialUnit,ExperimentalValueType.numeric, true)
        assertNotNull(experimentalValue)

        then: "The resulting search filters size must equal the expected value"
        assert experimentalValue.toString()  ==  stringValue

        where:
        label                           | initialUnit                       |  initialValue |   stringValue
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1       |   "1uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1.2     |   "1.2uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1.23    |   "1.23uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1.234   |   "1.23uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1.2345  |   "1.23uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  1.23456 |   "1.23uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  12      |   "12uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  12.3    |   "12.3uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  12.34   |   "12.3uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  12.345  |   "12.3uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  12.3456 |   "12.3uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  123     |   "123uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  123.4   |   "123uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  123.45  |   "123uM"
        "converting unit values"        | ExperimentalValueUnit.Micromolar |  123.456 |   "123uM"
    }





}
