package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Assay extends AbstractAssay {
    @JsonProperty("documents")
    private List<String> documents = new ArrayList<String>();
    @JsonProperty("targets")
    private List<String> targets = new ArrayList<String>();
    @JsonProperty("experiments")
    private List<String> experiments = new ArrayList<String>();
    @JsonProperty("projects")
    private List<String> projects = new ArrayList<String>();






    @JsonProperty("documents")
    public List<String> getDocuments() {
        return documents;
    }

    @JsonProperty("documents")
    public void setDocuments(List<String> documents) {
        this.documents = documents;
    }

    @JsonProperty("targets")
    public List<String> getTargets() {
        return targets;
    }

    @JsonProperty("targets")
    public void setTargets(List<String> targets) {
        this.targets = targets;
    }

    @JsonProperty("experiments")
    public List<String> getExperiments() {
        return experiments;
    }

    @JsonProperty("experiments")
    public void setExperiments(List<String> experiments) {
        this.experiments = experiments;
    }

    @JsonProperty("projects")
    public List<String> getProjects() {
        return projects;
    }

    @JsonProperty("projects")
    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

}
