package bardqueryapi

import bard.core.HillCurveValue
import spock.lang.Specification
import spock.lang.Unroll
import molspreadsheet.SpreadSheetActivity

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class SpreadSheetActivityUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }
    /**
     * {@link SpreadSheetActivity#interpretHillCurveValue}
     */
    void "test interpret Hill Curve Value with null HillCurveValue"() {
        when:
        final SpreadSheetActivity spreadSheetActivity =
            new SpreadSheetActivity(hillCurveValue: hillCurveValue)
        final Double value = spreadSheetActivity.interpretHillCurveValue()
        then:
        assert value == expectedResults

        where:
        label                       | hillCurveValue                           | expectedResults
        "Null HillCurveValue"       | null                                     | Double.NaN
        "HillCurveValue No Slope"   | new HillCurveValue()                     | Double.NaN
        "HillCurveValue With Slope" | new HillCurveValue(slope: new Double(2)) | new Double(2)
    }

}
