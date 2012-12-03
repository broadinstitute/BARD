package bardqueryapi

import bard.core.rest.spring.experiment.Readout
import molspreadsheet.SpreadSheetActivity
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class SpreadSheetActivityUnitSpec extends Specification {


    void "test readOutToHillCurveValue(final List<String> resultTypeNames, final Readout readout)"() {
        given:
        SpreadSheetActivity spreadSheetActivity = new SpreadSheetActivity()
        Readout readout = new Readout()
        List<String> resultTypeNames = []
        when:
        spreadSheetActivity.readOutToHillCurveValue(resultTypeNames, readout)
        then:
        assert resultTypeNames
    }

}
