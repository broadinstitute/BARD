package molspreadsheet

import bard.core.rest.spring.BiologyRestService
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.compounds.CompoundSummary
import com.fasterxml.jackson.databind.ObjectMapper
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 7/5/13
 * Time: 1:56 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@TestFor(RingManagerService)
class RingManagerServiceUnitSpec extends Specification{
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    RingManagerService ringManagerService
    CompoundRestService compoundRestService
    BiologyRestService biologyRestService
    CompoundSummary compoundSummary


    void setup() {
        compoundSummary = objectMapper.readValue(COMPOUND_SUMMARY, CompoundSummary.class)
        this.ringManagerService = Mock(RingManagerService)
        this.compoundRestService = Mock(CompoundRestService)
        this.biologyRestService = Mock(BiologyRestService)
//        service.gen   ringManagerService = ringManagerService
    }

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
          {
            "aid": 492961,
            "bardAssayId": 3377,
            "capAssayId": 1284,
            "category": 2,
            "type": 0,
            "summary": 0,
            "assays": 0,
            "classification": 0,
            "name": "qHTS Assay to Find Inhibitors of T. brucei phosphofructokinase: hit validation",
            "source": "NCGC",
            "grantNo": null,
            "deposited": null,
            "updated": null,
            "documents": [
            19084537,
            15955817,
            11512153
            ],
            "targets": [ ],
            "experiments": [
            3377
            ],
            "projects": [
            282
            ],
            "minimumAnnotations": {
                "detection method type": "bioluminescence",
                "assay footprint": "384-well plate",
                "detection instrument name": "PerkinElmer EnVision",
                "assay format": "cell-based format",
                "assay type": "cytotoxicity assay"
            },
            "kegg_disease_names": [ ],
            "kegg_disease_cat": [ ],
            "resourcePath": "/assays/3377"
           }
       ],
       "nhit": 1,
       "hitAssays":
       [
                    {
            "aid": 492961,
            "bardAssayId": 3377,
            "capAssayId": 1284,
            "category": 2,
            "type": 0,
            "summary": 0,
            "assays": 0,
            "classification": 0,
            "name": "qHTS Assay to Find Inhibitors of T. brucei phosphofructokinase: hit validation",
            "source": "NCGC",
            "grantNo": null,
            "deposited": null,
            "updated": null,
            "documents": [
            19084537,
            15955817,
            11512153
            ],
            "targets": [ ],
            "experiments": [
            3377
            ],
            "projects": [
            282
            ],
            "minimumAnnotations": {
                "detection method type": "bioluminescence",
                "assay footprint": "384-well plate",
                "detection instrument name": "PerkinElmer EnVision",
                "assay format": "cell-based format",
                "assay type": "cytotoxicity assay"
            },
            "kegg_disease_names": [ ],
            "kegg_disease_cat": [ ],
            "resourcePath": "/assays/3377"
           }
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


//    void "Test convertCompoundIntoSunburst" () {
//        given:
//        final int a = 1
//
//        when:
//        LinkedHashMap<String,Object> sunburst  =  service.convertCompoundIntoSunburst(compoundSummary, true, true )
//
//        then:
//        assert true
//    }

//    /***
//     * degenerate case
//     */
//    void "Test Degenerate generateLinkedData"() {
//        given:
//        LinkedHashMap<String, Object> compoundSummaryPlusId = null
//
//        when:
//        service.generateLinkedData(compoundSummaryPlusId, 47L)
//
//        then:
//        assert true
//    }
//
//
//    void "Test generateLinkedData"() {
//        given:
//        LinkedHashMap<String, Object> compoundSummaryPlusId = [:]
//        compoundSummaryPlusId['id'] = "16760208"
//        compoundSummaryPlusId['compoundSummary'] = compoundSummary
//        when:
//        service.generateLinkedData(compoundSummaryPlusId, 47L)
//
//        then:
//        assert true
//    }
//
//


}
