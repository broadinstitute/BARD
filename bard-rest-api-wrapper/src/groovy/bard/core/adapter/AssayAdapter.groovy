package bard.core.adapter;


import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import bard.core.rest.spring.util.Target
import bard.core.interfaces.*
import bard.core.rest.spring.assays.*

public class AssayAdapter implements AssayAdapterInterface{
    final AbstractAssay assay
    final Double score
    final NameDescription matchingField
    final List<BardAnnotation> annotations;


    public AssayAdapter(final AbstractAssay assay, final Double score=0, final NameDescription nameDescription=null, final List<BardAnnotation> annotations = [] ) {
        this.assay = assay
        this.score = score
        this.matchingField = nameDescription
        this.annotations = annotations
    }

    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if(matchFieldName){
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
    public String getTitle(){
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

    public List<BardAnnotation> getAnnotations() {
        return annotations
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
    public List<Document> getDocuments(){
        if(assay instanceof ExpandedAssay){
            ExpandedAssay expandedAssay = (ExpandedAssay)assay
            return expandedAssay.getDocuments()
        }
       return []
    }
    public List<Target> getTargets(){
        if(assay instanceof ExpandedAssay){
            ExpandedAssay expandedAssay = (ExpandedAssay)assay
            return expandedAssay.getTargets()
        }
        return []
    }
    public List<String> getTargetIds(){
        List<String> targetIds = []
        if(assay instanceof ExpandedAssay){
            ExpandedAssay expandedAssay = (ExpandedAssay)assay
            targetIds << expandedAssay.targets*.acc
        }
        if(assay instanceof Assay) {
            Assay assay1 = (Assay)assay
            targetIds << assay1.targetIds
        }
        return targetIds
    }
    public List<String> getDocumentIds(){
        List<String> documentIds = []
        if(assay instanceof ExpandedAssay){
            ExpandedAssay expandedAssay = (ExpandedAssay)assay
            documentIds << expandedAssay.documents*.pubmedId
        }
        if(assay instanceof Assay) {
            Assay assay1 = (Assay)assay
            documentIds << assay1.documentIds
        }
        return documentIds
    }
}
