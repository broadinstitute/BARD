package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import bard.core.rest.spring.assays.ExpandedAssay

import bard.core.rest.spring.util.SearchResult
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentResult extends SearchResult {
    @JsonProperty("collection")
    private List<Experiment> experiments = new ArrayList<Experiment>();
    @JsonProperty("collection")
    public List<Experiment> getExperiments() {
        return this.experiments;
    }
    @JsonProperty("collection")
    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }
}
