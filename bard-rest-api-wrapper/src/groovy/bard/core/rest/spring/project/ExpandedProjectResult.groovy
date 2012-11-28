package bard.core.rest.spring.project;


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.SearchResult
import bard.core.rest.spring.project.Project

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpandedProjectResult extends SearchResult {

    @JsonProperty("docs")
    private List<Project> projects = new ArrayList<Project>();

    @JsonProperty("docs")
    public List<Project> getProjects() {
        return this.projects;
    }

    @JsonProperty("docs")
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}