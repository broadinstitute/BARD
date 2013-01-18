package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import javax.servlet.ServletContext
import org.codehaus.groovy.grails.commons.spring.GrailsWebApplicationContext
import bard.core.rest.spring.DataExportRestService
import org.codehaus.groovy.grails.web.context.ServletContextHolder

@Unroll
class PriorityElementUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    @Shared
    String SP_bid_4567_aid_844255_single_point_with_no_element_nesting_available_JSON = '''
            {"displayName":"Outcome","dictElemId":899,"value":"Active"}
    '''
    @Shared
    String SP_bid_4020_aid_463170_simple_fold_change_average_with_element_nesting_JSON = '''
    {
        "displayName":"Average Fold Change at 0.1 uM",
        "dictElemId":1020,
        "testConcUnit":"uM",
        "testConc":0.1,
        "value":"3.409",
        "childElements":
        [
            {"displayName":"Fold Change at 0.1 uM [1]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.09"},
            {"displayName":"Fold Change at 0.1 uM [2]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.39"},
            {"displayName":"Fold Change at 0.1 uM [3]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.48"},
            {"displayName":"Fold Change at 0.1 uM [4]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.7"}
        ]
    }
    '''
    @Shared
    String CR_NO_SER_bid_573_aid_2802_no_P_C_mapping_JSON = '''
    {
        "displayName":"IC50",
        "dictElemId":959,
        "responseUnit":"um",
        "value":"3.513"
    }
 '''

    @Shared
    String CR_SER_bid_1998_aid_998_good_structure_5_hill_params_JSON = '''
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
             "s0":4.83421,
             "sInf":131.007,
             "hillCoef":2.17577,
             "logEc50":0.289812
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
    String CR_SER_bid_27_aid_2173_good_CR_with_qualifier_JSON = '''
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
    String MULTCONC_bid_3992_aid_504898_flat_resp_JSON = '''
   {
    "displayName":"Average IC90","responseUnit":"um","value":"0.51"
   }
'''
    ServletContext servletContext
    GrailsWebApplicationContext ctx
    DataExportRestService dataExportRestService
    void setup() {
        servletContext = Mock(ServletContext)
        ServletContextHolder.metaClass.static.getServletContext = {servletContext}
        ctx = Mock(GrailsWebApplicationContext)
        dataExportRestService =  Mock(DataExportRestService)
    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
        remove ServletContextHolder
    }

    void "test all #label"() {
        when:
        PriorityElement priorityElement = objectMapper.readValue(currentJSON, PriorityElement.class)
        then:
        servletContext.getAttribute(_)>>{ctx}
        ctx.dataExportRestService()>>{dataExportRestService}

        assert priorityElement
        assert !priorityElement.primaryElements

        assert priorityElement.value
        if (hasConcentration) {
            final ConcentrationResponseSeries concentrationResponseSeries = priorityElement.concentrationResponseSeries
            assert concentrationResponseSeries
            final List<ConcentrationResponsePoint> concentrationResponsePoints = concentrationResponseSeries.concentrationResponsePoints
            assert concentrationResponsePoints
            assert concentrationResponsePoints.size()  > 1
            for (ConcentrationResponsePoint concentrationResponsePoint in concentrationResponsePoints) {
                assert concentrationResponsePoint.testConcentration
                assert concentrationResponsePoint.value
            }
            final CurveFitParameters curveFitParameters = concentrationResponseSeries.curveFitParameters
            assert curveFitParameters
            assert curveFitParameters.s0 == 4.83421
            assert curveFitParameters.hillCoef == 2.17577
            assert curveFitParameters.SInf == 131.007
            assert curveFitParameters.logEc50 == 0.289812
            final List<ActivityData> miscDataList = concentrationResponseSeries.miscData

            assert miscDataList
            for(ActivityData miscData in miscDataList){
                assert miscData.pubChemDisplayName
                assert miscData.value
            }


        } else {
            assert !priorityElement.concentrationResponseSeries
        }
        if (hasChildElement) {
            final List<ActivityData> childElements = priorityElement.childElements
            assert childElements
            assert childElements.size() == 4
            for (ActivityData activityData : childElements) {
                assert activityData.pubChemDisplayName
                assert activityData.dictElemId
                assert activityData.testConcentration
                assert activityData.testConcentrationUnit
                assert activityData.value
            }
        }
        else {
            assert !priorityElement.childElements
        }

        where:
        label                   | responseClass | currentJSON                                                                 | hasChildElement | hasConcentration
        "CR_NO_SER"             | "CR_NO_SER"   | CR_NO_SER_bid_573_aid_2802_no_P_C_mapping_JSON                              | false           | false
        "CR_SER"                | "CR_SER"      | CR_SER_bid_1998_aid_998_good_structure_5_hill_params_JSON                   | false           | true
        "CR_SER_WITH_QUALIFIER" | "CR_SER"      | CR_SER_bid_27_aid_2173_good_CR_with_qualifier_JSON                          | false           | true
        "MULTCONC_BIG"          | "MULTCONC"    | MULTCONC_bid_3992_aid_504898_flat_resp_JSON                                 | false           | false
        "SP"                    | "SP"          | SP_bid_4567_aid_844255_single_point_with_no_element_nesting_available_JSON  | false           | false
        "SP_NO_NESTING"         | "SP"          | SP_bid_4020_aid_463170_simple_fold_change_average_with_element_nesting_JSON | true            | false
    }
}

