package molspreadsheet

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 *   These are the tags that are used to build the different cells within a molecular spreadsheet.  The same methods
 *   can be used in both the transposed and un-transposed version of the spreadsheet.
 */
@TestFor(SunburstHandlerTagLib)
@Unroll
class SunburstHandlerTagLibUnitSpec extends Specification {

    void setup() {
    }

    void tearDown() {

    }


    void "test sunburstSection"() {
        when:
        String results = this.tagLib.sunburstSection()
        then:
        results.contains("sunburstdiv_bigwin")
    }

    void "test sunburstLegend "() {
        when:
        String results = this.tagLib.sunburstLegend()
        then:
        results.contains("createALegend")
        results.contains("legendGoesHere")
    }


}