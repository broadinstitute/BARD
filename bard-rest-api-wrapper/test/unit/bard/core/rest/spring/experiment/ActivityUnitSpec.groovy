package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ActivityUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String ACTIVITY_WITH_RESULT_JSON = '''
    {
        "exptDataId": "4020.92092801",
        "eid": 463170,
        "cid": 45100431,
        "sid": 92092801,
        "bardExptId": 4020,
        "resultJson": "{\\"responseClass\\":\\"SP\\",\\"bardExptId\\":4020,\\"sid\\":92092801,\\"cid\\":45100431,\\"priorityElements\\":[{\\"displayName\\":\\"Average Fold Change at 0.1 uM\\",\\"dictElemId\\":1020,\\"testConcUnit\\":\\"uM\\",\\"testConc\\":0.1,\\"value\\":\\"3.409\\",\\"childElements\\":[{\\"displayName\\":\\"Standard Deviation\\",\\"dictElemId\\":613,\\"value\\":\\"0.26\\"},{\\"displayName\\":\\"Fold Change at 0.1 uM [1]\\",\\"dictElemId\\":1020,\\"testConcUnit\\":\\"uM\\",\\"testConc\\":0.1,\\"value\\":\\"3.09\\"},{\\"displayName\\":\\"Fold Change at 0.1 uM [2]\\",\\"dictElemId\\":1020,\\"testConcUnit\\":\\"uM\\",\\"testConc\\":0.1,\\"value\\":\\"3.39\\"},{\\"displayName\\":\\"Fold Change at 0.1 uM [3]\\",\\"dictElemId\\":1020,\\"testConcUnit\\":\\"uM\\",\\"testConc\\":0.1,\\"value\\":\\"3.48\\"},{\\"displayName\\":\\"Fold Change at 0.1 uM [4]\\",\\"dictElemId\\":1020,\\"testConcUnit\\":\\"uM\\",\\"testConc\\":0.1,\\"value\\":\\"3.7\\"}]}],\\"rootElements\\":[{\\"displayName\\":\\"Dummy\\",\\"dictElemId\\":899,\\"value\\":\\"Inactive\\"},{\\"displayName\\":\\"Score\\",\\"dictElemId\\":898,\\"value\\":\\"100\\"}]}",
        "runset": "default",
        "outcome": 1,
        "score": 100,
        "classification": null,
        "potency": null,
        "readouts": null,
        "resourcePath": "/exptdata/4020.92092801"
    }

'''

    final String ACTIVITY_JSON = '''
{
           "exptDataId": "197.859140",
           "eid": 519,
           "cid": 5389251,
           "sid": 859140,
           "bardExptId": 197,
           "runset": "default",
           "outcome": 2,
           "score": 78,
           "classification": 0,
           "potency": null,
           "readouts":
           [
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
           ],
           "resourcePath": "/exptdata/197.859140"
       }
'''



    void "test activity with Result JSON"(){
        when:
        Activity activity = objectMapper.readValue(ACTIVITY_WITH_RESULT_JSON, Activity.class)
        then:
        assert activity
        assert activity.exptDataId=="4020.92092801"
        assert activity.eid==463170
        assert activity.cid==45100431
        assert activity.sid==92092801
        assert activity.bardExptId==4020
        assert activity.runset=="default"
        assert activity.outcome==1
        assert activity.score==100
        assert activity.classification==null
        assert activity.potency==null
        assert activity.readouts==null
        assert activity.resourcePath=="/exptdata/4020.92092801"

        assert activity.resultJson
        final ResultData resultData = activity.resultData
        assert resultData
        assert !resultData.hasConcentrationResponseSeries()
        assert !activity.hasConcentrationSeries()
        assert resultData.responseClassEnum == ResponseClassEnum.SP
        assert !resultData.getOutcome()



    }
    void "test activity"() {
        when:
        Activity activity = objectMapper.readValue(ACTIVITY_JSON, Activity.class)
        then:
        assert activity
        assert activity.exptDataId == "197.859140"
        assert activity.eid == 519
        assert activity.cid == 5389251
        assert activity.sid == 859140
        assert activity.bardExptId == 197
        assert activity.runset == "default"
        assert activity.outcome == 2
        assert activity.score == 78
        assert activity.classification == 0
        assert activity.potency == null
        assert activity.resourcePath == "/exptdata/197.859140"

        assert !activity.resultJson
        assert !activity.resultData
        assert !activity.hasConcentrationSeries()

        assert activity.readouts
        Readout readOut = activity.readouts.get(0)
        assert readOut
        assert !readOut.responseUnit
        assert readOut.concentrationUnits
        assert readOut.numberOfPoints == 9
        assert readOut.name == "INHIBITION"
        assert readOut.s0 == 3.57
        assert readOut.getSInf() == 100
        assert readOut.coef == 1.35
        assert readOut.slope == 0.0000033999999999999996
        assert readOut.getCr().size() == 9
        assert readOut.getConcAsList().size() == 9
        assert readOut.getResponseAsList().size() == 9
    }

}

