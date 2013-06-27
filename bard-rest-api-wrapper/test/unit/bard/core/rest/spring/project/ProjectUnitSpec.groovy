package bard.core.rest.spring.project

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import bard.core.rest.spring.compounds.Compound

@Unroll
class ProjectUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    public static final String PROJECT_FROM_FREE_TEXT = '''
    {
        "projectId": "172",
        "name": "Probe Development Summary of Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
        "highlight": null
    }
   '''

    public static final String PROJECT = '''
    {
       "bardProjectId":3,
       "capProjectId":72,
       "experimentTypes":{
          "85":"secondary assay",
          "84":"counter-screening assay",
          "86":"secondary assay"
       },
       "category":0,
       "type":0,
       "classification":0,
       "name":"High Throughput Screen for Novel Inhibitors of Platelet Integrin alphaIIb-beta3",
       "description":"The alphaIIbbeta3 receptor plays a vital role in hemostasis and thrombosis, where receptor...",
       "source":"NCGC",
       "score":1.0,
       "grantNo":null,
       "deposited":null,
       "updated":null,
       "probes":[

       ],
       "probeIds":[

       ],
       "eids":[
          84,
          85,
          86
       ],
       "aids":[
          71,
          72,
          73
       ],
       "publications":[
            8
       ],
       "targets":[
            {
                "biology": "PROTEIN",
                "entityId": 2,
                "name": "Platelet Integrin alphaIIb-beta3",
                "entity": "project",
                "extId": "D3Z2V4",
                "extRef": null,
                "dictLabel": "UniProt accession number",
                "dictId": 1398,
                "serial": 240,
                "updated": null,
                "resourcePath": "/biology/240"
            }
       ],
       "resourcePath":"/projects/3",
       "experimentCount":3
    }
    '''

    void "test serialization to Project - Free Text Search"() {
        when:
        final Project project = objectMapper.readValue(PROJECT_FROM_FREE_TEXT, Project.class)
        then:
        assert project.projId == project.bardProjectId
    }

    void "test serialization to Project"() {
        when:
        final Project project = objectMapper.readValue(PROJECT, Project.class)
        then:
        assert project.getId() == 3
        assert project.getBardProjectId() == 3
        assert project.getName() == "High Throughput Screen for Novel Inhibitors of Platelet Integrin alphaIIb-beta3"
        assert project.getType() == 0
        assert project.getCategory() == 0
        assert project.getSource() == "NCGC"
        assert project.getDescription() == "The alphaIIbbeta3 receptor plays a vital role in hemostasis and thrombosis, where receptor..."
        assert !project.getGrantNo()
        assert !project.getDeposited()
        assert !project.getUpdated()
        assert project.getKegg_disease_names()
        assert project.getKegg_disease_cat()
        assert !project.getBiology().isEmpty()
        assert project.getAids()
        assert project.getEids()
        assert project.experimentCount == 3
        assert project.probes
        assert project.probeIds
        assert project.getResourcePath() == "/projects/3"
        assert !project.getAk_dict_label()
        assert !project.getAv_dict_label()
        assert !project.getGobp_id()
        assert !project.getGobp_term()
        assert !project.getGocc_id()
        assert !project.getGomf_id()
        assert !project.getGomf_term()
        assert !project.getGocc_term()
        assert project.getClassification() == 0
        final List<Long> publications = project.getPublications()
        assert publications
        final Long document = publications.get(0)
        assert document
        assert project.hasProbes()
    }

    void "test has Probes #label"() {
        given:
        ProjectAbstract projectAbstract = createdProjectAbstract
        when:
        boolean hasProbes = projectAbstract.hasProbes()
        then:
        assert hasProbes == expected
        where:
        label                   | expected | createdProjectAbstract
        "Has list of Probes"    | true     | new ProjectAbstract(probes: [new Compound()])
        "Has list of Probe Ids" | true     | new ProjectAbstract(probeIds: [2, 3])
        "Has no Probes"         | false    | new ProjectAbstract()

    }


}

