package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.core.rest.spring.util.SearchResult

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentSearchResult extends SearchResult {


    @JsonProperty("docs")
    private List<Experiment> experiments = new ArrayList<Experiment>()


    @JsonProperty("docs")
    public List<Experiment> getExperiments() {
        return this.experiments
    }

    @JsonProperty("docs")
    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments
    }
}