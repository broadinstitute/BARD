package bard.core.rest.spring.assays

import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.Target
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpandedAssay extends AbstractAssay {
    @JsonProperty("documents")
    private List<Document> documents = new ArrayList<Document>();
    @JsonProperty("targets")
    private List<Target> targets = new ArrayList<Target>();
    @JsonProperty("experiments")
    private List<ExperimentSearch> experiments = new ArrayList<ExperimentSearch>();
    @JsonProperty("projects")
    private List<Project> projects = new ArrayList<Project>();

    @JsonProperty("documents")
    public List<Document> getDocuments() {
        return documents;
    }

    @JsonProperty("documents")
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @JsonProperty("targets")
    public List<Target> getTargets() {
        return targets;
    }

    @JsonProperty("targets")
    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    @JsonProperty("experiments")
    public List<ExperimentSearch> getExperiments() {
        return experiments;
    }

    @JsonProperty("experiments")
    public void setExperiments(List<ExperimentSearch> experiments) {
        this.experiments = experiments;
    }

    @JsonProperty("projects")
    public List<Project> getProjects() {
        return projects;
    }

    @JsonProperty("projects")
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
