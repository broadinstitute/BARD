package molspreadsheet

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
class MolSpreadSheetCellUnitSpec extends  Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
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


//    void "test MolSpreadSheetCell constructor, four parameters, generic strings used for images"() {
//        when:
//        MolSpreadSheetCell molSpreadSheetCell = new MolSpreadSheetCell(inputString,inputString,molSpreadSheetCellType)
//        assertNotNull(molSpreadSheetCell)
//
//        then:
//        assert molSpreadSheetCell() == resultingString
//
//        where:
//        molSpreadSheetCellType                      | inputString   |   resultingString
//        MolSpreadSheetCellType.numeric              | "abc"         |   "abc"
//    }




}
