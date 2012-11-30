package bard.core.adapter;


import bard.core.DataSource
import bard.core.Probe
import bard.core.Value
import bard.core.interfaces.EntityNamedSources
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.project.Project
import bard.core.interfaces.ProjectAdapterInterface

public class ProjectAdapter implements ProjectAdapterInterface {
    Project project


    public ProjectAdapter(Project project) {
        this.project = project
    }


    public Long getId() {
        return project.projectId

    }

    public String getName() {
        return project.name
    }

    public String getDescription() {
        return project.description
    }

    public String getGrantNumber() {
        return project.grantNo ?: getDictionaryTerms().get("grant number")
    }

    public String getLaboratoryName() {
        return getDictionaryTerms().get("laboratory name")
    }

    public List<Probe> getProbes() {
        final List<Probe> probes = new ArrayList<Probe>()
        final List<Compound> compounds = project.getProbes()
        for (Compound compound : compounds) {
            Probe probe = new Probe(compound?.cid?.toString(), compound.probeId, compound.url, compound.smiles)
            probes.add(probe)
        }
        return probes

    }


    public Integer getNumberOfExperiments() {
        return project.experimentCount?.intValue() ?: 0
    }

    public Collection<Value> getAnnotations() {
        final Collection<Value> annos = new ArrayList<Value>();
        final Map<String, String> terms = getDictionaryTerms()
        for (String key : terms.keySet()) {
            Value value = new bard.core.StringValue(DataSource.DEFAULT, key, terms.get(key))
            annos.add(value)
        }

        return annos;
    }

    public Map<String, String> getDictionaryTerms() {
        Map<String, String> dictionaryTerms = [:]
        final List<String> keys = project.getAk_dict_label()
        final List<String> values = project.getAv_dict_label()
        assert keys.size() == values.size()
        int nkeys = keys.size()
        for (int index = 0; index < nkeys; index++) {
            String key = keys.get(index)
            String value = values.get(index)
            dictionaryTerms.put(key, value)
        }
        return dictionaryTerms
    }

    public Map<String, List<String>> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, project.getKegg_disease_cat())
        annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, project.getKegg_disease_names())
        return annos;
    }

}
