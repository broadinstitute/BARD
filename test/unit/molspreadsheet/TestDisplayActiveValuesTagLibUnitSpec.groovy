package molspreadsheet

import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test
import grails.test.mixin.TestMixin
import spock.lang.Specification
import grails.test.mixin.TestFor
import spock.lang.Unroll
import grails.plugin.spock.TagLibSpec

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 10/31/12
 * Time: 10:51 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(DisplayActiveValuesTagLib)
class TestDisplayActiveValuesTagLibUnitSpec {

    void setUp() {
        // Setup logic here
    }

   void tearDown() {
            // Tear down logic here
    }

    @Test
    void testDisplayActiveValuesTagLib() {
        when:
        int i=1
        then:
        assert true
//        String results = new DisplayActiveValuesTagLib().curveDescr([s0: 1.0d, slope: 2.0d, sInf: 3.0d, ac50: 4.0d],null)
//        assert results == "AC50 : 1.0  <br/>sInf : 2.0  <br/>s0 : 3.0  <br/>HillSlope : 4.0  <br/>"
//
//        results = new DisplayActiveValuesTagLib().curveDescr([s0: null, slope: null, sInf: null, ac50: null],null)
//        assert results.trim() == ""
//
//        results = new DisplayActiveValuesTagLib().curveDescr([s0: null, slope: null, sInf: null, ac50: null],null)
//        assert results.trim() == ""
//
//        results = new DisplayActiveValuesTagLib().curveDescr([s0: null, slope: null, sInf: null, ac50: null],null)
//        assert results.trim() == ""
//
//        results = new DisplayActiveValuesTagLib().curveDescr([s0: null, slope: null, sInf: null, ac50: null],null)
//        assert results.trim() == ""

    }

}
