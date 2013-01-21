package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil
import bard.core.util.ExperimentalValueUtil
import bard.core.util.ExperimentalValueUnitUtil
import bard.core.util.ExperimentalValueTypeUtil

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

    public String toDisplay(String testConcentrationUnit) {
        String responseValue = this.value
        Double concentrationValue = this.testConcentration
        String responseString = ""
        String concentrationString = ""
        if (responseValue) {
            ExperimentalValueUtil resp = new ExperimentalValueUtil(new Double(responseValue), false)
            responseString = resp.toString()
        }
        if (concentrationValue) {
            ExperimentalValueUtil experimentalValueUtil =
                new ExperimentalValueUtil(concentrationValue,
                        ExperimentalValueUnitUtil.getByValue(testConcentrationUnit),
                        ExperimentalValueTypeUtil.numeric)
            concentrationString = "@ " + experimentalValueUtil.toString()
        }
        return "${responseString} ${concentrationString}"
    }

    public String displayActivity() {
        String responseValue = this.value
        if (responseValue) {
            ExperimentalValueUtil response = new ExperimentalValueUtil(new Double(responseValue), false)
            return response.toString()
        }

        return ""
    }

    public String displayConcentration(String testConcentrationUnit) {
        Double concentrationValue = this.testConcentration
        if (concentrationValue) {
            ExperimentalValueUtil experimentalValueUtil =
                new ExperimentalValueUtil(concentrationValue,
                        ExperimentalValueUnitUtil.getByValue(testConcentrationUnit),
                        ExperimentalValueTypeUtil.numeric)
            return experimentalValueUtil.toString()
        }
        return ""
    }
}
