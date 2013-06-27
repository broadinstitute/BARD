package bard.core.adapter

import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectAdapterUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String PROJECT = '''
    {
       "bardProjectId": 17,
       "category": 0,
       "type": 0,
       "classification": 0,
       "capProjectId":20,
       "name": "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)",
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
        ],
       "ak_dict_label":
       [
        ],
       "kegg_disease_names":
       [
           "Lou Gehrig's disease"
       ],
       "kegg_disease_cat":
       [
           "Neurodegenerative disease"
       ],
       "probes":
       [
           {
               "cid": 9795907,
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
               "compoundClass": "ML Probe",
               "numAssay": 267,
               "numActiveAssay": 6,
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
           1472
       ],
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

    String EXPANDED_PROJECT='''
{
    "bardProjectId": 31,
    "category": 0,
    "type": 0,
    "classification": 0,
    "name": "Summary of Chemical Antagonists IAP-family anti-apoptotic proteins",
    "description": "Data Source: Sanford-Burnham Center for Chemical Genomics",
    "source": "Burnham Center for Chemical Genomics",
     "publications": [
        {
            "title": "Cytochrome c: can't live with it--can't live without it.",
            "doi": "null",
            "abs": "null",
            "pubmedId": 9393848,
            "resourcePath": "/documents/9393848"
        }
    ],
    "targets": [
        {
            "acc": "P98170",
            "name": "E3 ubiquitin-protein ligase XIAP",
            "description": null,
            "status": "Reviewed",
            "url": "http://www.uniprot.org/uniprot/P98170",
            "geneId": 331,
            "taxId": 9606,
            "resourcePath": "/targets/accession/P98170"
        }
    ],
    "resourcePath": "/projects/31",
    "experimentCount": 11
}
    '''
    void "test highlight"() {
        when:
        ProjectAdapter projectAdapter = new ProjectAdapter(null)
        then:
        assert !projectAdapter.matchingField
        assert !projectAdapter.highlight
        assert !projectAdapter.hasProbes()


    }


    void "test getters"() {

        given:
        final Project project = objectMapper.readValue(PROJECT, Project.class)
        when:
        ProjectAdapter projectAdapter = new ProjectAdapter(project, 2, new NameDescription(name: "name", description: "description"))
        then:
        assert projectAdapter.getId() == 17
        assert projectAdapter.name == "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)"
        assert projectAdapter.description == "NIH Molecular Libraries Probe"
        assert projectAdapter.capProjectId==20

        assert projectAdapter.getProbes()
        assert projectAdapter.numberOfExperiments == 12
        assert !projectAdapter.annotations

        assert projectAdapter.matchingField.name == "name"
        assert projectAdapter.score == 2
        assert projectAdapter.matchingField.description == "description"
        assert projectAdapter.highlight == "Matched Field: Name"
        assert projectAdapter.biology
        assert !projectAdapter.documents
        assert projectAdapter.hasProbes()
    }

    void "test Expanded Project"() {

        given:
        final ProjectExpanded project = objectMapper.readValue(EXPANDED_PROJECT, ProjectExpanded.class)
        when:
        ProjectAdapter projectAdapter = new ProjectAdapter(project, 2, new NameDescription(name: "name", description: "description"))
        then:
        assert projectAdapter.getId() == 31
        assert projectAdapter.name
        assert projectAdapter.description
        assert projectAdapter.documents
        assert projectAdapter.documents.size() == 1
        final Document document = projectAdapter.documents.get(0)
        assert document.title == "Cytochrome c: can't live with it--can't live without it."
        assert document.pubmedId==9393848
        assert document.resourcePath == "/documents/9393848"

    }
}


