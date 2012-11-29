package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude

import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.assays.Assay

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
public class ExperimentShow extends ExperimentAbstract {

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
