package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayResultUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

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


}

