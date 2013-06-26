package bard.core.adapter;


import bard.core.Probe
import bard.core.interfaces.ProjectAdapterInterface
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.project.ProjectAbstract
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import bard.core.rest.spring.util.Target
import bard.core.util.MatchedTermsToHumanReadableLabelsMapper

public class ProjectAdapter implements ProjectAdapterInterface {
    final ProjectAbstract project
    final Double score
    final NameDescription matchingField
    final BardAnnotation annotations

    public ProjectAdapter(ProjectAbstract project, Double score = 0, NameDescription nameDescription = null, BardAnnotation annotations = null) {
        this.project = project
        this.score = score
        this.matchingField = nameDescription
        this.annotations = annotations
    }
    @Override
    public String getExperimentType(Long experimentId){
        return this.getExperimentTypes()?.get(experimentId)
    }
    @Override
    public Map<Long, String> getExperimentTypes() {
        return this.project?.getExperimentTypes();
    }
    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if (matchFieldName) {
            matchFieldName = MatchedTermsToHumanReadableLabelsMapper.matchTermsToHumanReadableLabels(matchFieldName)
            return "Matched Field: " + matchFieldName
        }
        return ""

    }

    public boolean hasProbes() {
        return project?.hasProbes()
    }

    public Double getScore() {
        return score
    }

    public NameDescription getMatchingField() {
        return matchingField
    }

    public Long getId() {
        return project.bardProjectId

    }

    public String getName() {
        return project.name
    }

    public String getDescription() {
        return project.description
    }


    public List<Probe> getProbes() {
        final List<Probe> probes = new ArrayList<Probe>()
        final List<Compound> compounds = project.getProbes()
        for (Compound compound : compounds) {
            Probe probe = new Probe(compound)
            probes.add(probe)
        }
        return probes
    }


    public Integer getNumberOfExperiments() {
        return project.experimentCount.intValue()
    }

    public List<Document> getDocuments() {
        if (project instanceof ProjectExpanded) {
            return ((ProjectExpanded) project).getPublications()
        }
        return []
    }

    public List<Target> getTargets() {
        return project.getTargets()
    }

    public BardAnnotation getAnnotations() {

        return this.annotations
    }

    public Long getCapProjectId() {
        return project.getCapProjectId()
    }
}
