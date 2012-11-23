package results

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import molspreadsheet.MolSpreadSheetCellType
import org.apache.commons.lang.NotImplementedException
import spock.lang.Specification
import spock.lang.Unroll

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
        final ExperimentalValueType experimentalValueType = ExperimentalValueType.convert(cellType)

        then: "The resulting experimental value type should much the expected one"
        assert experimentalValueType == expectedExperimentalValueType

        where:
        label                  | cellType                                  | expectedExperimentalValueType
        "Less Than Numeric"    | MolSpreadSheetCellType.lessThanNumeric    | ExperimentalValueType.lessThanNumeric
        "Greater Than Numeric" | MolSpreadSheetCellType.greaterThanNumeric | ExperimentalValueType.greaterThanNumeric
        "Percentage Numeric"   | MolSpreadSheetCellType.percentageNumeric  | ExperimentalValueType.percentageNumeric
        "Numeric"              | MolSpreadSheetCellType.numeric            | ExperimentalValueType.numeric
        "Image"                | MolSpreadSheetCellType.image              | ExperimentalValueType.image
        "String"               | MolSpreadSheetCellType.string             | ExperimentalValueType.string
        "Unknown"              | MolSpreadSheetCellType.unknown            | ExperimentalValueType.unknown

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