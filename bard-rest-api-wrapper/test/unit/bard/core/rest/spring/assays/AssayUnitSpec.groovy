package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String ASSAY = '''
    {
           "aid": 2162,
           "bardAssayId": 17,
           "capAssayId": 4406,
           "category": 1,
           "type": 0,
           "summary": 0,
           "assays": 0,
           "classification": 0,
           "name": "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)",
           "source": "NCGC",
           "grantNo": null,
           "deposited": null,
           "updated": null,
           "documents":
           [
               17826100,
               17869117,
               16500106,
               16864780
           ],
           "targets":
           [
               "P18054"
           ],
           "experiments":
           [
               17
           ],
           "projects":
           [
               147
           ],
           "kegg_disease_names":
           [
           ],
           "kegg_disease_cat":
           [
           ],
           "resourcePath": "/assays/17"
       }
       '''

    void "test serialization to Assay"() {
        when:
        final Assay assay = objectMapper.readValue(ASSAY, Assay.class)
        then:
        assert assay.getId() == 17
        assert assay.getBardAssayId() == 17
        assert assay.getCapAssayId() == 4406
        assert assay.getName() == "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)"
        assert assay.getType() == 0
        assert assay.getCategory() == 1
        assert assay.getSummary() == 0
        assert assay.getAssays() == 0
        assert assay.getSource() == "NCGC"
        assert !assay.getGrantNo()
        assert !assay.getDeposited()
        assert !assay.getUpdated()
        assert !assay.getKegg_disease_names()
        assert !assay.getKegg_disease_cat()
        assert assay.getDocuments()
        assert assay.getTargets()
        assert assay.getAssayId() == 0
        assert assay.getResourcePath() == "/assays/17"
    }


}

