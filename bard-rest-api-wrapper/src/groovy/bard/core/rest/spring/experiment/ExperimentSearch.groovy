package bard.core.rest.spring.experiment

import bard.core.rest.spring.assays.Assay
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

//Results of Text Box searches
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentSearch extends ExperimentAbstract {
    @JsonProperty("assayId")
    private long assayId;

    @JsonProperty("assayId")
    public long getAssayId() {
        return assayId;
    }

    @JsonProperty("assayId")
    public void setAssayId(long assayId) {
        this.assayId = assayId;
    }
    @JsonProperty("assays")
    private long assays;
    @JsonProperty("assays")
    public long getAssays() {
        return assays;
    }

    @JsonProperty("assays")
    public void setAssays(long assays) {
        this.assays = assays;
    }
}
