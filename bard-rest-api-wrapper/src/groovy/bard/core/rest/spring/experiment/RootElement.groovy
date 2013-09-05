package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RootElement extends ActivityData {


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
    /**
     *
     * @return true if the display name is "Outcome"
     */
    public boolean hasOutcome(){
        return this.pubChemDisplayName=="PubChem outcome"
    }

}

