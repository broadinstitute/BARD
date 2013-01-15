package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ConcentrationResponseSeriesUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    @Shared
    String JSON_DATA = '''
    {
          "testConcUnit":"uM",
          "crSeriesDictId":1016,
          "concRespParams":{
             "s0":null,
             "sInf":null,
             "hillCoef":null,
             "logEc50":-6.393618634889395
          },
          "concRespPoints":
          [
             {
                "testConc":1000.0,
                "value":"104.181",
                "childElements":
                [
                   {
                      "displayName":"Rep2ForExperiment3_1000_uM",
                      "dictElemId":1016,
                      "testConcUnit":"uM",
                      "testConc":1000.0,
                      "value":"105.693"
                   }
                ]
             }
          ],
        "miscData":
        [
            {"displayName":"Qualifier","value":"="},
            {"displayName":"Hill dS","dictElemId":922,"value":"126.173"},
            {"displayName":"Chi Square","dictElemId":979,"value":"498.118"},
            {"displayName":"Rsquare","dictElemId":980,"value":"0.980837"},
            {"displayName":"Number of DataPoints","dictElemId":1397,"value":"30"}
        ]
    }
    '''



    void "test JSON #label"() {
        when:
        ConcentrationResponseSeries concentrationResponseSeries = objectMapper.readValue(JSON_DATA, ConcentrationResponseSeries.class)
        final List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
        then:
        assert concentrationResponsePoints
        assert concentrationResponsePoints.size() == 1
        final List<ActivityData> childElements = concentrationResponsePoints.get(0).childElements
        assert childElements
        assert childElements.size() == 1
        for(ActivityData childElement in childElements){
            assert childElement.displayName
            assert childElement.dictElemId
            assert childElement.testConcentration
            assert childElement.testConcentrationUnit
            assert childElement.value
        }
        final List<ActivityData> miscDataList = concentrationResponseSeries.miscData
        assert miscDataList
        assert miscDataList.size() == 5
        for(ActivityData activityData in miscDataList){
            assert activityData.displayName
            assert activityData.value
        }

    }


}

