package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.core.rest.spring.util.SearchResult

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpandedAssayResult extends SearchResult {
    @JsonProperty("docs")
    private List<ExpandedAssay> assays = new ArrayList<ExpandedAssay>();

    @JsonProperty("docs")
    public List<ExpandedAssay> getAssays() {
        return this.assays;
    }

    @JsonProperty("docs")
    public void setAssays(List<ExpandedAssay> assays) {
        this.assays = assays;
    }

}
