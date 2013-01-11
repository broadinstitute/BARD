package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/9/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcentrationResponsePoint extends JsonUtil {
    @JsonProperty("childElements")
    private List<ActivityData> childElements = new ArrayList<ActivityData>();

    @JsonProperty("testConc")
    private double testConcentration;
    @JsonProperty("value")
    private String value;


    @JsonProperty("testConc")
    public double getTestConcentration() {
        return testConcentration;
    }

    @JsonProperty("testConc")
    public void setTestConcentration(double testConcentration) {
        this.testConcentration = testConcentration;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("childElements")
    public List<ActivityData> getChildElements() {
        return childElements;
    }

    @JsonProperty("childElements")
    public void setChildElements(List<ActivityData> childElements) {
        this.childElements = childElements;
    }

}
