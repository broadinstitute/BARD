package molspreadsheet

import bardqueryapi.MolSpreadSheetCellType
import bardqueryapi.MolSpreadSheetCellUnit
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

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



    void "test MolSpreadSheetCell to see how unit conversion works #label"() {
        given: "incoming value"
        SpreadSheetActivityStorage spreadSheetActivityStorage = new  SpreadSheetActivityStorage ()
        when: "#label"
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(initialValue,MolSpreadSheetCellType.numeric,spreadSheetActivityStorage)
        molSpreadSheetCell.molSpreadSheetCellUnit =  initialUnit
        assertNotNull(molSpreadSheetCell)

        then: "The resulting search filters size must equal the expected value"
        BigDecimal testValue =  new BigDecimal(stringValue)
        assert molSpreadSheetCell.bardDecimal(3)  ==  testValue

        where:
        label                           | initialUnit                       |  initialValue |   stringValue
        "converting unit values"        | MolSpreadSheetCellUnit.Micromolar |  "1.23"       |   "1.23"
        "converting unit values"        | MolSpreadSheetCellUnit.Millimolar |  "1.23"       |   "1.23E3"
        "converting unit values"        | MolSpreadSheetCellUnit.Molar      |  "1.23"       |   "1.23E6"
    }



    void "test MolSpreadSheetCell to see number formatting works for #label"() {
        given: "incoming value"
        SpreadSheetActivityStorage spreadSheetActivityStorage = new  SpreadSheetActivityStorage ()
        when: "#label"
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(initialValue,MolSpreadSheetCellType.numeric,spreadSheetActivityStorage)
        molSpreadSheetCell.molSpreadSheetCellUnit =  initialUnit
        assertNotNull(molSpreadSheetCell)

        then: "The resulting search filters size must equal the expected value"
        println molSpreadSheetCell.toString()
        assert molSpreadSheetCell.toString()  ==  stringValue

        where:
        label                           | initialUnit                       |  initialValue |   stringValue
        "number formatting values"      | MolSpreadSheetCellUnit.Micromolar |  "0.1234"       |  "0.12 uM"
        "number formatting values"      | MolSpreadSheetCellUnit.Micromolar |  "1.234"       |   "1.23 uM"
        "number formatting values"      | MolSpreadSheetCellUnit.Micromolar |  "12.34"       |   "12.34 uM"
        "number formatting values"      | MolSpreadSheetCellUnit.Micromolar |  "123.4"       |   "123.4 uM"
    }



}
