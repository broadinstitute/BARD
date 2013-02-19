package bard.core.rest.spring.project

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectResultUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    /*
     * http://bard.nih.gov/api/v10/experiments/1048/projects?expand=true
     */
    @Shared String PROJECTS_FROM_EXPERIMENT = '''
    {
      "collection": [
        {
            "bardProjectId": 234,
            "category": 0,
            "type": 0,
            "classification": 0,
            "name": "Summary of the probe development effort to identify inhibitors of the Plasmodium falciparum M17- Family Leucine Aminopeptidase (M17AAP)",
            "description": "NCGC",
            "gobp_id": null,
            "gobp_term": null,
            "gomf_term": null,
            "gomf_id": null,
            "gocc_id": null,
            "gocc_term": null,
            "av_dict_label": [
                "MH082342-01A1",
                "The Scripps Research Institute Molecular Screening Center"
            ],
            "ak_dict_label": [
                "grant number",
                "laboratory name"
            ],
            "kegg_disease_names": [],
            "kegg_disease_cat": [],
            "grantNo": null,
            "deposited": null,
            "updated": null,
            "probes": [],
            "probeIds": [],
            "eids": [
                1048,
                2226
            ],
            "aids": [
                1048,
                 2226
            ],
            "publications": null,
            "targets": [],
            "resourcePath": "/projects/234",
            "experimentCount": 8
        }
    ],
    "link": null
   }
    '''
    /**
     *
     */
    @Shared String PROJECTS_FROM_FREE_TEXT = '''
    {
        "docs": [
            {
                "proj_id": "172",
                "name": "Probe Development Summary of Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
                "highlight": null
            },
            {
                "proj_id": "245",
                "name": "Summary of the probe development effort to identify inhibitors of AddAB recombination protein complex",
                "highlight": null
            }
        ],
        "metaData": {
            "nhit": 2,
            "facets": [
                {
                    "facetName": "num_expt",
                    "counts": {
                        "[* TO 1]": 1,
                        "[1 TO 5]": 8,
                        "[5 TO 10]": 9,
                        "[10 TO *]": 10
                    }
                },
                {
                    "facetName": "target_name",
                    "counts": {
                        "ATP-dependent DNA helicase Q1": 1,
                        "Wee1-like protein kinase": 1
                    }
                },
                {
                    "facetName": "kegg_disease_cat",
                    "counts": {
                        "Cancer": 5,
                        "Neurodegenerative disease": 3
                    }
                }
        ],
        "queryTime": 2,
        "elapsedTime": 8,
        "matchingFields": null,
        "scores": null
    },
    "etag": "66bc66cba72c7c7a",
    "link": "link"
    }
    '''

    void "test free text search for Projects"() {
        when:
        final ProjectResult projectResult = objectMapper.readValue(PROJECTS_FROM_FREE_TEXT, ProjectResult.class)
        then:
        assert projectResult
        final List<Project> projects = projectResult.projects
        assert projects.size()==2
        assert projectResult.numberOfHits == 2
        assert projectResult.metaData
        assert projectResult.projs.size() == projects.size()
        for(Project project: projects){
            assert project.getId()
        }

    }
    void "test Project From Experiments"() {
        when:
        final ProjectResult projectResult = objectMapper.readValue(PROJECTS_FROM_EXPERIMENT, ProjectResult.class)
        then:
        assert projectResult
        final List<Project> projects = projectResult.projects
        assert projects.size()==1

        for(Project project: projects){
            assert project.getId()
        }

    }

}

