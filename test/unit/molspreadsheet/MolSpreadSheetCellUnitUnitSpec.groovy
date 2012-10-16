package molspreadsheet

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/12/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@Unroll
class MolSpreadSheetCellUnitUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }


    void "test getByDecimalValue #label"() {
        given:

        when: "#label"
        MolSpreadSheetCellUnit molSpreadSheetCellUnit =  expectedMolSpreadSheetUnit

        then: "Should equal the expected"
        assert molSpreadSheetCellUnit.toString() == value

        where:
        label        | value | expectedMolSpreadSheetUnit
        "Molar"      | "M"    | MolSpreadSheetCellUnit.Molar
        "MilliMolar" | "mM"   | MolSpreadSheetCellUnit.Millimolar
        "Micromolar" | "uM"   | MolSpreadSheetCellUnit.Micromolar
        "Nanomolar"  | "nM"   | MolSpreadSheetCellUnit.Nanomolar
        "Picomolar"  | "pM"  | MolSpreadSheetCellUnit.Picomolar
        "Femtomolar" | "fM"  | MolSpreadSheetCellUnit.Femtomolar
        "Attamolar"  | "aM"  | MolSpreadSheetCellUnit.Attamolar
        "Zeptomolar" | "zM"  | MolSpreadSheetCellUnit.Zeptomolar
        "Yoctomolar" | "yM"  | MolSpreadSheetCellUnit.Yoctomolar
        "Null"       | "U"   | MolSpreadSheetCellUnit.unknown
    }


    void "test MolSpreadSheetCell constructor, two parameters, no units"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(inputString,molSpreadSheetCellType)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == resultingString
        assert molSpreadSheetCell.activity == activity

        where:
        molSpreadSheetCellType                      | inputString   |   resultingString |   activity
        MolSpreadSheetCellType.numeric              | "0.123"       |   "0.123"         |   true
        MolSpreadSheetCellType.numeric              | "NaN"         |   "(no activity)" |   false
        MolSpreadSheetCellType.percentageNumeric    | "0.123"       |   "0.123 %"       |   true
        MolSpreadSheetCellType.percentageNumeric    | "NaN"         |   "(no activity)" |   false
        MolSpreadSheetCellType.greaterThanNumeric   | "0.123"       |   "> 0.123"       |   true
        MolSpreadSheetCellType.greaterThanNumeric   | "NaN"         |   "(no activity)" |   false
        MolSpreadSheetCellType.lessThanNumeric      | "0.123"       |   "< 0.123"       |   true
        MolSpreadSheetCellType.lessThanNumeric      | "NaN"         |   "(no activity)" |   false
        MolSpreadSheetCellType.string               | "0.123"       |   "0.123"         |   true
        MolSpreadSheetCellType.identifier           | "3"           |   "3"             |   true
    }


    void "test MolSpreadSheetCell constructor, two parameters, no units, error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("NaN",MolSpreadSheetCellType.identifier)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == "0"
    }




    void "test MolSpreadSheetCell constructor, three parameters, no units"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(inputString,molSpreadSheetCellType,molSpreadSheetCellUnit)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == resultingString
        assert molSpreadSheetCell.activity == activity

        where:
        molSpreadSheetCellType                      | inputString   |   resultingString     |   molSpreadSheetCellUnit              |   activity
        MolSpreadSheetCellType.numeric              | "0.123"       |   "0.123uM"           |   MolSpreadSheetCellUnit.Micromolar   |   true
        MolSpreadSheetCellType.numeric              | "NaN"         |   "(no activity)"     |   MolSpreadSheetCellUnit.Micromolar   |   false
        MolSpreadSheetCellType.percentageNumeric    | "0.123"       |   "0.123 %"           |   MolSpreadSheetCellUnit.unknown      |   true
        MolSpreadSheetCellType.percentageNumeric    | "NaN"         |   "(no activity)"     |   MolSpreadSheetCellUnit.unknown      |   false
        MolSpreadSheetCellType.greaterThanNumeric   | "0.123"       |   "> 0.123uM"         |   MolSpreadSheetCellUnit.Micromolar   |   true
        MolSpreadSheetCellType.greaterThanNumeric   | "NaN"         |   "(no activity)"     |   MolSpreadSheetCellUnit.Micromolar   |   false
        MolSpreadSheetCellType.lessThanNumeric      | "0.123"       |   "< 0.123uM"         |   MolSpreadSheetCellUnit.Micromolar   |   true
        MolSpreadSheetCellType.lessThanNumeric      | "NaN"         |   "(no activity)"     |   MolSpreadSheetCellUnit.Micromolar   |   false
    }


    void "test MolSpreadSheetCell constructor, three parameters, no units, unknown type error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("NaN",MolSpreadSheetCellType.unknown,MolSpreadSheetCellUnit.Micromolar)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.molSpreadSheetCellType ==  MolSpreadSheetCellType.unknown
    }


    void "test MolSpreadSheetCell constructor, three parameters, no units, string error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("0",MolSpreadSheetCellType.string,MolSpreadSheetCellUnit.Micromolar)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString()=="null"
    }




}
