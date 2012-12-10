package bard.core.rest.spring.compound

import bard.core.rest.spring.compounds.CompoundSummary
import bard.core.rest.spring.experiment.Activity
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CompoundSummaryUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String COMPOUND_SUMMARY = '''
    {
       "testedExptdata":
       [
           {
               "exptDataId": "3377.26757972",
               "eid": 492961,
               "cid": 16760208,
               "sid": 26757972,
               "bardExptId": 3377,
               "runset": "default",
               "outcome": 1,
               "score": 0,
               "classification": 1,
               "potency": null,
               "readouts":
               [
                   {
                       "name": "Activity",
                       "s0": null,
                       "sInf": null,
                       "hill": null,
                       "ac50": null,
                       "cr":
                       [
                           [
                               8.220000192522999e-9,
                               -0.2345
                           ],
                           [
                               4.10999990999699e-8,
                               2.4632
                           ],
                           [
                               2.06000000238419e-7,
                               -2.8264
                           ],
                           [
                               0.00000102999997138977,
                               -1.9348
                           ],
                           [
                               0.0000051399998664856,
                               -0.1097
                           ],
                           [
                               0.000024312499999999998,
                               0.29
                           ],
                           [
                               0.0000514000015258789,
                               -7.8803
                           ]
                       ],
                       "npoint": 7,
                       "concUnit": "M",
                       "responseUnit": null
                   }
               ],
               "resourcePath": "/exptdata/3377.26757972"
           },
           {
               "exptDataId": "1420.26757972",
               "eid": 624044,
               "cid": 16760208,
               "sid": 26757972,
               "bardExptId": 1420,
               "runset": "default",
               "outcome": 2,
               "score": 90,
               "classification": null,
               "potency": 8.9058,
               "readouts":
               [
                   {
                       "name": "W460-Activity",
                       "s0": -0.0583,
                       "sInf": -100.294,
                       "hill": 1.2221,
                       "ac50": 0.000008905799999999999,
                       "cr":
                       [
                           [
                               1.0999999940395401e-9,
                               -2.492
                           ],
                           [
                               1.0999999940395401e-9,
                               -0.1863
                           ],
                           [
                               1.0999999940395401e-9,
                               -4.46984
                           ],
                           [
                               5.47999981790781e-9,
                               -34.186
                           ],
                           [
                               5.47999981790781e-9,
                               -10.2118
                           ],
                           [
                               5.47999981790781e-9,
                               -21.2604
                           ],
                           [
                               2.74000000208616e-8,
                               -1.933
                           ],
                           [
                               2.74000000208616e-8,
                               -5.1577
                           ],
                           [
                               2.74000000208616e-8,
                               1.69217
                           ],
                           [
                               1.3699999451637302e-7,
                               0.6076
                           ],
                           [
                               1.3699999451637302e-7,
                               -6.6305
                           ],
                           [
                               1.3699999451637302e-7,
                               1.6709
                           ],
                           [
                               7.11000978946686e-7,
                               -3.7281
                           ],
                           [
                               7.11000978946686e-7,
                               0.0576
                           ],
                           [
                               7.11000978946686e-7,
                               4.04171
                           ],
                           [
                               0.00000350651001930237,
                               -36.3845
                           ],
                           [
                               0.00000350651001930237,
                               -19.7831
                           ],
                           [
                               0.00000350651001930237,
                               -13.6112
                           ],
                           [
                               0.00000765999984741211,
                               -64.4744
                           ],
                           [
                               0.00000765999984741211,
                               -39.2249
                           ],
                           [
                               0.00000765999984741211,
                               -17.0927
                           ],
                           [
                               0.0000171000003814697,
                               -72.572
                           ],
                           [
                               0.0000171000003814697,
                               -67.1737
                           ],
                           [
                               0.0000171000003814697,
                               6.38658
                           ],
                           [
                               0.00003829999923706049,
                               -94.9752
                           ],
                           [
                               0.00003829999923706049,
                               -84.0315
                           ],
                           [
                               0.00003829999923706049,
                               -0.119884
                           ]
                       ],
                       "npoint": 27,
                       "concUnit": "M",
                       "responseUnit": null
                   }
               ],
               "resourcePath": "/exptdata/1420.26757972"
           }
       ],
       "testedAssays":
       [
           "/assays/3377",
           "/assays/1420"
       ],
       "nhit": 1,
       "hitAssays":
       [
           "/assays/1420"
       ],
       "ntest": 2,
       "hitExptdata":
       [
           {
               "exptDataId": "1420.26757972",
               "eid": 624044,
               "cid": 16760208,
               "sid": 26757972,
               "bardExptId": 1420,
               "runset": "default",
               "outcome": 2,
               "score": 90,
               "classification": null,
               "potency": 8.9058,
               "readouts":
               [
                   {
                       "name": "W460-Activity",
                       "s0": -0.0583,
                       "sInf": -100.294,
                       "hill": 1.2221,
                       "ac50": 0.000008905799999999999,
                       "cr":
                       [
                           [
                               1.0999999940395401e-9,
                               -2.492
                           ],
                           [
                               1.0999999940395401e-9,
                               -0.1863
                           ],
                           [
                               1.0999999940395401e-9,
                               -4.46984
                           ],
                           [
                               5.47999981790781e-9,
                               -34.186
                           ],
                           [
                               5.47999981790781e-9,
                               -10.2118
                           ],
                           [
                               5.47999981790781e-9,
                               -21.2604
                           ],
                           [
                               2.74000000208616e-8,
                               -1.933
                           ],
                           [
                               2.74000000208616e-8,
                               -5.1577
                           ],
                           [
                               2.74000000208616e-8,
                               1.69217
                           ],
                           [
                               1.3699999451637302e-7,
                               0.6076
                           ],
                           [
                               1.3699999451637302e-7,
                               -6.6305
                           ],
                           [
                               1.3699999451637302e-7,
                               1.6709
                           ],
                           [
                               7.11000978946686e-7,
                               -3.7281
                           ],
                           [
                               7.11000978946686e-7,
                               0.0576
                           ],
                           [
                               7.11000978946686e-7,
                               4.04171
                           ],
                           [
                               0.00000350651001930237,
                               -36.3845
                           ],
                           [
                               0.00000350651001930237,
                               -19.7831
                           ],
                           [
                               0.00000350651001930237,
                               -13.6112
                           ],
                           [
                               0.00000765999984741211,
                               -64.4744
                           ],
                           [
                               0.00000765999984741211,
                               -39.2249
                           ],
                           [
                               0.00000765999984741211,
                               -17.0927
                           ],
                           [
                               0.0000171000003814697,
                               -72.572
                           ],
                           [
                               0.0000171000003814697,
                               -67.1737
                           ],
                           [
                               0.0000171000003814697,
                               6.38658
                           ],
                           [
                               0.00003829999923706049,
                               -94.9752
                           ],
                           [
                               0.00003829999923706049,
                               -84.0315
                           ],
                           [
                               0.00003829999923706049,
                               -0.119884
                           ]
                       ],
                       "npoint": 27,
                       "concUnit": "M",
                       "responseUnit": null
                   }
               ],
               "resourcePath": "/exptdata/1420.26757972"
           }
       ]
    }
    '''


    void "test serialization to CompoundSummary"() {
        when:
        final CompoundSummary compoundSummary = objectMapper.readValue(COMPOUND_SUMMARY, CompoundSummary.class)
        then:
        assert compoundSummary

        assert compoundSummary.hitAssays.size() == 1
        assert compoundSummary.nhit == 1

        assert compoundSummary.ntest == 2
        assert compoundSummary.testedAssays.size() == 2

        final List<Activity> exptdata = compoundSummary.hitExptdata
        assert exptdata
        assert exptdata.size() == 1

        for (Activity activity : exptdata) {
            assert activity
            assert activity.readouts
        }

        final List<Activity> testedExptdata = compoundSummary.testedExptdata
        assert testedExptdata
        assert testedExptdata.size() == 2
        for (Activity activity : testedExptdata) {
            assert activity
            assert activity.readouts
        }
    }


}
