package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.experiment.Experiment
import bard.core.rest.spring.project.Project

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder([
"aid",
"bardAssayId",
"capAssayId",
"category",
"type",
"summary",
"assays",
"classification",
"name",
"source",
"grantNo",
"deposited",
"updated",
"documents",
"targets",
"experiments",
"projects",
"kegg_disease_names",
"kegg_disease_cat",
"resourcePath",
"description",
"protocol",
"comments"
])
public class ExpandedAssay extends AbstractAssay {
    @JsonProperty("documents")
    private List<Document> documents = new ArrayList<Document>();
    @JsonProperty("targets")
    private List<Target> targets = new ArrayList<Target>();
    @JsonProperty("experiments")
    private List<Experiment> experiments = new ArrayList<Experiment>();
    @JsonProperty("projects")
    private List<Project> projects = new ArrayList<Project>();
    @JsonProperty("comments")
    private String comments;

    @JsonProperty("comments")
    public String getComments() {
        return this.comments;
    }

    @JsonProperty("comments")
    public void setComments(String comments) {
        this.comments = comments;
    }

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
    public List<Experiment> getExperiments() {
        return experiments;
    }

    @JsonProperty("experiments")
    public void setExperiments(List<Experiment> experiments) {
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
