package bard.core.rest.spring.experiment

import bard.core.rest.spring.assays.Assay
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * For Show Experiment Pages/ getId pages
 *
 * The URL that produces this kind of results:
 *
 *  http://bard.nih.gov/api/v10/experiments/1048?expand=true
 *
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentShow extends ExperimentAbstract {

    //TODO: This should change after de-duplication. Right now there is a 1-1 mapping between
    //assays and experiments
    @Override
    public Long getAdid() {
        if (assays) {
            return assays.get(0).getId()
        }
        return null
    }

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
