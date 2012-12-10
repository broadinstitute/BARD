package bard.core.rest.spring.substances

import bard.core.rest.spring.util.SearchResult
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubstanceResult extends SearchResult {


    @JsonProperty("collection")
    private List<Substance> substances = new ArrayList<Substance>()


    @JsonProperty("collection")
    public List<Substance> getSubstances() {
        return this.substances
    }

    @JsonProperty("collection")
    public void setSubstances(List<Substance> substances) {
        this.substances = substances
    }
}