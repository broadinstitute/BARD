package bard.core.adapter;


import bard.core.Assay
import bard.core.DataSource
import bard.core.Value
import bard.core.interfaces.AssayCategory
import bard.core.interfaces.AssayRole
import bard.core.interfaces.AssayType
import bard.core.interfaces.EntityNamedSources
import bard.core.rest.spring.assays.ExpandedAssay

public class AssayAdapter extends EntityAdapter<Assay> {
    private bard.core.rest.spring.assays.AbstractAssay restAssay
    List<String> srcNames = [EntityNamedSources.CAPAnnotationSource,
            EntityNamedSources.GOBPAnnotationSource,
            EntityNamedSources.GOMFAnnotationSource,
            EntityNamedSources.GOCCAnnotationSource,
            EntityNamedSources.KEGGDiseaseCategoryAnnotationSource,
            EntityNamedSources.KEGGDiseaseNameAnnotationSource]

    public AssayAdapter() {

    }
    public String getName(){
        if(restAssay){
            return restAssay.name
        }
    }
    public AssayAdapter(bard.core.rest.spring.assays.AbstractAssay assay) {
        this.restAssay = assay
    }
    public Long getCapAssayId(){
        if(restAssay){
            return restAssay.getCapAssayId()
        }
    }
    public AssayAdapter(Assay assay) {
        super(assay);
    }

    public AssayType getType() {
        if (restAssay) {
            int assayType = this.restAssay.getType()
            return AssayType.valueOf(assayType)
        }
        return assay.getType()
    }

    public AssayRole getRole() {
        if (restAssay) {
            return AssayRole.valueOf(restAssay.classification?.intValue());
        }
        return getAssay().getRole()
    }

    public AssayCategory getCategory() {
        if (restAssay) {
            return AssayCategory.valueOf(restAssay.category?.intValue())
        }
        return getAssay().getCategory()
    }

    public String getDescription() {
        if (restAssay) {
            return restAssay.description
        }
        return getEntity().getDescription()
    }
    public Long getId(){
        if(restAssay){
            return restAssay.getId()
        }
        return null
    }
    public String getProtocol() {
        if (restAssay) {
            return restAssay.protocol
        }
        return getAssay().getProtocol()
    }

    public String getComments() {
        if (restAssay) {
            return restAssay.getComments()
        }
        return getAssay().getComments()
    }
    public Long getAid(){
        if(restAssay){
            return restAssay.aid
        }
        return getAssay().getValue('AssayPubChemAID').value
    }
    public Assay getAssay() { return (Assay) getEntity(); }

    public void setAssay(Assay assay) {
        setEntity(assay);
    }
    public String getSource(){
        if(restAssay){
            return restAssay.source
        }
        else{
            return getAssay().getValue('AssaySource').value
        }
    }
    public Collection<Value> getAnnotations() {
        Collection<Value> annos = new ArrayList<Value>();
//
//        for (String srcName : this.srcNames) {
//            Collection<Value> values = entity.getValues(new DataSource(srcName));
//            if (values) {
//                annos.addAll(values)
//            }
//        }
        return annos;
    }

    public List<String> getKeggDiseaseNames() {
        if (restAssay) {
            return restAssay.getKegg_disease_names()
        }
        else {
            List<String> annos = new ArrayList<Value>();
            Collection<Value> values = entity.getValues(new DataSource(EntityNamedSources.KEGGDiseaseNameAnnotationSource));
            if (values) {
                for (Value value : values) {
                    annos.add(value.value.toString())
                }
            }
            return annos
        }

    }
    //TODO Perhaps convert to values
    public List<String> getKeggDiseaseCategories() {
        if (restAssay) {
            return restAssay.getKegg_disease_cat()
        }
        else {
            List<String> annos = new ArrayList<Value>();
            Collection<Value> values = entity.getValues(new DataSource(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource));
            if (values) {
                for (Value value : values) {
                    annos.add(value.value.toString())
                }
            }
            return annos
        }
    }

    //TODO: Change to values?
    public List<String> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        if (restAssay) {
            annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, restAssay.getKegg_disease_cat())
            annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, restAssay.getKegg_disease_names())
        }
        return annos;
    }
}
