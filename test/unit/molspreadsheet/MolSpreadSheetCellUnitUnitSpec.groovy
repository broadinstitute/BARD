package molspreadsheet

import spock.lang.Specification
import molspreadsheet.MolSpreadSheetCellUnit

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/12/12
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
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


}
