package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.core.rest.spring.util.SearchResult
import bard.core.rest.spring.util.NameDescription

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssayResult extends SearchResult {
    private List<Assay> assays = new ArrayList<Assay>();

    @JsonProperty("collection")
    public List<Assay> getAssays() {
        return this.assays;
    }

    @JsonProperty("collection")
    public void setAssays(List<Assay> assays) {
        this.assays = assays;
    }

    /**
     * Free text searches have a root node of "docs"
     * Note that this returns assays, same thing as "collection"
     * @return
     */
    @JsonProperty("docs")
    public List<Assay> getAssayDocs() {
        return this.assays;
    }

    @JsonProperty("docs")
    public void setAssayDocs(List<Assay> assays) {
        this.assays = assays;
    }


}
