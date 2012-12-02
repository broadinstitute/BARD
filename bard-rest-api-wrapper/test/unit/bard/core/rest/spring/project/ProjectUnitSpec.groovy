package bard.core.rest.spring.project

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String PROJECT = '''
    {
       "projectId": 17,
       "category": 0,
       "type": 0,
       "classification": 0,
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
           "X01 MH083262-01",
           "NCGC"
        ],
       "ak_dict_label":
       [
           "grant number",
           "laboratory name"
       ],
       "kegg_disease_names":
       [
           "Lou Gehrig's disease"
       ],
       "kegg_disease_cat":
       [
           "Neurodegenerative disease"
       ],
       "grantNo": null,
       "deposited": null,
       "updated": null,
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
       "aids":
       [
           1472
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

    void "test serialization to Project"() {
        when:
        final Project project = objectMapper.readValue(PROJECT, Project.class)
        then:
        assert project.getId() == 17
        assert project.getProjectId() == 17
        assert project.getName() == "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)"
        assert project.getType() == 0
        assert project.getCategory() == 0
        assert project.getSource() == "NCGC"
        assert project.getDescription() == "NIH Molecular Libraries Probe"
        assert !project.getGrantNo()
        assert !project.getDeposited()
        assert !project.getUpdated()
        assert project.getKegg_disease_names()
        assert project.getKegg_disease_cat()
        assert project.getTargets()
        assert project.getAids()
        assert project.getEids()
        assert project.experimentCount == 12
        assert project.probes
        assert project.probeIds
        assert project.getResourcePath() == "/projects/17"
        assert project.getAk_dict_label()
        assert project.getAv_dict_label()
        assert !project.getGobp_id()
        assert !project.getGobp_term()
        assert !project.getGocc_id()
        assert !project.getGomf_id()
        assert !project.getGomf_term()
        assert !project.getGocc_term()
        assert project.getClassification() == 0
        assert project.getPublications() == null
    }


}

