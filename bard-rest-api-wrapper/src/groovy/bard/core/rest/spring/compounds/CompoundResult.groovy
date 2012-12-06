package bard.core.rest.spring.compounds

import bard.core.rest.spring.util.SearchResult
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompoundResult extends SearchResult {


    @JsonProperty("collection")
    private List<Compound> compounds = new ArrayList<Compound>();


    @JsonProperty("collection")
    public List<Compound> getCompounds() {
        return compounds;
    }

    @JsonProperty("collection")
    public void setCompounds(List<Compound> compounds) {
        this.compounds = compounds;
    }
    //Map docs to compounds so that the various JSON responses can share this class
    @JsonProperty("docs")
    public List<Compound> getCompds() {
        return this.compounds;
    }

    @JsonProperty("docs")
    public void setCompds(List<Compound> compounds) {
        this.compounds = compounds;
    }
}