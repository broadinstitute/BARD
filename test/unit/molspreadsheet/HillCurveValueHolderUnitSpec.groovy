package molspreadsheet

import grails.test.mixin.TestFor
import spock.lang.Unroll
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(HillCurveValueHolder)
@Unroll
class HillCurveValueHolderUnitSpec extends Specification{

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "Smoke test can we build a HillCurveValueHolder"() {
        when:
        HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        assertNotNull(hillCurveValueHolder)

        then:
        assertNotNull hillCurveValueHolder.conc
        assertNotNull hillCurveValueHolder.response
    }

}
