package bard.core.adapter

import bard.core.interfaces.AssayCategory
import bard.core.interfaces.AssayRole
import bard.core.interfaces.AssayType
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.util.NameDescription
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AssayAdapterUnitSpec extends Specification {
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



    void "test Highlight"() {
        given:
        final Assay assay = new Assay()

        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay)
        then:
        assert !assayAdapter.highlight

    }

    void "test getters"() {

        given:
        final Assay assay = objectMapper.readValue(ASSAY, Assay.class)
        Double score = 2
        NameDescription nameDescription = new NameDescription(description: "description", name: "name")
        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay, score, nameDescription)

        then:
        assert assayAdapter.name == "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)"
        assert assayAdapter.getCapAssayId() == 4406
        assert assayAdapter.getBardAssayId() == 17
        assert assayAdapter.getId() == 17
        assert assayAdapter.aid == 2162
        assert assayAdapter.source == "NCGC"
        assert assayAdapter.keggAnnotations
        assert !assayAdapter.keggDiseaseCategories
        assert !assayAdapter.keggDiseaseNames
        assert !assayAdapter.getDescription()
        assert !assayAdapter.annotations
        assert assayAdapter.getRole() == AssayRole.Primary
        assert assayAdapter.getType() == AssayType.Other
        assert !assayAdapter.protocol
        assert !assayAdapter.comments
        assert assayAdapter.getCategory() == AssayCategory.MLSCN
        assert assayAdapter.score == score
        assert assayAdapter.matchingField.name == nameDescription.name
        assert assayAdapter.matchingField.description == nameDescription.description
        assert assayAdapter.highlight == "Score: 2.0 Matched Field: name"
    }


}

