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
           "title": "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)",
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


    public static final String ASSAY2 = '''
{
"bardAssayId": 43,
"capAssayId": 973,
"category": 0,
"summary": 0,
"assays": 0,
"classification": 0,
"name": "Using DiI-HDL to assay lipid transfer in ldlA[SR-BI] cells Measured in Cell-Based System Using Plate Reader - 2085-01",
"source": null,
"grantNo": null,
"title": "Scarb1, scavenger receptor class B, member 1 [Mus musculus]; cell-based format; transporter assay; using measured value et al",
"designedBy": "Broad Institute",
"deposited": null,
"updated": "2013-04-25",
"assayType": "Regular",
"assayStatus": "Approved",
"documents": [ ],
"targets": [
232,
231
],
"experiments": [
21
],
"projects": [
2
],
"minimumAnnotations": {
"assay footprint": "384-well plate",
"detection method type": "fluorescence intensity",
"detection instrument name": "PerkinElmer EnVision",
"assay format": "cell-based format",
"assay type": "transporter assay"
},
"score": 1,
"resourcePath": "/assays/43"
}'''


    void "test serialization to updated Assay format focusing on MinimumAnnotation"() {
        when:
        final Assay assay = objectMapper.readValue(ASSAY2, Assay.class)
        then:
        assert assay.getBardAssayId() == 43
        assert assay.getCapAssayId() == 973
        assert assay.getName() == "Using DiI-HDL to assay lipid transfer in ldlA[SR-BI] cells Measured in Cell-Based System Using Plate Reader - 2085-01"
        assert assay.getTitle() == "Scarb1, scavenger receptor class B, member 1 [Mus musculus]; cell-based format; transporter assay; using measured value et al"
        assert assay.getAssayStatus() == "Approved"
        assert assay.getClassification() == 0
        assert assay.getAssays() == 0
        assert !assay.getSource()
        assert !assay.getGrantNo()
        assert !assay.getDeposited()
        assert assay.getUpdated() == "2013-04-25"
        assert assay.getMinimumAnnotation() != null
        assert assay.getMinimumAnnotation().getAssayFootprint() == "384-well plate"
        assert assay.getMinimumAnnotation().getDetectionMethodType() == "fluorescence intensity"
        assert assay.getMinimumAnnotation().getDetectionInstrumentName() == "PerkinElmer EnVision"
        assert assay.getMinimumAnnotation().getAssayFormat() == "cell-based format"
        assert assay.getMinimumAnnotation().getAssayType() == "transporter assay"
        assert assay.getMinimumAnnotation().getSpeciesName() == null
        assert assay.getMinimumAnnotation().getCulturedCellName() == null
        assert assay.getMinimumAnnotation().getExcitationWavelength() == null
        assert assay.getMinimumAnnotation().getEmissionWavelength() == null
        assert assay.getMinimumAnnotation().getAbsorbanceWavelength() == null
        assert assay.getMinimumAnnotation().getMeasurementWavelength() == null
    }



    void "test serialization to Assay"() {
        when:
        final Assay assay = objectMapper.readValue(ASSAY, Assay.class)
        then:
        assert assay.getId() == 17
        assert assay.getBardAssayId() == 17
        assert assay.getCapAssayId() == 4406
        assert assay.getName() == "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)"
        assert assay.getTitle() == "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)"
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
        assert assay.getDocumentIds()
        assert assay.getTargetIds()
        assert assay.getAssayId() == 0
        assert assay.getResourcePath() == "/assays/17"
    }




    void "test set assay Id"() {
        when:
        final Assay assay = new Assay()
        assay.setAssayId(2L)
        then:
        assert assay.getId() == 2

    }



}

