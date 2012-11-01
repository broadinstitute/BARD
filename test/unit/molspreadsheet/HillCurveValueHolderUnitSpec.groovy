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

    SpreadSheetActivityStorage spreadSheetActivityStorage

    void setup() {
         spreadSheetActivityStorage = new  SpreadSheetActivityStorage ()
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


    void "Test constraints for HillCurveValueHolder"() {
        given:
        mockForConstraintsTests(HillCurveValueHolder)

        when:
        HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        hillCurveValueHolder.spreadSheetActivityStorage = spreadSheetActivityStorage
        assert hillCurveValueHolder.validate()
        assert !hillCurveValueHolder.hasErrors()

        then:
        assertTrue hillCurveValueHolder.validate()
        def identifier = hillCurveValueHolder.identifier
        hillCurveValueHolder.setIdentifier( null )
        assertFalse hillCurveValueHolder.validate()
        hillCurveValueHolder.setIdentifier ( identifier )
        assertTrue hillCurveValueHolder.validate()

        assertTrue hillCurveValueHolder.validate()
        def subColumnIndex = hillCurveValueHolder.subColumnIndex
        hillCurveValueHolder.setSubColumnIndex( null )
        assertFalse hillCurveValueHolder.validate()
        hillCurveValueHolder.setSubColumnIndex ( subColumnIndex )
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setS0(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setSlope(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setCoef(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setConc(null)
        assertTrue hillCurveValueHolder.validate()

        hillCurveValueHolder.setResponse(null)
        assertTrue hillCurveValueHolder.validate()

    }



    void "Test toString method"() {
        when:
        HillCurveValueHolder hillCurveValueHolder = new HillCurveValueHolder()
        hillCurveValueHolder.slope = slope
        hillCurveValueHolder.response = [response]

        then:
        assertNotNull hillCurveValueHolder
        assert hillCurveValueHolder.toString() ==  returnValue

        where:
        slope       |   response    |   returnValue
        47.89       |   null        |   "47.9"
        null        |   47.89       |   "47.9"
        47.89       |   47.89       |   "47.9"
        null        |   null        |    "Missing data qualifier"

    }



}
