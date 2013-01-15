package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CurveFitParametersUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    @Shared
    String JSON_CURVE_FIT_PARAMS = '''
   {
    "s0":0.4253,
    "sInf":-73.0939,
    "hillCoef":1.3723,
    "logEc50":-4.6
   }
   '''


    void "test JSON #label"() {
        when:
        CurveFitParameters curveFitParameters = objectMapper.readValue(JSON_CURVE_FIT_PARAMS, CurveFitParameters.class)
        then:
        assert curveFitParameters
        assert curveFitParameters.s0 == 0.4253
        assert curveFitParameters.SInf == -73.0939
        assert curveFitParameters.hillCoef == 1.3723
        curveFitParameters.logEc50 == -4.6

    }


}

