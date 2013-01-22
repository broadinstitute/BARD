package bard.core.adapter;


import bard.core.DataSource
import bard.core.Probe
import bard.core.Value
import bard.core.interfaces.EntityNamedSources
import bard.core.interfaces.ProjectAdapterInterface
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.project.ProjectAbstract
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.project.ProjectExpanded

public class ProjectAdapter implements ProjectAdapterInterface {
    final ProjectAbstract project
    final Double score
    final NameDescription matchingField
    final List<BardAnnotation> annotations
    public ProjectAdapter(ProjectAbstract project, Double score = 0, NameDescription nameDescription = null, List<BardAnnotation> annotations = []) {
        this.project = project
        this.score = score
        this.matchingField = nameDescription
        this.annotations = annotations
    }
    public Map<String, String> getDictionaryTerms(){
        return [:]
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
        if(project instanceof ProjectExpanded){
           ((ProjectExpanded)project).getPublications()
        }
        return []
    }
    public List<Target> getTargets(){
       return project.getTargets()
    }
    public List<BardAnnotation> getAnnotations() {
      return this.annotations
    }
    public int getNumberOfAnnotations(){
        return this.annotations.size()
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
