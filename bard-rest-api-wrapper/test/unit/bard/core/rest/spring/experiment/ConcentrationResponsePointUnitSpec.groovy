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

    void "test concentration response point serialization"() {
        when:
        ConcentrationResponsePoint concentrationResponsePoint = objectMapper.readValue(JSON_DATA, ConcentrationResponsePoint.class)
        then:
        assert concentrationResponsePoint.testConcentration
        assert concentrationResponsePoint.value
        assert concentrationResponsePoint.toDisplay("uM") == "1.96 @ 0.985 nM"
        assert concentrationResponsePoint.displayActivity() == "1.96"
        assert concentrationResponsePoint.displayConcentration("uM") == "0.985 nM"
        assert concentrationResponsePoint.toDisplay("uM")
        final List<ActivityData> childElements = concentrationResponsePoint.childElements
        assert childElements
        assert childElements.size() == 3
        for (ActivityData childElement in childElements) {
            assert childElement.pubChemDisplayName
            assert childElement.dictElemId
            assert childElement.testConcentration
            assert childElement.testConcentrationUnit
            assert childElement.value
        }
    }


    void "test toDisplay #label"() {
        given:
        ConcentrationResponsePoint concentrationResponsePoint = new ConcentrationResponsePoint(value: responseValue, testConcentration: concentrationValue)
        when:
        String display = concentrationResponsePoint.toDisplay("um")
        then:
        assert display.trim() == expectedDisplay

        where:
        label                                    | responseValue | concentrationValue | expectedDisplay
        "With Response Value"                    | "2.0"         | null               | "2"
        "With Concentration Value"               | ""            | 2.0                | "@ 2 uM"
        "With Response and Concentration values" | "2.0"         | 2.0                | "2 @ 2 uM"
        "With No Value"                          | null          | null               | ""
    }

    void "test displayActivity"() {
        given:
        ConcentrationResponsePoint concentrationResponsePoint = new ConcentrationResponsePoint(value: responseValue)
        when:
        String display = concentrationResponsePoint.displayActivity()
        then:
        assert display.trim() == expectedDisplay
        where:
        label                      | responseValue | expectedDisplay
        "With Response Value"      | "2.0"         | "2"
        "With Null Response Value" | null          | ""

    }

    void "test displayConcentration"() {
        given:
        ConcentrationResponsePoint concentrationResponsePoint = new ConcentrationResponsePoint(testConcentration: concentrationValue)
        when:
        String display = concentrationResponsePoint.displayConcentration("um")
        then:
        assert display.trim() == expectedDisplay
        where:
        label                      | concentrationValue | expectedDisplay
        "With Concentration Value" | 2.0                | "2 uM"
        "With No Value"            | null               | ""

    }


}

