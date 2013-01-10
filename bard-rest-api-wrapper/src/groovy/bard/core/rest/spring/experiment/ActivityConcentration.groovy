package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonProperty

public class ActivityConcentration extends ActivityData {

    private ConcentrationResponseSeries concResponseSeries

    @JsonProperty("childElements")
    private List<ActivityData> childElements

    @JsonProperty("childElements")
    public List<ActivityData> getChildElements() {
        return childElements
    }

    @JsonProperty("childElements")
    public void setChildElements(List<ActivityData> childElements) {
        this.childElements = childElements
    }
    public ConcentrationResponseSeries getConcResponseSeries() {
        return concResponseSeries
    }

    public void setConcResponseSeries(ConcentrationResponseSeries concResponseSeries) {
        this.concResponseSeries = concResponseSeries
    }
}
