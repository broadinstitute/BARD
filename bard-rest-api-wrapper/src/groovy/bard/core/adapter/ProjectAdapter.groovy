package bard.core.adapter;


import bard.core.DataSource
import bard.core.Probe
import bard.core.Value
import bard.core.interfaces.EntityNamedSources
import bard.core.rest.spring.compounds.Compound
import bard.core.interfaces.ProjectAdapterInterface
import bard.core.rest.spring.util.NameDescription
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.project.ProjectAbstract


public class ProjectAdapter implements ProjectAdapterInterface {
    final ProjectAbstract project
    final Double score
    final NameDescription matchingField

    public ProjectAdapter(ProjectAbstract project, Double score=0, NameDescription nameDescription=null) {
        this.project = project
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
    public boolean hasProbes(){
        return project?.hasProbes()
    }
    public Double getScore() {
        return score
    }

    public NameDescription getMatchingField() {
        return matchingField
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
            Probe probe = new Probe(compound.cid.toString(), compound.probeId, compound.url, compound.smiles)
            probes.add(probe)
        }
        return probes
    }


    public Integer getNumberOfExperiments() {
        return project.experimentCount.intValue()
    }
    public List<Document> getDocuments(){
        return project.getPublications()
    }
    public List<Target> getTargets(){
       return project.getTargets()
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
    public int getNumberOfAnnotations(){
        return this.annotations.size() + this.getKeggDiseaseCategories().size() + this.getKeggDiseaseNames().size()
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
    public List<String> getKeggDiseaseNames() {
        return project.getKegg_disease_names()
    }
    //TODO Perhaps convert to values
    public List<String> getKeggDiseaseCategories() {
        return project.getKegg_disease_cat()
    }

}
