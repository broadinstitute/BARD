package bard.core.rest.spring.compounds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.core.rest.spring.util.SearchResult

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpandedCompoundResult extends SearchResult {


    @JsonProperty("docs")
    private List<Compound> compounds = new ArrayList<Compound>();


    @JsonProperty("docs")
    public List<Compound> getCompounds() {
        return compounds;
    }

    @JsonProperty("docs")
    public void setCompounds(List<Compound> compounds) {
        this.compounds = compounds;
    }
}