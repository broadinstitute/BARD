package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * For Show Experiment Pages/ getId pages
 *
 *
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Experiment extends ExperimentAbstract {

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
}
