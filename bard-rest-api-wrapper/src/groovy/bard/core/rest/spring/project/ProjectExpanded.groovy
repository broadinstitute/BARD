package bard.core.rest.spring.project

import bard.core.rest.spring.assays.Assay

import bard.core.rest.spring.experiment.ExperimentSearch

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectExpanded extends ProjectAbstract {
    @JsonProperty("eids")
    private List<ExperimentSearch> experiments = new ArrayList<ExperimentSearch>();
    @JsonProperty("aids")
    private List<Assay> assays = new ArrayList<Assay>();

    @JsonProperty("eids")
    public List<ExperimentSearch> getExperiments() {
        return experiments;
    }

    @JsonProperty("eids")
    public void setExperiments(List<ExperimentSearch> experiments) {
        this.experiments = experiments;
    }

    @JsonProperty("aids")
    public List<Assay> getAssays() {
        return assays;
    }

    @JsonProperty("aids")
    public void setAssays(List<Assay> assays) {
        this.assays = assays
    }

}
