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
        MolSpreadSheetCellUnit molSpreadSheetCellUnit = expectedMolSpreadSheetUnit

        then: "Should equal the expected"
        assert molSpreadSheetCellUnit.toString() == value

        where:
        label        | value | expectedMolSpreadSheetUnit
        "Molar"      | "M"   | MolSpreadSheetCellUnit.Molar
        "MilliMolar" | "mM"  | MolSpreadSheetCellUnit.Millimolar
        "Micromolar" | "uM"  | MolSpreadSheetCellUnit.Micromolar
        "Nanomolar"  | "nM"  | MolSpreadSheetCellUnit.Nanomolar
        "Picomolar"  | "pM"  | MolSpreadSheetCellUnit.Picomolar
        "Femtomolar" | "fM"  | MolSpreadSheetCellUnit.Femtomolar
        "Attamolar"  | "aM"  | MolSpreadSheetCellUnit.Attamolar
        "Zeptomolar" | "zM"  | MolSpreadSheetCellUnit.Zeptomolar
        "Yoctomolar" | "yM"  | MolSpreadSheetCellUnit.Yoctomolar
        "Null"       | "U"   | MolSpreadSheetCellUnit.unknown
    }


    void "test MolSpreadSheetCell constructor, two parameters, no units"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(inputString, molSpreadSheetCellType)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == resultingString
        assert molSpreadSheetCell.activity == activity

        where:
        molSpreadSheetCellType                    | inputString | resultingString | activity
        MolSpreadSheetCellType.identifier         | "3"         | "3"             | MolSpreadSheetCellActivityOutcome.Unknown
    }


    void "test MolSpreadSheetCell constructor, two parameters, no units, error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("NaN", MolSpreadSheetCellType.identifier)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == "0"
    }


    void "test MolSpreadSheetCell constructor, three parameters, no units, unknown type error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("NaN", MolSpreadSheetCellType.unknown, MolSpreadSheetCellUnit.Micromolar)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.molSpreadSheetCellType == MolSpreadSheetCellType.unknown
    }


    void "test MolSpreadSheetCell constructor, three parameters, no units, string error condition"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("0", MolSpreadSheetCellType.string, MolSpreadSheetCellUnit.Micromolar)
        assertNotNull(molSpreadSheetCell)

        then:
        assert molSpreadSheetCell.toString() == "null"
    }

    void "test MolSpreadSheetCell constructor, three parameters: String value1, String value2, MolSpreadSheetCellType molSpreadSheetCellType: #label"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell("0", "1", molSpreadSheetCellType)

        then:
        assertNotNull(molSpreadSheetCell)
        assert molSpreadSheetCell.strInternalValue == expectedStrInternalValue


        where:
        label                           | molSpreadSheetCellType        | expectedStrInternalValue
        'MolSpreadSheetCellType.string' | MolSpreadSheetCellType.string | "null"
        'MolSpreadSheetCellType.image'  | MolSpreadSheetCellType.image  | "0"
    }

    void "test overriden toString(): #label"() {
        when:
        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell()
        molSpreadSheetCell.activity = MolSpreadSheetCellActivityOutcome.Unknown
        molSpreadSheetCell.molSpreadSheetCellType = molSpreadSheetCellType
        molSpreadSheetCell.strInternalValue = 'something'
        molSpreadSheetCell.intInternalValue = 2

        then:
        assertNotNull(molSpreadSheetCell)
        assert molSpreadSheetCell.toString() == expectedToString


        where:
        label                                       | molSpreadSheetCellType                    | expectedToString
        'MolSpreadSheetCellType.identifier'         | MolSpreadSheetCellType.identifier         | "2"
        'MolSpreadSheetCellType.image'              | MolSpreadSheetCellType.image              | null
    }
}
