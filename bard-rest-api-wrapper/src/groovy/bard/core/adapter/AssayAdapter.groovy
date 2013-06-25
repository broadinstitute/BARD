package bard.core.adapter
import bard.core.interfaces.*
import bard.core.rest.spring.assays.*
import bard.core.rest.spring.biology.BiologyEntity
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import bard.core.util.MatchedTermsToHumanReadableLabelsMapper

public class AssayAdapter implements AssayAdapterInterface {
    final AbstractAssay assay
    final Double score
    final NameDescription matchingField
    final BardAnnotation annotations;


    public AssayAdapter(final AbstractAssay assay, final Double score = 0, final NameDescription nameDescription = null, final BardAnnotation annotations = null) {
        this.assay = assay
        this.score = score
        this.matchingField = nameDescription
        this.annotations = annotations
    }
    @Override
    String getAssayStatus(){
        return this.assay.getAssayStatus();
    }
    @Override
    String getDesignedBy(){
        return this.assay.getDesignedBy();
    }
    @Override
    String getAssayTypeString(){
        return this.assay.getAssayType();
    }
    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if (matchFieldName) {
            matchFieldName = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(matchFieldName)
            //TODO: Talk to Steve about formatting
            return "Matched Field: " + matchFieldName
        }
        return ""

    }

    Double getScore() {
        return this.score
    }

    NameDescription getMatchingField() {
        return this.matchingField
    }

    public String getName() {
        return assay.name
    }

    public String getTitle() {
        return assay.title
    }

    public Long getCapAssayId() {
        return assay.getCapAssayId()
    }

    public Long getBardAssayId() {
        return assay.getBardAssayId()
    }



    public AssayType getType() {
        int assayType = this.assay.getType()
        return AssayType.valueOf(assayType)
    }

    public AssayRole getRole() {
        return AssayRole.valueOf(assay.classification.intValue());
    }

    public AssayCategory getCategory() {
        return AssayCategory.valueOf(assay.category.intValue())
    }

    public String getLastUpdatedDate() {
        return assay.updated
    }

    public String getDescription() {
        return assay.description
    }

    public Long getId() {
        return assay.getId()
    }

    public String getProtocol() {
        return assay.protocol
    }

    public String getComments() {
        return assay.getComments()
    }

    public Long getAid() {
        return assay.aid
    }

    public String getSource() {
        return assay.source
    }

    public BardAnnotation getAnnotations() {
        return annotations
    }

    public MinimumAnnotation getMinimumAnnotation() {
        return assay.getMinimumAnnotation()
    }

    public List<String> getKeggDiseaseNames() {
        return assay.getKegg_disease_names()
    }
    //TODO Perhaps convert to values
    public List<String> getKeggDiseaseCategories() {
        return assay.getKegg_disease_cat()
    }

    //TODO: Change to values?
    public Map<String, List<String>> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, assay.getKegg_disease_cat())
        annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, assay.getKegg_disease_names())
        return annos;
    }

    public List<Document> getDocuments() {
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            return expandedAssay.getDocuments()
        }
        return []
    }

    public List<BiologyEntity> getBiology() {
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            return expandedAssay.getBiology()
        }
        return []
    }

    public List<String> getBiologyIds() {
        List<String> biologyIds = []
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            expandedAssay.biology.collect { BiologyEntity biology ->
                if (biology.serial) {
                  biologyIds.add(biology.serial)
                }
            }
        }
        else if (assay instanceof Assay) {
            Assay assay1 = (Assay) assay
            biologyIds.addAll(assay1.biologyIds)
        }
        return biologyIds
    }

    public List<String> getDocumentIds() {
        List<String> documentIds = []
        if (assay instanceof ExpandedAssay) {
            ExpandedAssay expandedAssay = (ExpandedAssay) assay
            expandedAssay.documents.collect { Document document ->
                if (document.pubmedId) {
                    documentIds.add(document.pubmedId)
                }
            }
        }
        if (assay instanceof Assay) {
            Assay assay1 = (Assay) assay
            documentIds.addAll(assay1.documentIds)
        }
        return documentIds
    }

}
