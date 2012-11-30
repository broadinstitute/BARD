package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import bard.core.rest.spring.util.SearchResult

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentSearchResult extends SearchResult {



    private List<ExperimentSearch> experiments = new ArrayList<ExperimentSearch>()


    @JsonProperty("docs")
    public List<ExperimentSearch> getExperiments() {
        return this.experiments
    }

    @JsonProperty("docs")
    public void setExperiments(List<ExperimentSearch> experiments) {
        this.experiments = experiments
    }

    /**
     * We use this strategy to get around a mapping issue where the only difference between the
     * json returned is the root node. One has a root node of "docs" while the other has a root node of "collection"
     *
     * @return
     */
    @JsonProperty("collection")
    public List<ExperimentSearch> getExpes() {
        return this.experiments
    }

    @JsonProperty("collection")
    public void setExpes(List<ExperimentSearch> expes) {
        this.experiments = expes
    }
}