package results

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import molspreadsheet.MolSpreadSheetCellType
import org.apache.commons.lang.NotImplementedException
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.util.ExperimentalValueTypeUtil

@TestMixin(GrailsUnitTestMixin)
@Unroll
class ExperimentalValueTypeUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test convert #label"() {


        when: "We call the convert method"
        final ExperimentalValueTypeUtil experimentalValueType = ExperimentalValueType.convert(cellType)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueType == expectedExperimentalValueType

        where:
        label                  | cellType                                  | expectedExperimentalValueType
        "Less Than Numeric"    | MolSpreadSheetCellType.lessThanNumeric    | ExperimentalValueTypeUtil.lessThanNumeric
        "Greater Than Numeric" | MolSpreadSheetCellType.greaterThanNumeric | ExperimentalValueTypeUtil.greaterThanNumeric
        "Percentage Numeric"   | MolSpreadSheetCellType.percentageNumeric  | ExperimentalValueTypeUtil.percentageNumeric
        "Numeric"              | MolSpreadSheetCellType.numeric            | ExperimentalValueTypeUtil.numeric
        "Image"                | MolSpreadSheetCellType.image              | ExperimentalValueTypeUtil.image
        "String"               | MolSpreadSheetCellType.string             | ExperimentalValueTypeUtil.string
        "Unknown"              | MolSpreadSheetCellType.unknown            | ExperimentalValueTypeUtil.unknown

    }

    void "test convert throw NotImplementedException"() {


        when: "We call the convert method"
        ExperimentalValueType.convert(cellType)

        then: "The resulting experimental value is completely unexpected, so throw an exception"
        Exception ee = thrown()
        assert ee instanceof NotImplementedException

        where:
        label        | cellType
        "Identifier" | MolSpreadSheetCellType.identifier

    }

    void "test default switch"() {
        when: "We call the convert method for something we don't handle"
        ExperimentalValueType.convert(cellType)

        then: "The code should throw an exception"
        Exception ee = thrown()
        assert ee instanceof NotImplementedException

        where:
        label                 | cellType
        "UnHandled Exception" | MolSpreadSheetCellType.unhandled
    }


}