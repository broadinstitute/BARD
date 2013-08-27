package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExpandedAssayUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

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
        "deposited": "deposited",
        "updated": "updated",
        "documents":
        [
            {
                "title": "Cytochrome c: can't live with it--can't live without it.",
                "doi": "null",
                "abs": "null",
                "pubmedId": 9393848,
                "resourcePath": "/documents/9393848"
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
        "kegg_disease_names":
       [
           "a",
            "b"
       ],
       "kegg_disease_cat":
       [
           "a"
       ],
       "minimumAnnotations": {
            "assay footprint": "1536-well plate",
            "detection method type": "luminescence method",
            "detection instrument name": "PerkinElmer ViewLux",
            "assay format": "cell-based format",
            "assay type": "signal transduction assay"
        },
       "resourcePath": "/assays/600"
    }
    '''

    void "test serialization to ExpandedAssay"() {
        when:
        final ExpandedAssay expandedAssay = objectMapper.readValue(ASSAY_EXPANDED_SEARCH_RESULTS, ExpandedAssay.class)
        then:
        assert expandedAssay.getId() == 600
        assert expandedAssay.getBardAssayId() == 600
        assert expandedAssay.getCapAssayId() == 4098
        assert expandedAssay.getSummary() == 0
        assert expandedAssay.getAssays() == 0
        assert expandedAssay.getSource() == "Burnham Center for Chemical Genomics"
        assert expandedAssay.getGrantNo() == "GR334"
        assert expandedAssay.getDeposited() == "deposited"
        assert expandedAssay.getUpdated() == "updated"
        assert expandedAssay.getKegg_disease_names() == ["a", "b"]
        assert expandedAssay.getKegg_disease_cat() == ["a"]
        assert !expandedAssay.getDocuments()?.isEmpty()
        assert expandedAssay.getBiology()
        assert expandedAssay.getMinimumAnnotation()
        assert expandedAssay.getMinimumAnnotation().getAssayFormat() == "cell-based format"
        assert expandedAssay.getAssayId() == 0
        assert expandedAssay.getResourcePath() =="/assays/600"
    }


}

