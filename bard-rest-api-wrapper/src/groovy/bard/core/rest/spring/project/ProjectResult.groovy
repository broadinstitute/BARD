package bard.core.rest.spring.project;


import bard.core.rest.spring.util.SearchResult
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectResult extends SearchResult {


    private List<Project> projects = new ArrayList<Project>();

    /*
     * Produced when one uses a url such as:
     * http://bard.nih.gov/api/v10/experiments/1048/projects?expand=true
     *
     */
    @JsonProperty("collection")
    public List<Project> getProjects() {
        return this.projects;
    }

    @JsonProperty("collection")
    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    /*
    * We map docs and collections to the same object
     * "docs" is the root of a JSON when one uses urls such as:
     *
     * http://bard.nih.gov/api/v10/search/projects?q="dna repair" & expand=true
     */
    @JsonProperty("docs")
    public List<Project> getProjs() {
        return this.projects;
    }

    @JsonProperty("docs")
    public void setProjs(List<Project> projects) {
        this.projects = projects;
    }
}