package bard.core.adapter;


import bard.core.DataSource
import bard.core.Probe
import bard.core.Project
import bard.core.Value
import bard.core.interfaces.EntityNamedSources
import bard.core.rest.spring.compounds.Compound

public class ProjectAdapter extends EntityAdapter<Project> {
    bard.core.rest.spring.project.Project restProject
    List<String> srcNames = [
            EntityNamedSources.CAPAnnotationSource,
            EntityNamedSources.GOBPAnnotationSource,
            EntityNamedSources.GOMFAnnotationSource,
            EntityNamedSources.GOCCAnnotationSource,
            EntityNamedSources.KEGGDiseaseCategoryAnnotationSource,
            EntityNamedSources.KEGGDiseaseNameAnnotationSource
    ]

    public ProjectAdapter(bard.core.rest.spring.project.Project project) {
        this.restProject = project
    }

    public ProjectAdapter(Project project) {
        super(project);
    }

   public Long getId(){
       if(restProject){
           return restProject.projectId
       }
       return project.id
   }
   public String getName(){
       if(restProject){
           return restProject.name
       }
       return project.name
   }
    public String getDescription() {
        if (restProject) {
            return restProject.description
        }
        return getProject().getDescription();
    }

    public String getGrantNumber() {
        if (restProject) {
            return restProject.grantNo
        }
        Value grant = getEntity().getValue("grant number");
        return grant ? (String) grant.getValue() : null;
    }

    public String getLaboratoryName() {
        if (restProject) {
            return null
        }
        Value labs = getEntity().getValue("laboratory name");
        return labs ? (String) labs.getValue() : null;
    }

    public List<Probe> getProbes() {
        if (restProject) {
            final List<Probe> probes = new ArrayList<Probe>()
            final List<Compound> compounds = restProject.getProbes()
            for (Compound compound : compounds) {
                Probe probe = new Probe(compound.cid, compound.probeId, compound.url, compound.smiles)
                probes.add(probe)
            }
            return probes
        }
        return getProject().getProbes();
    }

    public Project getProject() { return getEntity(); }


    public Integer getNumberOfExperiments() {
        if (restProject) {
            return restProject.experimentCount
        }
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
    //TODO: Change to values?
    public Map<String, String> getDictionaryTerms() {
        Map<String, String> dictionaryTerms = [:]
        if (restProject) {
            final List<String> keys = restProject.getAk_dict_label()
            final List<String> values = restProject.getAv_dict_label()
            assert keys.size() == values.size()
            int nkeys = keys.size()
            for (int index = 0; index < nkeys; index++) {
                String key = keys.get(index)
                String value = values.get(index)
                dictionaryTerms.put(key, value)
            }
        }
        return dictionaryTerms
    }
    //TODO: Change to values?
    public List<String> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        if (restProject) {
            annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, restProject.getKegg_disease_cat())
            annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, restProject.getKegg_disease_names())
        }
        return annos;
    }


}
