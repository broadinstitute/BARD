package bard.core.adapter;


import bard.core.*
import bard.core.interfaces.EntityNamedSources

public class ProjectAdapter extends EntityAdapter<Project> {
    List<String> srcNames = [
            EntityNamedSources.CAPAnnotationSource,
            EntityNamedSources.GOBPAnnotationSource,
            EntityNamedSources.GOMFAnnotationSource,
            EntityNamedSources.GOCCAnnotationSource,
            EntityNamedSources.KEGGDiseaseCategoryAnnotationSource,
            EntityNamedSources.KEGGDiseaseNameAnnotationSource
    ]

    public ProjectAdapter() {}

    public ProjectAdapter(Project project) {
        super(project);
    }



    public String getDescription() {
        return getProject().getDescription();
    }

    public String getGrantNumber() {
        Value grant = getEntity().getValue("grant number");
        return grant ? (String) grant.getValue() : null;
    }

    public String getLaboratoryName() {
        Value labs = getEntity().getValue("laboratory name");
        return labs ? (String) labs.getValue() : null;
    }

    public List<Probe> getProbes() {
        return getProject().getProbes();
    }

    public Project getProject() { return getEntity(); }

    public void setProject(Project proj) {
        setEntity(proj);
    }

    public Integer getNumberOfExperiments() {
        Value expt = getEntity().getValue(Project.NumberOfExperimentsValue);
        return expt ? (Integer) expt.getValue() : null;
    }

    public Collection<Value> getAnnotations() {
        Collection<Value> annos = new ArrayList<Value>();

        for (String srcName : this.srcNames) {
            Collection<Value> values = entity.getValues(new DataSource(srcName));
            if (values) { annos.addAll(values)}
        }
        return annos;
    }
}
