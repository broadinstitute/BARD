package bard.core.rest.spring.experiment

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ResultDataUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    @Shared
    String active = "Active"
    @Shared
    String inactive = "Inactive"
    @Shared
    String SP_bid_4567_aid_844255_single_point_with_no_element_nesting_available_JSON = '''
    {
        "responseClass":"SP",
        "bardExptId":4567,
        "sid":844255,
        "cid":646579,
        "priorityElements":
        [
            {"displayName":"%Inhibition at 5 uM","dictElemId":998,"testConcUnit":"um","testConc":5.0,"value":"85.1"}
        ],
        "rootElements":
        [
            {"displayName":"Outcome","dictElemId":899,"value":"Active"},
            {"displayName":"Score","dictElemId":898,"value":"35"},
            {"displayName":"Mean_NC","value":"3.8"},
            {"displayName":"StdDev_NC","value":"2.7"},
            {"displayName":"Mean_PC","value":"83.6"},
            {"displayName":"StdDev_PC","value":"10.5"}
        ]
    }
    '''
    @Shared
    String SP_bid_4020_aid_463170_simple_fold_change_average_with_element_nesting_JSON = '''
    {
        "responseClass":"SP",
        "bardExptId":4020,
        "sid":92092801,
        "cid":45100431,
        "priorityElements":
        [
            {
                "displayName":"Average Fold Change at 0.1 uM",
                "dictElemId":1020,
                "testConcUnit":"uM",
                "testConc":0.1,
                "value":"3.409",
                "childElements":
                [
                    {"displayName":"Standard Deviation","dictElemId":613,"value":"0.26"},
                    {"displayName":"Fold Change at 0.1 uM [1]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.09"},
                    {"displayName":"Fold Change at 0.1 uM [2]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.39"},
                    {"displayName":"Fold Change at 0.1 uM [3]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.48"},
                    {"displayName":"Fold Change at 0.1 uM [4]","dictElemId":1020,"testConcUnit":"uM","testConc":0.1,"value":"3.7"}
                ]
            }
        ],
        "rootElements":
        [
            {"displayName":"Outcome","dictElemId":899,"value":"Inactive"},
            {"displayName":"Score","dictElemId":898,"value":"100"}
        ]
    }
    '''
    @Shared
    String CR_NO_SER_bid_573_aid_2802_no_P_C_mapping_JSON = '''
    {
        "responseClass":"CR_NO_SER",
        "bardExptId":573,
        "sid":846218,
        "cid":72574,
        "priorityElements":
        [
            {
                "displayName":"IC50",
                "dictElemId":959,
                "responseUnit":"um",
                "value":"3.513"
            }
        ],
        "rootElements":
        [
            {
                "displayName":"Outcome",
                "dictElemId":899,
                "value":"Active"
            },
            {
                "displayName":"Score",
                "dictElemId":898,"value":"92"
            },
            {
                "displayName":"Qualifier","value":"="
            },
            {
                "displayName":"LogIC50",
                "dictElemId":968,"value":"-5.454"
            },
            {
                "displayName":"Hill Slope",
                "dictElemId":919,"value":"0.842613"
            },
            {
                "displayName":"Hill S0",
                "dictElemId":920,"value":"1.74951"
             },
             {
                "displayName":"Hill Sinf",
                "dictElemId":921,
                "value":"180.765"
             },
             {
                "displayName":"Hill dS",
                "dictElemId":922,
                "value":"179.015"
             },
             {
                "displayName":"Chi Square","dictElemId":979,"value":"66.3097"
             },
             {"displayName":"Rsquare","dictElemId":980,"value":"0.986998"},{"displayName":"Number of DataPoints","value":"30"},
             {"displayName":"Inhibition at 3.0 nM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.003,"value":"0.3"},
             {"displayName":"Inhibition at 9.1 nM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.0091,"value":"2"},
             {"displayName":"Inhibition at 27.3 nM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.0273,"value":"2.4"},
             {"displayName":"Inhibition at 81.8 nM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.0818,"value":"8"},
             {"displayName":"Inhibition at 245.5 nM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.2455,"value":"8"},
             {"displayName":"Inhibition at 736.6 nM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.7366,"value":"16.9"},
             {"displayName":"Inhibition at 2.2 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":2.2,"value":"37.1"},
             {"displayName":"Inhibition at 6.6 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":6.6,"value":"72"},
             {"displayName":"Inhibition at 19.9 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":19.9,"value":"111.6"},
             {"displayName":"Inhibition at 59.7 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":59.7,"value":"146.2"}
        ]
    }
 '''

    @Shared
    String CR_SER_bid_1998_aid_998_good_structure_5_hill_params_JSON = '''
    {
        "responseClass":"CR_SER",
        "bardExptId":1998,
        "sid":856002,
        "cid":3194,
        "priorityElements":
        [
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
        ],
        "rootElements":
        [
            {"displayName":"Outcome","dictElemId":899,"value":"Active"},
            {"displayName":"Score","dictElemId":898,"value":"40"}
        ]
    }
    '''
    @Shared
    String MULTCONC_smaller_bid_2466_aid_504674_flat_resp_JSON = '''
    {
        "responseClass":"MULTCONC",
        "bardExptId":2466,
        "sid":109967232,
        "cid":50897761,
        "priorityElements":[
            {"displayName":"CC50","responseUnit":"um","value":"12.99"}
        ],
        "rootElements":[
            {"displayName":"Outcome","dictElemId":899,"value":"Active"},
            {"displayName":"Score","dictElemId":898,"value":"96"},
            {"displayName":"CC50 Modifier"},
            {"displayName":"% Cell Viability @ 50 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":50.0,"value":"25.07"},
            {"displayName":"% Cell Viability @ 25 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":25.0,"value":"26.48"},
            {"displayName":"% Cell Viability @ 12.5 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":12.5,"value":"54.96"},
            {"displayName":"% Cell Viability @ 6.25 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":6.25,"value":"77.06"},
            {"displayName":"% Cell Viability @ 3.125 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":3.125,"value":"79.08"},
            {"displayName":"% Cell Viability @ 1.563 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":1.563,"value":"74.98"},
            {"displayName":"% Cell Viability @ 0.781uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.781,"value":"82.37"},
            {"displayName":"% Cell Viability @ 0.391 uM","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.391,"value":"87.28"}
        ]
    }
'''
    @Shared
    String CR_SER_bid_27_aid_2173_good_CR_with_qualifier_JSON = '''
    {
        "responseClass":"CR_SER",
        "bardExptId":27,
        "sid":49829387,
        "cid":3531704,
        "priorityElements":
        [
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
        ],
        "rootElements":
        [
            {"displayName":"Outcome","dictElemId":899,"value":"Active"},
            {"displayName":"Score","dictElemId":898,"value":"97"}
        ]
    }
    '''
    @Shared
    String UNCLASS_bid_2819_aid_2373_no_annot_JSON = '''
    {
        "responseClass":"UNCLASS",
        "bardExptId":2819,
        "sid":57644019,
        "cid":2950007,
        "priorityElements":[],
        "rootElements":
        [
            {"displayName":"Outcome","dictElemId":899,"value":"Active"},
            {"displayName":"Score","dictElemId":898,"value":"43"},
            {"displayName":"BoundGTP_Compound_1.56nanoM","value":"229"},
            {"displayName":"BoundGTP_Compound_3.125nanoM","value":"382"},
            {"displayName":"BoundGTP_Compound_6.25nanoM","value":"650"},
            {"displayName":"BoundGTP_Compound_12.5nanoM","value":"960"},
            {"displayName":"BoundGTP_Compound_25nanoM","value":"1345"},
            {"displayName":"BoundGTP_Compound_50nanoM","value":"1808"},
            {"displayName":"BoundGTP_Compound_100nanoM","value":"1959"},
            {"displayName":"FitBmax_Compound","value":"2318"},
            {"displayName":"FitKd_Compound_nanoM","responseUnit":"nm","value":"16.72"},
            {"displayName":"BoundGTP_DMSO_1.56nanoM","value":"522"},
            {"displayName":"BoundGTP_DMSO_3.125nanoM","value":"1108"},
            {"displayName":"BoundGTP_DMSO_6.25nanoM","value":"1749"},
            {"displayName":"BoundGTP_DMSO_12.5nanoM","value":"1996"},
            {"displayName":"BoundGTP_DMSO_25nanoM","value":"3031"},
            {"displayName":"BoundGTP_DMSO_50nanoM","value":"3055"},
            {"displayName":"BoundGTP_DMSO_100nanoM","value":"3436"},
            {"displayName":"FitBmax_DMSO","value":"3675"},
            {"displayName":"FitKd_DMSO_nanoM","responseUnit":"nm","value":"7.864"}
        ]
    }
   '''
    @Shared
    String MULTCONC_bid_3992_aid_504898_flat_resp_JSON = '''
   {
        "responseClass":"MULTCONC",
        "bardExptId":3992,
        "sid":103147611,
        "cid":2792221,
        "priorityElements":
        [
            {"displayName":"Average IC90","responseUnit":"um","value":"0.51"}
        ],
        "rootElements":
        [
            {"displayName":"Outcome","dictElemId":899,"value":"Active"},
            {"displayName":"Score","dictElemId":898,"value":"100"},
            {"displayName":"Average IC90 Modifier"},
            {"displayName":"Average IC50 Modifier"},
            {"displayName":"Average IC50","dictElemId":959,"responseUnit":"um","value":"0.36"},
            {"displayName":"IC90 Modifier Rep 1"},
            {"displayName":"IC90 Rep 1","responseUnit":"um","value":"0.56"},
            {"displayName":"IC50 Modifier Rep 1"},
            {"displayName":"IC50 Rep 1","dictElemId":959,"responseUnit":"um","value":"0.35"},
            {"displayName":"IC50 Std Dev Rep 1","value":"0.01"},
            {"displayName":"IC50 Hill Slope Rep 1","dictElemId":919,"value":"2.94"},
            {"displayName":"IC50 Norm Chi2 Rep 1","value":"4.58"},
            {"displayName":"IC90 Modifier Rep 2"},
            {"displayName":"IC90 Rep 2","responseUnit":"um","value":"0.51"},
            {"displayName":"IC50 Modifier Rep 2"},
            {"displayName":"IC50 Rep 2","dictElemId":959,"responseUnit":"um","value":"0.37"},
            {"displayName":"IC50 Std Dev Rep 2","value":"0.01"},
            {"displayName":"IC50 Hill Slope Rep 2","dictElemId":919,"value":"3.42"},
            {"displayName":"IC50 Norm Chi2 Rep 2","value":"6.72"},
            {"displayName":"IC90 Modifier Rep 3"},
            {"displayName":"IC90 Rep 3","responseUnit":"um","value":"0.45"},
            {"displayName":"IC50 Modifier Rep 3"},
            {"displayName":"IC50 Rep 3","dictElemId":959,"responseUnit":"um","value":"0.35"},
            {"displayName":"IC50 Std Dev Rep 3","value":"0.04"},
            {"displayName":"IC50 Hill Slope Rep 3","dictElemId":919,"value":"3.39"},
            {"displayName":"IC50 Norm Chi2 Rep 3","value":"9.09"},
            {"displayName":"IC90 Modifier Rep 4"},
            {"displayName":"IC90 Rep 4","responseUnit":"um"},
            {"displayName":"IC50 Modifier Rep 4"},
            {"displayName":"IC50 Rep 4","responseUnit":"um"},{"displayName":"IC50 Std Dev Rep 4"},
            {"displayName":"IC50 Hill Slope Rep 4","dictElemId":919},
            {"displayName":"IC50 Norm Chi2 Rep 4"},
            {"displayName":"% Inhibition @ 100 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":100.0},
            {"displayName":"% Inhibition @ 50 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":50.0},
            {"displayName":"% Inhibition @ 25 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":25.0},
            {"displayName":"% Inhibition @ 12.5 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":12.5},
            {"displayName":"% Inhibition @ 6.25 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":6.25,"value":"99.76"},
            {"displayName":"% Inhibition @ 3.125 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":3.125,"value":"99.77"},
            {"displayName":"% Inhibition @ 1.563 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":1.563,"value":"99.12"},
            {"displayName":"% Inhibition @ 0.781 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.781,"value":"96.03"},
            {"displayName":"% Inhibition @ 0.391 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.391,"value":"64.17"},
            {"displayName":"% Inhibition @ 0.195 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.195,"value":"5.43"},
            {"displayName":"% Inhibition @ 0.098 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.098,"value":"8.05"},
            {"displayName":"% Inhibition @ 0.049 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.049,"value":"-2.58"},
            {"displayName":"% Inhibition @ 0.024 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.024,"value":"3.92"},
            {"displayName":"% Inhibition @ 0.012 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.012,"value":"4.4"},
            {"displayName":"% Inhibition @ 0.006 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.006,"value":"0.18"},
            {"displayName":"% Inhibition @ 0.003 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.003,"value":"-1.73"},
            {"displayName":"% Inhibition @ 0.0015 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.0015,"value":"0.76"},
            {"displayName":"% Inhibition @ 0.0008 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":8.0E-4,"value":"0.65"},
            {"displayName":"% Inhibition @ 0.0004 uM Rep 1","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":4.0E-4,"value":"1.18"},
            {"displayName":"% Inhibition @ 100 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":100.0},
            {"displayName":"% Inhibition @ 50 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":50.0},
            {"displayName":"% Inhibition @ 25 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":25.0},
            {"displayName":"% Inhibition @ 12.5 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":12.5},
            {"displayName":"% Inhibition @ 6.25 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":6.25,"value":"99.72"},
            {"displayName":"% Inhibition @ 3.125 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":3.125,"value":"99.66"},
            {"displayName":"% Inhibition @ 1.563 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":1.563,"value":"99.5"},
            {"displayName":"% Inhibition @ 0.781 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.781,"value":"96.43"},
            {"displayName":"% Inhibition @ 0.391 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.391,"value":"58.98"},
            {"displayName":"% Inhibition @ 0.195 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.195,"value":"-0.23"},
            {"displayName":"% Inhibition @ 0.098 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.098,"value":"3.23"},
            {"displayName":"% Inhibition @ 0.049 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.049,"value":"-4.36"},
            {"displayName":"% Inhibition @ 0.024 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.024,"value":"-1.79"},
            {"displayName":"% Inhibition @ 0.012 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.012,"value":"-4.05"},
            {"displayName":"% Inhibition @ 0.006 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.006,"value":"-0.34"},
            {"displayName":"% Inhibition @ 0.003 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.003,"value":"-6.34"},
            {"displayName":"% Inhibition @ 0.0015 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.0015,"value":"1.03"},
            {"displayName":"% Inhibition @ 0.0008 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":8.0E-4,"value":"-7.01"},
            {"displayName":"% Inhibition @ 0.0004 uM Rep 2","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":4.0E-4,"value":"1.12"},
            {"displayName":"% Inhibition @ 100 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":100.0},
            {"displayName":"% Inhibition @ 50 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":50.0},
            {"displayName":"% Inhibition @ 25 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":25.0},
            {"displayName":"% Inhibition @ 12.5 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":12.5},
            {"displayName":"% Inhibition @ 6.25 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":6.25,"value":"99.77"},
            {"displayName":"% Inhibition @ 3.125 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":3.125,"value":"99.76"},
            {"displayName":"% Inhibition @ 1.563 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":1.563,"value":"99.61"},
            {"displayName":"% Inhibition @ 0.781 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.781,"value":"96.65"},
            {"displayName":"% Inhibition @ 0.391 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.391,"value":"71.32"},
            {"displayName":"% Inhibition @ 0.195 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.195,"value":"-0.11"},
            {"displayName":"% Inhibition @ 0.098 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.098,"value":"0.42"},
            {"displayName":"% Inhibition @ 0.049 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.049,"value":"5.32"},
            {"displayName":"% Inhibition @ 0.024 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.024,"value":"7.2"},
            {"displayName":"% Inhibition @ 0.012 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.012,"value":"0.64"},
            {"displayName":"% Inhibition @ 0.006 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.006,"value":"4.56"},
            {"displayName":"% Inhibition @ 0.003 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.003,"value":"-4.02"},
            {"displayName":"% Inhibition @ 0.0015 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.0015,"value":"4.32"},
            {"displayName":"% Inhibition @ 0.0008 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":8.0E-4,"value":"1.23"},
            {"displayName":"% Inhibition @ 0.0004 uM Rep 3","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":4.0E-4,"value":"-0.41"},
            {"displayName":"% Inhibition @ 100 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":100.0},
            {"displayName":"% Inhibition @ 50 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":50.0},
            {"displayName":"% Inhibition @ 25 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":25.0},
            {"displayName":"% Inhibition @ 12.5 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":12.5},
            {"displayName":"% Inhibition @ 6.25 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":6.25},
            {"displayName":"% Inhibition @ 3.125 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":3.125},
            {"displayName":"% Inhibition @ 1.563 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":1.563},
            {"displayName":"% Inhibition @ 0.781 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.781},
            {"displayName":"% Inhibition @ 0.391 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.391},
            {"displayName":"% Inhibition @ 0.195 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.1954},
            {"displayName":"% Inhibition @ 0.098 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.098},
            {"displayName":"% Inhibition @ 0.049 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.049},
            {"displayName":"% Inhibition @ 0.024 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.024},
            {"displayName":"% Inhibition @ 0.012 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.012},
            {"displayName":"% Inhibition @ 0.006 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.006},
            {"displayName":"% Inhibition @ 0.003 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.003},
            {"displayName":"% Inhibition @ 0.0015 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":0.0015},
            {"displayName":"% Inhibition @ 0.0008 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":8.0E-4},
            {"displayName":"% Inhibition @ 0.0004 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":4.0E-4},
            {"displayName":"% Inhibition @ 0.0002 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":2.0E-4},
            {"displayName":"% Inhibition @ 0.0001 uM Rep 4","dictElemId":986,"responseUnit":"percent","testConcUnit":"um","testConc":1.0E-4},
            {"displayName":"Verification","value":"Verified"}
        ]
   }
'''

    void validateRootElementActivityData(final String responseClass, final ActivityData activityData, boolean hasQualifier) {
        assert activityData.dictElemId
        assert activityData.pubChemDisplayName

        assert activityData.qualifier == true == hasQualifier
        assert activityData.responseUnit
        assert activityData.value
        assert activityData.testConcentration
        assert activityData.testConcentrationUnit
    }

    void validateRootElements(List<RootElement> rootElements) {
        for (RootElement rootElement : rootElements) {
            assert rootElement.pubChemDisplayName
        }
    }

    void validatePriorityElements(List<PriorityElement> priorityElements, boolean hasChildElement) {
        for (PriorityElement priorityElement : priorityElements) {
            assert priorityElement
            assert !priorityElement.primaryElements
            assert priorityElement.pubChemDisplayName
            if (hasChildElement) {
                assert priorityElement.childElements
            }
        }
    }


    void "test all JSON #label"() {
        when:
        ResultData resultData = objectMapper.readValue(currentJSON, ResultData.class)
        then:

        assert resultData
        assert resultData.responseClass
        assert resultData.bardExptId
        assert resultData.sid
        assert resultData.cid
        final List<RootElement> rootElements = resultData.rootElements
        assert rootElements
        final List<PriorityElement> priorityElements = resultData.priorityElements
        assert resultData.isMapped() == isMapped
        assert resultData.getOutcome() == outcome
        assert resultData.hasConcentrationResponseSeries() == hasPlot
        assert resultData.hasPlot() == hasPlot
        assert resultData.responseClassEnum == respClassEnum
        validateRootElements(rootElements)
        if (label != 'UNCLASS') {
            assert resultData.hasPriorityElements() == true
            assert priorityElements
            validatePriorityElements(priorityElements, hasChildElements)
        } else {
            assert resultData.hasPriorityElements() == false
            assert !priorityElements
        }

        where:
        label                   | respClassEnum               | hasPlot | responseClass | currentJSON                                                                 | hasChildElements | isMapped | outcome
        "CR_NO_SER"             | ResponseClassEnum.CR_NO_SER | false          | "CR_NO_SER"   | CR_NO_SER_bid_573_aid_2802_no_P_C_mapping_JSON                              | false            | false    | active
        "CR_SER"                | ResponseClassEnum.CR_SER    | true           | "CR_SER"      | CR_SER_bid_1998_aid_998_good_structure_5_hill_params_JSON                   | false            | true     | active
        "CR_SER_WITH_QUALIFIER" | ResponseClassEnum.CR_SER    | true           | "CR_SER"      | CR_SER_bid_27_aid_2173_good_CR_with_qualifier_JSON                          | false            | true     | active
        "MULTCONC_SMALL"        | ResponseClassEnum.MULTCONC  | false          | "MULTCONC"    | MULTCONC_smaller_bid_2466_aid_504674_flat_resp_JSON                         | false            | false    | active
        "MULTCONC_BIG"          | ResponseClassEnum.MULTCONC  | false          | "MULTCONC"    | MULTCONC_bid_3992_aid_504898_flat_resp_JSON                                 | false            | false    | active
        "UNCLASS"               | ResponseClassEnum.UNCLASS   | false          | "UNCLASS"     | UNCLASS_bid_2819_aid_2373_no_annot_JSON                                     | true             | false    | active
        "SP"                    | ResponseClassEnum.SP        | false          | "SP"          | SP_bid_4567_aid_844255_single_point_with_no_element_nesting_available_JSON  | false            | true     | active
        "SP_NO_NESTING"         | ResponseClassEnum.SP        | false          | "SP"          | SP_bid_4020_aid_463170_simple_fold_change_average_with_element_nesting_JSON | true             | true     | inactive
    }
}

