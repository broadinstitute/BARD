package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.core.rest.spring.util.SearchResult

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssayResult extends SearchResult {
    @JsonProperty("collection")
    private List<Assay> assays = new ArrayList<Assay>();

    @JsonProperty("collection")
    public List<Assay> getAssays() {
        return this.assays;
    }

    @JsonProperty("collection")
    public void setAssays(List<Assay> assays) {
        this.assays = assays;
    }
}
