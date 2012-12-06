package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Assay extends AbstractAssay {
    @JsonProperty("documents")
    private List<String> documentIds = new ArrayList<String>();
    @JsonProperty("targets")
    private List<String> targetIds = new ArrayList<String>();
    @JsonProperty("experiments")
    private List<String> experimentIds = new ArrayList<String>();
    @JsonProperty("projects")
    private List<String> projectsIds = new ArrayList<String>();


    @JsonProperty("documents")
    public List<String> getDocumentIds() {
        return documentIds;
    }

    @JsonProperty("documents")
    public void setDocumentIds(List<String> documentIds) {
        this.documentIds = documentIds;
    }

    @JsonProperty("targets")
    public List<String> getTargetIds() {
        return targetIds;
    }

    @JsonProperty("targets")
    public void setTargetIds(List<String> targetIds) {
        this.targetIds = targetIds;
    }

    @JsonProperty("experiments")
    public List<String> getExperiments() {
        return experimentIds;
    }

    @JsonProperty("experiments")
    public void setExperiments(List<String> experimentIds) {
        this.experimentIds = experimentIds;
    }

    @JsonProperty("projects")
    public List<String> getProjectIds() {
        return projectsIds;
    }

    @JsonProperty("projects")
    public void setProjectsIds(List<String> projectIds) {
        this.projectsIds = projectIds;
    }

}
