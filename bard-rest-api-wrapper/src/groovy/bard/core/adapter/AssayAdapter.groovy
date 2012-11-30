package bard.core.adapter;


import bard.core.Value
import bard.core.rest.spring.assays.AbstractAssay
import bard.core.interfaces.*

public class AssayAdapter implements AssayAdapterInterface {
    private AbstractAssay assay

    public AssayAdapter() {

    }

    public AssayAdapter(AbstractAssay assay) {
        this.assay = assay
    }

    public String getName() {
        return assay.name
    }

    public Long getCapAssayId() {
        return assay.getCapAssayId()
    }



    public AssayType getType() {
        int assayType = this.assay.getType()
        if (assayType >= 0) {
            return AssayType.valueOf(assayType)
        }
        return null
    }

    public AssayRole getRole() {
        return AssayRole.valueOf(assay.classification?.intValue());
    }

    public AssayCategory getCategory() {
        return AssayCategory.valueOf(assay?.category?.intValue())
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
        return assay.getKegg_disease_names()
    }
    //TODO Perhaps convert to values
    public List<String> getKeggDiseaseCategories() {
        return assay.getKegg_disease_cat()
    }

    //TODO: Change to values?
    public Map<String, List<String>> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        if (assay) {
            annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, assay.getKegg_disease_cat())
            annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, assay.getKegg_disease_names())
        }
        return annos;
    }
}
