package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.rest.api.wrapper.Dummy
import bard.core.rest.spring.DataExportRestService

@Unroll
class ActivityConcentrationUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    @Shared
    String JSON_NO_CHILD_ELEMENT_NO_QUALIFIER = '''
   {
        "displayName":"Potency",
        "dictElemId":959,
        "responseUnit":"um",
        "testConcUnit":"uM",
        "value":"25.1189",
        "concResponseSeries":
        {
            "responseUnit":"percent",
            "testConcUnit":"uM",
            "crSeriesDictId":986,
            "concRespParams":{
                "s0":0.4253,
                "sInf":-73.0939,
                "hillCoef":1.3723,
                "logEc50":-4.6
            },
            "concRespPoints":
            [
                {"testConc":9.85385E-4,"value":"1.9624"},
                {"testConc":0.00220231,"value":"-2.447"},
                {"testConc":0.00492462,"value":"0.7184"},
                {"testConc":0.0110123,"value":"0.9477"},
                {"testConc":0.0246231,"value":"2.5592"},
                {"testConc":0.0550569,"value":"-0.1019"},
                {"testConc":0.123107,"value":"2.6532"},
                {"testConc":0.275267,"value":"-0.4138"},
                {"testConc":0.615497,"value":"-0.9334"},
                {"testConc":1.37625,"value":"-5.0823"},
                {"testConc":3.0773,"value":"-1.7523"},
                {"testConc":6.88084,"value":"-9.5959"},
                {"testConc":15.3856,"value":"-32.6444"},
                {"testConc":34.4021,"value":"-51.7455"},
                {"testConc":76.9231,"value":"-73.0939"}
            ],
            "miscData":
            [
                {"displayName":"Phenotype","dictElemId":897,"value":"Inhibitor"},
                {"displayName":"Efficacy","dictElemId":983,"responseUnit":"percent","value":"88.7126"},
                {"displayName":"Analysis Comment","dictElemId":1329},
                {"displayName":"Curve_Description","dictElemId":1329,"value":"Partial curve; partial efficacy"},
                {"displayName":"Fit_R2","dictElemId":980,"value":"0.9913"},
                {"displayName":"Fit_CurveClass","dictElemId":1477,"value":"-2.2"},
                {"displayName":"Excluded_Points","dictElemId":1348,"value":"0 0 0 0 0 0 0 0 0 0 0 0 0 0 0"},
                {"displayName":"Compound QC","dictElemId":1476,"value":"QC'd by SigmaAldrich"}
            ]
        }
    }
'''
    @Shared
    String JSON_WITH_QUALIFIER = '''
    {
        "displayName":"IC50",
        "dictElemId":963,
        "responseUnit":"um",
        "testConcUnit":"uM",
        "value":"1.949",
        "qualifierValue":"=",
        "concResponseSeries":
        {
            "responseUnit":"percent",
            "testConcUnit":"uM",
            "crSeriesDictId":998,
            "concRespParams":
            {
             "s0":4.83421,
             "sInf":131.007,
             "hillCoef":2.17577,
             "logEc50":0.289812
            },
            "concRespPoints":
            [
                {
                  "testConc":108.8,"value":"120.2"
                },
                {
                    "testConc":36.3,"value":"143.3"
                },
                {"testConc":12.1,"value":"141.6"},
                {"testConc":4.0,"value":"95.3"},
                {"testConc":1.3,"value":"32.2"},
                {"testConc":0.4477,"value":"7.9"},
                {"testConc":0.1492,"value":"3.2"},
                {"testConc":0.0497,"value":"2.1"},
                {"testConc":0.0166,"value":"-1.7"},
                {"testConc":0.0055,"value":"14.3"}
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
    }
'''
    @Shared
    String JSON_WITH_CHILD_ELEMENTS = '''
    {
       "displayName":"GluPotencyExperiment1",
       "dictElemId":961,
       "value":"4.04e-007",
       "concResponseSeries":{
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
    }
'''
    DataExportRestService dataExportRestService = Mock(DataExportRestService)

    void "test #label"() {
        given:
        Dummy dummy = new Dummy()
        dummy.dataExportRestService = dataExportRestService
        when:
        ActivityConcentration activityConcentration = objectMapper.readValue(JSON_DATA, ActivityConcentration.class)
        activityConcentration.dummy = dummy
        then:
        assert activityConcentration
        assert activityConcentration.dictElemId
        assert activityConcentration.value

        assert activityConcentration.toDisplay()
        assert activityConcentration.hasPlot()
        assert activityConcentration.getSlope()
        assert activityConcentration.hasChildElements() == hasChildElements
        assert activityConcentration.toDisplay()
        assert activityConcentration.pubChemDisplayName
        final ConcentrationResponseSeries concResponseSeries = activityConcentration.concentrationResponseSeries
        assert concResponseSeries
        assert concResponseSeries.testConcentrationUnit
        assert concResponseSeries.dictElemId
        assert concResponseSeries.curveFitParameters
        final List<ConcentrationResponsePoint> concRespPoints = concResponseSeries.concentrationResponsePoints
        assert concRespPoints
        for (ConcentrationResponsePoint concRespPoint in concRespPoints) {
            assert concRespPoint.testConcentration
            assert concRespPoint.value
        }
        assert concResponseSeries.miscData
        for (ActivityData miscData in concResponseSeries.miscData) {
            assert miscData.pubChemDisplayName
        }
        if (hasChildElements) {

            final ConcentrationResponsePoint concRespPoint = concRespPoints.get(0)
            final List<ActivityData> childElements = concRespPoint.childElements
            assert childElements
            assert childElements.size() == 3
            for (ActivityData childElement in childElements) {
                assert childElement.pubChemDisplayName
            }
        } else {
            assert activityConcentration.getDictionaryLabel()

        }


        where:
        label                                         | JSON_DATA                          | hasChildElements
        "JSON Has no Child Elements, but a qualifier" | JSON_WITH_QUALIFIER                | false
        "JSON Has no Child Elements and no qualifier" | JSON_NO_CHILD_ELEMENT_NO_QUALIFIER | false
        "JSON has Child Elements"                     | JSON_WITH_CHILD_ELEMENTS           | true
    }

    void "Empty fields #label"() {
        when:
        ActivityConcentration activityConcentration = new ActivityConcentration(qualifier: qualifier, value: value)
        then:
        assert !activityConcentration.hasPlot()
        assert !activityConcentration.getSlope()
        assert activityConcentration.toDisplay() == expectedDisplay

        where:
        label                          | qualifier | expectedDisplay | value
        "Qualifier=='=' and has value" | "="       | " 2"            | "2"
        "Qualifier=='>' and has Value" | ">"       | "> 2"           | "2"
        "Qualifier==null"              | ""        | ""              | ""

    }

}

