package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayResultUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final ASSAY_JSON='''
  {
    "collection": [
        {
            "bardAssayId": 2977,
            "capAssayId": 8181,
            "category": 0,
            "summary": 0,
            "assays": 0,
            "classification": 0,
            "name": "High Content Imaging Assay to Measure T. cruzi CAI/72 Strain in J774",
            "source": null,
            "grantNo": null,
            "title": "Fluorescence Cell-Based/Microorganism Screen to Determine Compound Effect on T. cruzi in NIH3T3 Cells",
            "designedBy": "lmar",
            "deposited": null,
            "updated": "2013-10-11",
            "assayType": "Regular",
            "assayStatus": "Draft",
            "documents": [],
            "targets": [
                {
                    "biology": "PROCESS",
                    "entityId": 2977,
                    "name": "parasitism",
                    "entity": "assay",
                    "extId": "GO:0072519",
                    "extRef": null,
                    "dictLabel": "GO biological process term",
                    "dictId": 1419,
                    "serial": 10268,
                    "updated": null,
                    "resourcePath": "/biology/10268"
                }
            ],
            "experiments": [
                {
                    "bardExptId": 4278,
                    "capExptId": 8082,
                    "bardAssayId": 2977,
                    "capAssayId": 8181,
                    "pubchemAid": 652278,
                    "category": -1,
                    "type": -1,
                    "summary": 0,
                    "assays": 0,
                    "classification": -1,
                    "substances": 11,
                    "compounds": 3,
                    "activeCompounds": 3,
                    "confidenceLevel": 1,
                    "name": "Cidal vs. static assay: inhibition of T. cruzi (CAI/72) in J774 macrophages after prolonged compound treatment and treatment withdrawal Measured in Cell-Based System Using Imaging - 2138-13_Inhibitor_SinglePoint_DryPowder_Activity_Set2",
                    "description": null,
                    "source": null,
                    "grantNo": null,
                    "status": "Pending",
                    "deposited": null,
                    "updated": "2013-10-11",
                    "hasProbe": false,
                    "projectIdList": [
                        121
                    ],
                    "resourcePath": "/experiments/4278"
                },
                {
                    "bardExptId": 4303,
                    "capExptId": 8083,
                    "bardAssayId": 2977,
                    "capAssayId": 8181,
                    "pubchemAid": 652279,
                    "category": -1,
                    "type": -1,
                    "summary": 0,
                    "assays": 0,
                    "classification": -1,
                    "substances": 11,
                    "compounds": 3,
                    "activeCompounds": 3,
                    "confidenceLevel": 1,
                    "name": "Cidal vs. static assay: inhibition of T. cruzi (CAI/72) in J774 macrophages after prolonged compound treatment and treatment withdrawal Measured in Cell-Based System Using Imaging - 2138-13_Inhibitor_SinglePoint_DryPowder_Activity",
                    "description": null,
                    "source": null,
                    "grantNo": null,
                    "status": "Pending",
                    "deposited": null,
                    "updated": "2013-10-11",
                    "hasProbe": false,
                    "projectIdList": [
                        121
                    ],
                    "resourcePath": "/experiments/4303"
                }
            ],
            "projects": [
                {
                    "bardProjectId": 121,
                    "capProjectId": 1143,
                    "experimentTypes": {
                        "852": "primary screen",
                        "1377": "confirmatory screen",
                        "1378": "alternative organism screen",
                        "1576": "confirmatory screen",
                        "1594": "alternative organism screen",
                        "1595": "compound toxicity screen",
                        "1813": "alternative organism screen",
                        "1969": "compound toxicity screen",
                        "1970": "compound toxicity screen",
                        "2175": "confirmatory screen",
                        "2718": "compound toxicity screen",
                        "3153": "lead-optimization screen",
                        "4278": "alternative confirmatory screen",
                        "4279": "alternative confirmatory screen",
                        "4280": "alternative confirmatory screen",
                        "4284": "alternative confirmatory screen",
                        "4291": "compound toxicity screen",
                        "4293": "alternative confirmatory screen",
                        "4294": "alternative confirmatory screen",
                        "4296": "alternative confirmatory screen",
                        "4300": "primary screen",
                        "4301": "compound toxicity screen",
                        "4303": "alternative confirmatory screen",
                        "4304": "alternative confirmatory screen",
                        "4305": "alternative confirmatory screen",
                        "4306": "compound toxicity screen",
                        "4307": "alternative confirmatory screen",
                        "4311": "alternative confirmatory screen",
                        "4312": "confirmatory screen"
                    },
                    "category": 0,
                    "type": 0,
                    "classification": 0,
                    "name": "Identification of Trypanosoma cruzi inhibitors",
                    "description": null,
                    "source": null,
                    "score": 3,
                    "grantNo": null,
                    "deposited": null,
                    "updated": null,
                    "probes": [
                        {
                            "cid": 44493436,
                            "probeId": "ML341",
                            "url": "https://www.ncbi.nlm.nih.gov/books/?term=ML341",
                            "smiles": "C[C@@H](CO)N1C[C@H](C)[C@@H](CN(C)CC2=CC=C(OC3=CC=CC=C3)C=C2)OC4=C(C=CC=C4NC(=O)C5=CC=NC=C5)C1=O",
                            "name": "BRD-K82092559-001-01-6",
                            "iupacName": "N-[(2S,3S)-5-[(2S)-1-hydroxypropan-2-yl]-3-methyl-2-[[methyl-[(4-phenoxyphenyl)methyl]amino]methyl]-6-oxo-3,4-dihydro-2H-1,5-benzoxazocin-10-yl]pyridine-4-carboxamide",
                            "mwt": 594.7,
                            "tpsa": 104,
                            "exactMass": 594.284,
                            "xlogp": 4.6,
                            "complexity": 902,
                            "rotatable": 10,
                            "hbondAcceptor": 7,
                            "hbondDonor": 2,
                            "compoundClass": "ML Probe",
                            "numAssay": 8,
                            "numActiveAssay": 5,
                            "probeAnnotations": [
                                {
                                    "entityId": null,
                                    "entity": "project",
                                    "source": "cap-context",
                                    "id": 1677,
                                    "display": "ML341",
                                    "contextRef": "probe",
                                    "contextGroup": null,
                                    "key": "1776",
                                    "value": null,
                                    "extValueId": null,
                                    "url": "https://www.ncbi.nlm.nih.gov/books/?term=ML341",
                                    "displayOrder": 1,
                                    "related": null
                                },
                                {
                                    "entityId": null,
                                    "entity": "project",
                                    "source": "cap-context",
                                    "id": 1677,
                                    "display": "44493436",
                                    "contextRef": "probe",
                                    "contextGroup": null,
                                    "key": "878",
                                    "value": null,
                                    "extValueId": null,
                                    "url": "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid=44493436",
                                    "displayOrder": 0,
                                    "related": null
                                }
                            ],
                            "highlight": null,
                            "resourcePath": "/compounds/44493436"
                        }
                    ],
                    "probeIds": [
                        44493436
                    ],
                    "eids": [
                        852,
                        1377,
                        1378,
                        1576,
                        1594,
                        1595,
                        1813,
                        1969,
                        1970,
                        2175,
                        2718,
                        3153,
                        4278,
                        4279,
                        4280,
                        4284,
                        4291,
                        4293,
                        4294,
                        4296,
                        4300,
                        4301,
                        4303,
                        4304,
                        4305,
                        4306,
                        4307,
                        4311,
                        4312
                    ],
                    "aids": [
                        598,
                        1048,
                        1049,
                        1214,
                        1394,
                        459,
                        2977,
                        2974,
                        2973
                    ],
                    "publications": [],
                    "targets": [
                        {
                            "biology": "PROCESS",
                            "entityId": 121,
                            "name": "parasitism",
                            "entity": "project",
                            "extId": "GO:0072519",
                            "extRef": null,
                            "dictLabel": "GO biological process term",
                            "dictId": 1419,
                            "serial": 9895,
                            "updated": null,
                            "resourcePath": "/biology/9895"
                        }
                    ],
                    "resourcePath": "/projects/121",
                    "experimentCount": 29
                }
            ],
            "minimumAnnotations": {
                "assay footprint": "48-well plate",
                "detection method type": "brightfield microscopy",
                "assay format": "cell-based format",
                "assay type": "infection assay"
            },
            "score": 1,
            "resourcePath": "/assays/2977",
            "description": null,
            "protocol": null,
            "comments": null
        }
    ],
    "link": "/assays?skip=2953&top=1&expand=true"
}
'''
    public static final String ASSAY_FREE_TEXT = '''
{
    "docs": [
        {
            "aid": 624247,
            "bardAssayId": 770,
            "capAssayId": 81,
            "category": 2,
            "type": 0,
            "summary": 0,
            "assays": 0,
            "classification": 0,
            "name": "qHTS screen for small molecules that inhibit ELG1-dependent DNA repair: Hit Confirmation in Cell-Based Luciferin Assay",
            "source": "NCGC",
            "grantNo": null,
            "deposited": null,
            "updated": null,
            "documents": [],
            "targets": [
                "Q96QE3"
            ],
            "experiments": [
                770
            ],
            "projects": [
                328
            ],
            "kegg_disease_names": [],
            "kegg_disease_cat": [],
            "resourcePath": "/assays/770"
        }
    ],
    "metaData": {
        "nhit": 111,
        "facets": [
            {
                "facetName": "assay_component_role",
                "counts": {
                    "dye": 1,
                    "substrate": 2
                }
            },
            {
                "facetName": "detection_method_type",
                "counts": {
                    "absorbance": 11,
                    "autoradiography": 2
                }
            },
            {
                "facetName": "target_name",
                "counts": {
                    "Cyclin-dependent kinase inhibitor 1B": 1,
                    "ATPase family AAA domain-containing protein 5": 9
                }
            },
            {
                "facetName": "kegg_disease_cat",
                "counts": {
                    "Infectious disease": 2,
                    "Cancer": 3
                }
            }
        ],
        "queryTime": 165,
        "elapsedTime": 179,
        "matchingFields": {
            "770": {
                "name": "qHTS screen for small molecules that inhibit ELG1-dependent DNA repair: Hit Confirmation in Cell-Based Luciferin Assay"
            },
            "779": {
                "name": "qHTS screen for small molecules that inhibit ELG1-dependent DNA repair: Hit Confirmation with 5FU Viability"
            }
        },
        "scores": {
            "770": 3.0651672,
            "779": 3.0651672
        }
    },
    "etag": "7b2486794ca8aa61",
    "link": "/search/assays?q=\\"dna repair\\"&skip=2&top=2&expand=true"
        }
       '''

    void "test serialization to AssayResult"() {
        when:
        final AssayResult assayResult = objectMapper.readValue(ASSAY_FREE_TEXT, AssayResult.class)
        then:
        assert assayResult.getMatchingField("770").name == "name"
        assert assayResult.getScore("770") == 3.0651672
        assert assayResult.getAssayDocs() == assayResult.getAssays()
        for (Assay assay : assayResult.getAssayDocs()) {
            assert assay.id
        }
    }
    void "test serialization to AssayResult from a browse search"() {
        when:
        final ExpandedAssayResult assayResult = objectMapper.readValue(ASSAY_JSON, ExpandedAssayResult.class)
        then:
        assert assayResult.assays.size() == 1
    }

}

