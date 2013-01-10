package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ConcentrationResponsePointUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    @Shared
    String JSON_DATA = '''
     {
        "testConc":9.85385E-4,"value":"1.9624",
        "childElements":
        [
           {
              "displayName":"Rep2ForExperiment3_1000_uM",
              "dictElemId":1016,
              "testConcUnit":"uM",
              "testConc":1000.0,
              "value":"105.693"
           },
           {
              "displayName":"Rep1ForExperiment3_1000_uM",
              "dictElemId":1016,
              "testConcUnit":"uM",
              "testConc":1000.0,
              "value":"102.669"
           },
           {
              "displayName":"StddevForExperiment3_1000uM",
              "dictElemId":613,
              "testConcUnit":"uM",
              "testConc":1000.0,
              "value":"2.13829"
           }
        ]
     }
    '''



    void "test JSON #label"() {
        when:
        ConcentrationResponsePoint concentrationResponsePoint = objectMapper.readValue(JSON_DATA, ConcentrationResponsePoint.class)
        then:
        assert concentrationResponsePoint.testConc
        assert concentrationResponsePoint.value

        final List<ActivityData> childElements = concentrationResponsePoint.childElements
        assert childElements
        assert childElements.size() == 3
        for(ActivityData childElement in childElements){
            assert childElement.displayName
            assert childElement.dictElemId
            assert childElement.testConcentration
            assert childElement.testConcentrationUnit
            assert childElement.value
        }
    }


}

