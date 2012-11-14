package bard.core.adapter;


import bard.core.Assay
import bard.core.DataSource
import bard.core.Value

import bard.core.interfaces.AssayType
import bard.core.interfaces.EntityNamedSources
import bard.core.interfaces.AssayCategory
import bard.core.interfaces.AssayRole

public class AssayAdapter extends EntityAdapter<Assay> {
    List<String> srcNames = [EntityNamedSources.CAPAnnotationSource,
            EntityNamedSources.GOBPAnnotationSource,
            EntityNamedSources.GOMFAnnotationSource,
            EntityNamedSources.GOCCAnnotationSource,
            EntityNamedSources.KEGGDiseaseCategoryAnnotationSource,
            EntityNamedSources.KEGGDiseaseNameAnnotationSource]

    public AssayAdapter() {}

    public AssayAdapter(Assay assay) {
        super(assay);
    }

    public AssayType getType() {
        return assay.getType()
    }

    public AssayRole getRole() {
        return getAssay().getRole()
    }

    public AssayCategory getCategory() {
        return getAssay().getCategory()
    }

    public String getDescription() {
        return getEntity().getDescription()
    }

    public String getProtocol() {
        return getAssay().getProtocol()
    }

    public String getComments() {
        return getAssay().getComments()
    }

    public Assay getAssay() { return (Assay) getEntity(); }

    public void setAssay(Assay assay) {
        setEntity(assay);
    }

    public Collection<Value> getAnnotations() {
        Collection<Value> annos = new ArrayList<Value>();

        for (String srcName : this.srcNames) {
            Collection<Value> values = entity.getValues(new DataSource(srcName));
            if (values != null) annos.addAll(values);
        }
        return annos;
    }
}
