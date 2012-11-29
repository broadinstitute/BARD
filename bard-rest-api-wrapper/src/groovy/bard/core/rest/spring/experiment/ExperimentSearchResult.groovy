package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.core.rest.spring.util.SearchResult

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentSearchResult extends SearchResult {


    @JsonProperty("docs")
    private List<ExperimentSearch> experiments = new ArrayList<ExperimentSearch>()


    @JsonProperty("docs")
    public List<ExperimentSearch> getExperiments() {
        return this.experiments
    }

    @JsonProperty("docs")
    public void setExperiments(List<ExperimentSearch> experiments) {
        this.experiments = experiments
    }
}