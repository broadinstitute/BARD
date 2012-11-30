package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.SearchResult

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FreeTextAssayResult extends SearchResult {
    @JsonProperty("docs")
    private List<Assay> assays = new ArrayList<Assay>();

    @JsonProperty("docs")
    public List<Assay> getAssays() {
        return this.assays;
    }

    @JsonProperty("docs")
    public void setAssays(List<Assay> assays) {
        this.assays = assays;
    }
}
