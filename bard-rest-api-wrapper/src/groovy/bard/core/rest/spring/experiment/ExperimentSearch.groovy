package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * This is produced from the following URL:
 * http://bard.nih.gov/api/v10/assays/1048?expand=true
 *
 * Look at the nested experiment object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentSearch extends ExperimentAbstract {

    @JsonProperty("assayId")
    private long assayId;
    @JsonProperty("assays")
    private long assays;

    @Override
    public Long getAdid() {
        return assayId
    }


    @JsonProperty("assayId")
    public long getAssayId() {
        return assayId;
    }

    @JsonProperty("assayId")
    public void setAssayId(long assayId) {
        this.assayId = assayId;
    }



    @JsonProperty("assays")
    public long getAssays() {
        return assays;
    }

    @JsonProperty("assays")
    public void setAssays(long assays) {
        this.assays = assays;
    }
}
