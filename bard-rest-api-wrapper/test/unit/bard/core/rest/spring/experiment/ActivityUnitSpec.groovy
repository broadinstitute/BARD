package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.Readout

@Unroll
class ActivityUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

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

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test activity"() {
        when:
        Activity activity = objectMapper.readValue(ACTIVITY_JSON, Activity.class)
        then:
        assert activity
        assert activity.exptDataId == "197.859140"
        assert activity.eid == 519
        assert activity.cid == 5389251
        assert activity.sid==859140
        assert activity.bardExptId== 197
        assert activity.runset=="default"
        assert activity.outcome== 2
        assert activity.score== 78
        assert activity.classification==0
        assert activity.potency== null
        assert activity.resourcePath=="/exptdata/197.859140"

        assert activity.readouts
        Readout readOut = activity.readouts.get(0)
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

