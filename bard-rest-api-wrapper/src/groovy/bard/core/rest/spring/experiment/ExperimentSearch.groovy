package bard.core.rest.spring.experiment

import bard.core.rest.spring.assays.Assay
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

//Results of Text Box searches
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentSearch extends ExperimentAbstract {
    @JsonProperty("assayId")
    private List<Assay> assays = new ArrayList<Assay>();

    @JsonProperty("assayId")
    public List<Assay> getAssays() {
        return assays;
    }

    @JsonProperty("assayId")
    public void setAssays(List<Assay> assays) {
        this.assays = assays;
    }

}
