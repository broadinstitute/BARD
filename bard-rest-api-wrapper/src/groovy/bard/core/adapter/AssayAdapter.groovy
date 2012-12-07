package bard.core.adapter;


import bard.core.Value
import bard.core.rest.spring.assays.AbstractAssay
import bard.core.rest.spring.util.NameDescription
import bard.core.interfaces.*

public class AssayAdapter implements AssayAdapterInterface {
    final AbstractAssay assay
    final Double score
    final NameDescription matchingField


    public AssayAdapter(final AbstractAssay assay, final Double score=0, final NameDescription nameDescription=null) {
        this.assay = assay
        this.score = score
        this.matchingField = nameDescription
    }

    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if(matchFieldName){
            //TODO: Talk to Steve about formating
            return "Score: " + this.getScore() + " Matched Field: " + matchFieldName
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

    public Collection<Value> getAnnotations() {
        Collection<Value> annos = new ArrayList<Value>();
        return annos;
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
}
