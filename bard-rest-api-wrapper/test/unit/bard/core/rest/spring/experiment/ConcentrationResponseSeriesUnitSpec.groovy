package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.rest.api.wrapper.Dummy
import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.util.Node

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

    DataExportRestService dataExportRestService = Mock(DataExportRestService)


    void "test JSON #label"() {
        given:
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        when:
        ConcentrationResponseSeries concentrationResponseSeries = objectMapper.readValue(JSON_DATA, ConcentrationResponseSeries.class)
        concentrationResponseSeries.dummy = dummy
        final List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
        then:
        assert !concentrationResponseSeries.responseUnit
        assert !concentrationResponseSeries.getDictionaryLabel()
        assert !concentrationResponseSeries.getDictionaryDescription()
        assert !concentrationResponseSeries.getYAxisLabel()

        assert concentrationResponsePoints
        assert concentrationResponsePoints.size() == 1
        final List<ActivityData> childElements = concentrationResponsePoints.get(0).childElements
        assert childElements
        assert childElements.size() == 1
        for (ActivityData childElement in childElements) {
            assert childElement.pubChemDisplayName
            assert childElement.dictElemId
            assert childElement.testConcentration
            assert childElement.testConcentrationUnit
            assert childElement.value
        }
        final List<ActivityData> miscDataList = concentrationResponseSeries.miscData
        assert miscDataList
        assert miscDataList.size() == 5
        for (ActivityData activityData in miscDataList) {
            assert activityData.pubChemDisplayName
            assert activityData.value
        }

    }


    void "test getDictionaryDescription #label"() {
        given:
        ConcentrationResponseSeries concentrationResponseSeries = new ConcentrationResponseSeries(dictElemId: dictElemId, responseUnit: "pubChem")
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        concentrationResponseSeries.dummy = dummy
        when:
        final String foundDescription = concentrationResponseSeries.getDictionaryDescription()

        then:
        expectedNumExecutions * dummy.dataExportRestService.findDictionaryElementById(_) >> {dictionaryElement}
        assert expectedDescription == foundDescription
        where:
        label                                     | dictElemId | dictionaryElement                           | expectedDescription | expectedNumExecutions
        "Has a DictElemId and in dictionary"      | 222        | new Node(description: "label") | "label"             | 1
        "Has a DictElemId, but not in dictionary" | 221        | null                                        | ""                  | 1
        "Has no DictElemId"                       | 0          | null                                        | ""                  | 0

    }

    void "test getDictionaryLabel #label"() {
        given:
        ConcentrationResponseSeries concentrationResponseSeries = new ConcentrationResponseSeries(dictElemId: dictElemId)
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        concentrationResponseSeries.dummy = dummy
        when:
        final String foundLabel = concentrationResponseSeries.getDictionaryLabel()

        then:
        expectedNumExecutions * dummy.dataExportRestService.findDictionaryElementById(_) >> {dictionaryElement}
        assert expectedLabel == foundLabel
        where:
        label                                     | dictElemId | dictionaryElement                     | expectedLabel | expectedNumExecutions
        "Has a DictElemId and in dictionary"      | 222        | new Node(label: "label") | "label"       | 1
        "Has a DictElemId, but not in dictionary" | 221        | null                                  | ""            | 1
        "Has no DictElemId"                       | 0          | null                                  | ""            | 0

    }

    void "sortedActivities"() {
        given:
        ConcentrationResponseSeries concentrationResponseSeries =
            new ConcentrationResponseSeries(concentrationResponsePoints:
                    [
                            new ConcentrationResponsePoint(value: null, testConcentration: 2.0),
                            new ConcentrationResponsePoint(value: "3", testConcentration: 3.0),
                            new ConcentrationResponsePoint(value: "2", testConcentration: 2.0)
                    ])

        when:
        final List<Double> activities = concentrationResponseSeries.sorterdActivities()
        then:
        assert activities
        assert activities.size() == 2
        assert activities.get(0) < activities.get(1)

    }
    void "test sortedActivities with various params"() {
        given:
        ConcentrationResponsePoint concentrationResponsePoint = new ConcentrationResponsePoint(value: responseValue)
        ConcentrationResponseSeries concentrationResponseSeries =
            new ConcentrationResponseSeries(concentrationResponsePoints:[concentrationResponsePoint])
        when:
        final List<Double> activities = concentrationResponseSeries.sorterdActivities()
        then:
        assert activities.size() == expectedSize
        where:
        label                               | responseValue | expectedSize
        "With Response Value"               | "2.0"         | 1
        "With Response Value, not a number" | "No Number"   | 0
        "With Null Response Value"          | null          | 0

    }
    void toDoseResponsePoints() {
        given:
        ConcentrationResponseSeries concentrationResponseSeries =
            new ConcentrationResponseSeries(concentrationResponsePoints:
                    [
                            new ConcentrationResponsePoint(value: null, testConcentration: 2.0),
                            new ConcentrationResponsePoint(value: "3", testConcentration: 3.0),
                            new ConcentrationResponsePoint(value: "3", testConcentration: null),
                            new ConcentrationResponsePoint(value: "2", testConcentration: 2.0)
                    ])


        when:
        final Map<String, List<Double>> points = ConcentrationResponseSeries.toDoseResponsePoints(concentrationResponseSeries.concentrationResponsePoints)
        then:
        assert points.activities.size() == 2
        assert points.concentrations.size() == 2

    }


}

