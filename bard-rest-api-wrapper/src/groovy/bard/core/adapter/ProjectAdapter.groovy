package bard.core.adapter;


import bard.core.Probe
import bard.core.interfaces.ProjectAdapterInterface
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.assays.Comp
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.project.ProjectAbstract
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.NameDescription
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.assays.Context

public class ProjectAdapter implements ProjectAdapterInterface {
    final ProjectAbstract project
    final Double score
    final NameDescription matchingField
    final List<BardAnnotation> annotations = []

    public ProjectAdapter(ProjectAbstract project, Double score = 0, NameDescription nameDescription = null, List<BardAnnotation> annotations = []) {
        this.project = project
        this.score = score
        this.matchingField = nameDescription
        this.annotations = annotations
    }

    @Override
    String getHighlight() {
        String matchFieldName = getMatchingField()?.getName()
        if (matchFieldName) {
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
        return project.projectId

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
            Probe probe = new Probe(compound.cid.toString(), compound.probeId, compound.url, compound.smiles)
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

    public List<BardAnnotation> getAnnotations() {
        final List<String> keggDiseaseCat = project.getKegg_disease_cat()
        final List<String> keggDiseaseNames = project.getKegg_disease_names()

        //add kegg annotations
        //TODO: Note that the Kegg annotations would become part of the main annotations in v13 of the API
        if (keggDiseaseCat || keggDiseaseNames) {
            BardAnnotation bardAnnotation
            if (!this.annotations) {
                bardAnnotation = new BardAnnotation()
                this.annotations.add(bardAnnotation)
            }
            bardAnnotation = annotations.get(0)

            for (String keggName: project.kegg_disease_names) {
                Comp comp = new Comp(key: "Disease", display: "KEGG Disease Name", value: keggName)
                bardAnnotation.otherAnnotations.add(comp)
            }

            for (String keggCat: project.kegg_disease_cat) {
                Comp comp = new Comp(key: "Disease", display: "KEGG Disease Category", value: keggCat)
                bardAnnotation.otherAnnotations.add(comp)
            }

        }
        return this.annotations
    }

    public int getNumberOfAnnotations() {
        return this.annotations.size()
    }

    public Boolean areAnnotationsEmpty() {
        Boolean foundSomething = this.annotations.find {BardAnnotation annotation ->
            annotation.contexts.find {Context context ->
                context.getComps().find()
            }
        }

        return foundSomething ?: false
    }
}
