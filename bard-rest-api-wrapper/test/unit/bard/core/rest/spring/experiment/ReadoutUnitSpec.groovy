package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.experiment.Readout

@Unroll
class ReadoutUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String READ_OUT_JSON = '''
   {
       "name": "INHIBITION",
       "s0": 3.57,
       "sInf": 100,
       "hill": 1.35,
       "ac50": 0.0000033999999999999996,
       "cr":
       [
           [
               1.02e-8,
               11.67
           ],
           [
               3.05e-8,
               4.38
           ],
           [
               9.15e-8,
               3.57
           ],
           [
               2.74e-7,
               4.13
           ],
           [
               8.230000000000001e-7,
               12.16
           ],
           [
               0.00000247,
               39.47
           ],
           [
               0.00000741,
               72.93
           ],
           [
               0.000022199999999999998,
               93.92
           ],
           [
               0.0000667,
               100
           ]
       ],
       "npoint": 9,
       "concUnit": "m",
       "responseUnit": null
   }
'''

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Readout Dose response"() {
        when:
        Readout readOut = objectMapper.readValue(READ_OUT_JSON, Readout.class)
        then:
        assert readOut
        assert !readOut.responseUnit
        assert readOut.concentrationUnits
        assert readOut.numberOfPoints == 9
        assert readOut.name == "INHIBITION"
        assert readOut.s0 == 3.57
        assert readOut.sInf == 100
        assert readOut.coef == 1.35
        assert readOut.slope == 0.0000033999999999999996
        assert readOut.getCr().size() == 9
        assert readOut.getConcAsList().size() == 9
        assert readOut.getResponseAsList().size() == 9
    }

}

