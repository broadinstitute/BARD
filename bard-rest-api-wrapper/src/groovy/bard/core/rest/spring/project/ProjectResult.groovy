package bard.core.rest.spring.project;


import bard.core.rest.spring.util.SearchResult
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResult extends SearchResult {

    @JsonProperty("collection")
    private List<Project> projects = new ArrayList<Project>();

    @JsonProperty("collection")
    public List<Project> getProjects() {
        return this.projects;
    }

    @JsonProperty("collection")
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}