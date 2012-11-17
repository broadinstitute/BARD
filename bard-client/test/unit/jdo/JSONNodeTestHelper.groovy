package jdo

/**
 *
 */
class JSONNodeTestHelper {
    public static final COMPOUND_SYNONYMS='''
    [
       "NSC228155",
       "MLS000756562",
       "NSC-228155",
       "AC1L7NUE",
       "CHEMBL505670",
       "HMS2796J10",
       "ZINC01757986",
       "SMR000448993",
       "7-nitro-4-(1-oxidopyridin-1-ium-2-yl)sulfanyl-2,1,3-benzoxadiazole"
    ]
'''
    public static final TESTED_ASSAY_NODE = '''
   {
  "collection": [
    {
      "aid": 1714,
      "bardAssayId": 411,
      "capAssayId": 4063,
      "category": 1,
      "type": 0,
      "summary": 0,
      "assays": 0,
      "classification": 0,
      "name": "Identification of Novel Modulators of Cl- dependent Transport Process via HTS: Secondary Assay 3 with KCC2 cells",
      "description": "description"
      }
      ]
      }
'''
    public static final TARGETS_NODE = '''
              { "targets":
                    [
                            {
                                "acc": "P98170",
                                "name": "E3 ubiquitin-protein ligase XIAP",
                                "description": null,
                                "status": "Reviewed",
                                "geneId": 331,
                                "taxId": 9606,
                                "resourcePath": "/targets/accession/P98170"
                            }
                    ]
                    }

'''
    public static final String SUGGEST_PAIR = '''
{
"protocol":
   [
       "dna",
       "dnase",
       "dnak",
       "dnaj",
       "dnab"
   ]
}
'''
    public static final String COMPOUND_SEARCH_RESULTS = '''
{
   "docs":[
      {
         "cid":"2722",
         "iso_smiles":"C1=CC2=C(C(=C(C=C2Cl)Cl)O)N=C1",
         "iupac_name":"5,7-dichloroquinolin-8-ol",
         "preferred_term":"CHLOROXINE",
         "highlight":"Although the mechanism of action is not understood, chloroxine may slow down mitotic activity in the epidermis, thereby reducing excessive scaling associated with dandruff or seborrheic dermatitis of the scalp. Chloroxine induces SOS-DNA repair in E. coli, so chloroxine may be genotoxic to bacteria. "
      },
      {
         "cid":"16760208",
         "iso_smiles":"C1=NC2=C(N1[C@H]3C([C@H]([C@H](O3)CO)O)F)N=C(N=C2N)Cl",
         "iupac_name":"(2R,3S,5R)-5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol",
         "preferred_term":"CAFdA",
         "highlight":" inhibition. The affinity of clofarabine triphosphate for these enzymes is similar to or greater than that of deoxyadenosine triphosphate. In preclinical models, clofarabine has demonstrated the ability to inhibit DNA repair by incorporation into the DNA chain during the repair process. Clofarabine 5"
      }
   ],
   "metaData":{
      "nhit":6,
      "facets":[
         {
            "facetName":"COLLECTION",
            "counts":{
               "":23,
               "HTS amenable drugs":6,
               "Japan":3
            }
         },
         {
            "facetName":"xlogp",
            "counts":{
               "[* TO 1]":2,
               "[5 TO 10]":2,
               "[10 TO *]":2
            }
         }
      ]
   },
   "etag":"a628b5728fdb0819",
   "link":null
}
'''
    public static final String ASSAY_SEARCH_RESULTS = '''
{
   "docs":[
      {
         "assay_id":"2377",
         "name":"NCI Yeast Anticancer Drug Screen. Data for the mgt1 strain",
         "highlight":"gi|6320001|ref|NP_010081.1|DNA repair methyltransferase (6-O-methylguanine-DNA methylase) involved in protection against DNA alkylation damage; Mgt1p [Saccharomyces cerevisiae]"
      },
      {
         "assay_id":"2380",
         "name":"NCI Yeast Anticancer Drug Screen. Data for the sgs1 mgt1 strain",
         "highlight":"gi|6320001|ref|NP_010081.1|DNA repair methyltransferase (6-O-methylguanine-DNA methylase) involved in protection against DNA alkylation damage; Mgt1p [Saccharomyces cerevisiae]"
      }
   ],
   "metaData":{
      "nhit":216,
      "facets":[
         {
            "facetName":"assay_component_role",
            "counts":{
               "dye":3,
                "substrate":2
            }
         },
         {
            "facetName":"kegg_disease_cat",
            "counts":{
               "Infectious disease":2,
               "Cancer":57
            }
         }
      ]
   },
   "etag":"f418a668f18e7394",
   "link":"/search/assays?q=dna repair&skip=10&top=10&expand=false"
}
'''
    public static final String PROJECT_SEARCH_RESULTS = '''
  {
   "docs":[
      {
         "proj_id":"179",
         "name":"Probe Development Summary for Inhibitors of Bloom's syndrome helicase (BLM)",
         "highlight":" develop resistance to therapy through enhanced activity of DNA repair functions; this has led to an increased interest in developing drugs that interfere with DNA repair, which could sensitize cancer cells to conventional therapy. This summary assay pertains to the Bloom syndrome helicase (BLM), which"
      },
      {
         "proj_id":"172",
         "name":"Probe Development Summary of Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
         "highlight":" and functions centrally in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting"
      }
   ],
   "metaData":{
      "nhit":15,
      "facets":[
         {
            "facetName":"num_expt",
            "counts":{
               "[* TO 1]":0,
               "[10 TO *]":7
            }
         }
      ]
   },
   "etag":"cb5e7d5bb4da97a6",
   "link":"/search/projects?q=dna repair&skip=10&top=10&expand=false"
}
'''

    public static final String PROJECT_SUMMARY_SEARCH_RESULTS = '''
{
         "proj_id":"179",
         "name":"Probe Development Summary for Inhibitors of Bloom's syndrome helicase (BLM)",
         "highlight":" develop resistance to therapy through enhanced activity of DNA repair functions; this has led to an increased interest in developing drugs that interfere with DNA repair, which could sensitize cancer cells to conventional therapy. This summary assay pertains to the Bloom syndrome helicase (BLM), which"

}
'''
    public static final String ASSAY_SUMMARY_SEARCH_RESULTS = '''
      {
         "assay_id":"2377",
         "name":"NCI Yeast Anticancer Drug Screen. Data for the mgt1 strain",
         "highlight":"gi|6320001|ref|NP_010081.1|DNA repair methyltransferase (6-O-methylguanine-DNA methylase) involved in protection against DNA alkylation damage; Mgt1p [Saccharomyces cerevisiae]"
      }
'''
    public static final String COMPOUND_SUMMARY_SEARCH_RESULTS = '''
{
               "cid": "2722",
               "iso_smiles": "C1=CC2=C(C(=C(C=C2Cl)Cl)O)N=C1",
               "iupac_name": "5,7-dichloroquinolin-8-ol",
               "preferred_term": "CHLOROXINE",
               "highlight": "Although the mechanism of action is not understood, chloroxine may slow down mitotic activity in the epidermis, thereby reducing excessive scaling associated with dandruff or seborrheic dermatitis of the scalp. Chloroxine induces SOS-DNA repair in E. coli, so chloroxine may be genotoxic to bacteria. "
           }
'''
    public static final String EXPERIMENT_EXPANDED_SEARCH_RESULTS = '''
{
   "exptId": 346,
   "assayId": 346,
   "pubchemAid": 2209,
   "category": 2,
   "type": 2,
   "summary": 0,
   "assays": 0,
   "classification": 2,
   "substances": 3,
   "compounds": 3,
   "name": "Fluorescence-based dose response cell-based high-throughput screening assay for potentiators or agonists of NPY-Y1.",
   "description": "Source (MLPCN Center Name): The Scripps Research Institute Molecular",
    "source": null,
   "grantNo": "GR002",
   "deposited": null,
   "updated": null,
   "hasProbe": false,
   "projectIdList":
   [
       67
   ],
   "resourcePath": "/experiments/346"
  }
   '''
    public static final String PROJECT_EXPANDED_SEARCH_RESULTS = '''
    {
        "projectId": 17,
        "category": 0,
        "type": 0,
        "classification": 0,
        "name": "Quantitative High-Throughput Screen for Inhibitors of Tau Fibril Formation: Summary",
        "description": "NIH Molecular Libraries Probe",
            "source": "NCGC",
            "gobp_id": null,
            "gobp_term": null,
            "gomf_term": null,
            "gomf_id": null,
            "gocc_id": null,
            "gocc_term": null,
            "av_dict_label":
                    [
                            "X01 MH083262-01",
                             "microtubule-associated protein tau [Homo sapiens]"
                    ],
            "ak_dict_label":
                    [
                            "grant number",
                            "gene"
                    ],
            "kegg_disease_names":
                    [
                            "Amyotrophic lateral sclerosis (ALS)",
                            "Frontotemporal dementia, chromosome 3-linked (FTD3)"
                    ],
            "kegg_disease_cat":
                    [
                            "Neurodegenerative disease",
                            "Neurodegenerative disease"
                    ],
            "grantNo": "BR222",
            "deposited": null,
            "updated": null,
            "probes":
                    [
                            {
                                "cid": 9795907,
                                "sids":
                                [
                                        14751169,
                                        142290905
                                ],
                                "probeId": "ML103",
                                "url": "https://mli.nih.gov/mli/?dl_id=976",
                                "smiles": "NC1=C2C(=CS1)C(=NN(C2=O)C3=CC=CC=C3)C(O)=O",
                                "name": "ML103",
                                "iupacName": "5-amino-4-oxo-3-phenylthieno[3,4-d]pyridazine-1-carboxylic acid",
                                "mwt": 287.294,
                                "tpsa": 124,
                                "exactMass": 287.036,
                                "xlogp": 2.3,
                                "complexity": 462,
                                "rotatable": 2,
                                "hbondAcceptor": 6,
                                "hbondDonor": 2,
                                "anno_val":
                                [
                                ],
                                "anno_key":
                                [
                                ],
                                "highlight": null,
                                "resourcePath": "/compounds/9795907"
                            }
                    ],
            "probeIds":
                    [
                            9795907
                    ],
            "eids":
                    [
                            1472,
                            1260
                    ],
            "aids":
                    [
                            1472,
                            1260
                    ],
            "publications": null,
            "targets":
                    [
                            {
                                "acc": "P10636",
                                "name": "Microtubule-associated protein tau",
                                "description": null,
                                "status": "Reviewed",
                                "geneId": 4137,
                                "taxId": 9606,
                                "resourcePath": "/targets/accession/P10636"
                            }
                    ],
            "resourcePath": "/projects/17",
            "experimentCount": 12
    }
'''
    public static final String TARGET_NODE = '''
 {
 "acc": "P10636",
  "name": null,
  "description": "Description",
  "status": "Reviewed",
  "geneId": null,
  "taxId": null
  }
'''
    public static final String COMPOUND_EXPANDED_SEARCH_RESULTS = '''
    {
       "cid": 600,
       "sids":
       [
           6820697,
           24438962,
           57320327,
           79750249,
           103067409,
           103080689,
           104295294,
           117595058,
           125659758
       ],
       "probeId": "ML108",
       "url": null,
       "smiles": "OC1C(O)C(COP(O)(O)=O)OC(=O)C1O",
       "name": "(3,4,5-trihydroxy-6-oxotetrahydro-2h-pyran-2-yl)methyldihydrogen-phosphat",
       "iupacName": "(3,4,5-trihydroxy-6-oxooxan-2-yl)methyl dihydrogen phosphate",
       "mwt": 258.12,
       "tpsa": 154,
       "exactMass": 258.014,
       "xlogp": -3.7,
       "complexity": 311,
       "rotatable": 3,
       "hbondAcceptor": 9,
       "hbondDonor": 5,
       "anno_val":
       [
       ],
       "anno_key":
       [
       ],
       "highlight": null,
       "resourcePath": "/compounds/600"
    }
'''
    public static final String ASSAY_EXPANDED_SEARCH_RESULTS = '''
    {
        "aid": 1749,
        "bardAssayId": 600,
        "capAssayId": 4098,
        "category": 1,
        "type": 0,
        "summary": 0,
        "assays": 0,
        "classification": 0,
        "name": "SAR analysis of Antagonists of IAP-family anti-apoptotic proteins",
        "description": "Data Source: Sanford-Burnham Center for Chemical Genomics (SBCCG) Source Affiliation",
        "source": "Burnham Center for Chemical Genomics",
        "grantNo": "GR334",
        "protocol": "BIR1/2 assay materials: 1) Bir1/2 protein was provided by Prof. John C. Reed",
        "comments": "Compounds with an IC50 < 100 uM are considered 'active.' To simplify the distinction ",
            "deposited": null,
            "updated": null,
            "publications":
                    [
                            {
                                "title": "Cytochrome c: can't live with it--can't live without it.",
                                "doi": "null",
                                "abs": "null",
                                "pubmedId": 9393848,
                                "resourcePath": "/documents/9393848"
                            },
                            {
                                "title": "Mitochondria and apoptosis.",
                                "doi": "null",
                                "abs": "A variety of key events in apoptosis focus on mitochondria, including the release",
                                "pubmedId": 9721092,
                                "resourcePath": "/documents/9721092"
                            },
                            {
                                "title": "IAPs block apoptotic events induced by caspase-8 and cytochrome c by direct inhibition of distinct caspases.",
                                "doi": "10.1093/emboj/17.8.2215",
                                "abs": "Inhibitor of apoptosis (IAP) gene products play an evolutionarily conserved role in regulating programmed cell",
                                "pubmedId": 9545235,
                                "resourcePath": "/documents/9545235"
                            },
                            {
                                "title": "Structural basis for the inhibition of caspase-3 by XIAP.",
                                "doi": "null",
                                "abs": "The molecular mechanism(s) that regulate apoptosis by caspase inhibition remain poorly understood",
                                "pubmedId": 11257232,
                                "resourcePath": "/documents/11257232"
                            },
                            {
                                "title": "Small-molecule antagonists of apoptosis suppressor XIAP exhibit broad antitumor activity.",
                                "doi": "null",
                                "abs": "Apoptosis resistance commonly occurs in cancers, preventing activation of Caspase family cell death proteases.",
                                "pubmedId": 14749124,
                                "resourcePath": "/documents/14749124"
                            }
                    ],
            "targets":
                    [
                            {
                                "acc": "P98170",
                                "name": "E3 ubiquitin-protein ligase XIAP",
                                "description": null,
                                "status": "Reviewed",
                                "geneId": 331,
                                "taxId": 9606,
                                "resourcePath": "/targets/accession/P98170"
                            }
                    ],
            "experiments":
                    [
                            600
                    ],
            "projects":
                    [
                            31
                    ],
            "gobp_id":
                    [
                            "GO:0050801",
                            "GO:0050789"
                    ],
            "gobp_term":
                    [
                            "ion homeostasis",
                             "regulation of biological process"
                    ],
            "gomf_term":
                    [
                            "metal ion binding",
                             "molecular_function"
                    ],
            "gomf_id":
                    [
                            "GO:0046872",
                             "GO:0003674"
                    ],
            "gocc_id":
                    [
                            "GO:0044444",
                            "GO:0005623"
                    ],
            "gocc_term":
                    [
                            "cytoplasmic part",
                            "cell"
                    ],
            "av_dict_label":
                    [
                            "gi|8744934|emb|CAB95312.1|X-linked inhibitor of apoptosis [Homo sapiens]",
                            "baculoviral IAP repeat-containing protein 4 [Homo sapiens]"
                    ],
            "ak_dict_label":
                    [
                            "protein",
                            "comment"
                    ],
            "kegg_disease_names":
                    [
                            "Other well-defined immunodeficiency syndromes, including the following eight diseases:Wiskott-Aldrich syndrome",
                             "Autoimmune polyendocrinopathy-candidiasis-ectodermal dystrophy (APECED)"
                    ],
            "kegg_disease_cat":
                    [
                            "Primary immunodeficiency",
                             "Primary immunodeficiency2"
                    ],
            "resourcePath": "/assays/600"
    }
    '''
    public static final String ETAG_FACETS = '''
[
   {
      "facetName":"COLLECTION",
      "counts":{
         "US DEA":338,
         "NPC informatics":10201
      }
   },
   {
      "facetName":"xlogp",
      "counts":{
         "":4641,
         "-1":151
      }
   }
]
'''

    public static final String JSON_NODE_FULL_ETAG = '''
          {
               "accessed": 1348295120000,
               "etag_id": "de6add5339d1af20",
               "count": 7109,
               "status": 1,
               "created": 1348240757000,
               "description": "The NCGC Pharmaceutical Collection",
               "name": "NPC database",
               "type": "gov.nih.ncgc.bard.entity.Compound",
               "url": "http://tripod.nih.gov/npc",
               "modified": 1348240757000
           }
    '''
    public static final String JSON_NODE_ONLY_ETAG_ID = '''
          {
               "etag_id": "de6add5339d1af20"
           }
    '''
    public static final String JSON_NODE_NO_ETAG_ID = '''
          {
               "some_id": "de6add5339d1af20"
           }
    '''
    public static final String JSON_NODE_NO_FACETS = '''
    {
        "metaData":{
        "nhit":1
    }
    }
   '''
    public static final String JSON_NODE_WITH_FACETS = '''
    {
        "metaData":{
        "nhit":1,
        "facets":[
                {
                    "facetName":"assay_component_role",
                    "counts":{
                    "dye":3,
                    "measured component":6
                }
                },
                {
                    "facetName":"assay_mode",
                    "counts":{

                }
                }
        ]
    }
    }
     '''
    public static final String FACETS_ONLY = '''
    {
            "facets":[
                {
                    "facetName":"assay_component_role",
                    "counts":{
                    "dye":3,
                    "measured component":6
                }
                }
        ]
    }
    '''
    public static final String JSON_NODE_NO_HITS = '''
    {
        "metaData":{
        "facets":[
                {
                    "facetName":"assay_component_role",
                    "counts":{
                    "dye":3,
                    "measured component":6
                }
                },
                {
                    "facetName":"assay_mode",
                    "counts":{

                }
                }
        ]
    }
    }
     '''
    public static final String JSON_NODE_NO_META_DATA = '''
    {
        "nhit":216,
        "facets":[
                {
                    "facetName":"assay_component_role",
                    "counts":{
                    "dye":3,
                    "measured component":6
                }
                },
                {
                    "facetName":"assay_mode",
                    "counts":{

                }
                }
        ]

    }
     '''
    public static final String JSON_FACET = '''
{
   "facetName":"assay_component_role",
   "counts":{
      "dye":3,
      "measured component":0
   }
}
'''
    public static final String JSON_FACET_NO_FACET_NAME = '''
{
   "facetName":"",
   "counts":{
      "dye":3,
      "measured component":6
   }
}
'''

    public static final String JSON_COUNTS = '''
{
      "dye":3,
      "measured component":6
}
'''
    public static final String JSON_FACET_ZERO_COUNT_VALUE = '''
{
   "facetName":"assay_component_role",
    "counts":{
      "dye":0
   }
}
'''

    public static final String JSON_FACET_NO_COUNT_KEY = '''
{
   "facetName":"assay_component_role"
}
'''
}
