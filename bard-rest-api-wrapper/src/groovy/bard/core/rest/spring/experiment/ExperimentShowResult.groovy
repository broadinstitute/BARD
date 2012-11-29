package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude

import bard.core.rest.spring.util.SearchResult
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentShowResult extends SearchResult {
    @JsonProperty("collection")
    private List<ExperimentShow> experiments = new ArrayList<ExperimentShow>();
    @JsonProperty("collection")
    public List<ExperimentShow> getExperiments() {
        return this.experiments;
    }
    @JsonProperty("collection")
    public void setExperiments(List<ExperimentShow> experiments) {
        this.experiments = experiments;
    }
}
