package bard.core.rest.spring.experiment


import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityData extends JsonUtil {
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("dictElemId")
    private long dictElemId;
    @JsonProperty("value")
    private String value;
    @JsonProperty("responseUnit")
    private String responseUnit;
    @JsonProperty("testConc")
    private Double testConcentration;
    @JsonProperty("testConcUnit")
    private String testConcentrationUnit;
    @JsonProperty("qualifierValue")
    private String qualifier;

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("dictElemId")
    public long getDictElemId() {
        return dictElemId;
    }

    @JsonProperty("dictElemId")
    public void setDictElemId(long dictElemId) {
        this.dictElemId = dictElemId;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("responseUnit")
    public String getResponseUnit() {
        return responseUnit;
    }

    @JsonProperty("responseUnit")
    public void setResponseUnit(String responseUnit) {
        this.responseUnit = responseUnit;
    }

    Double getTestConcentration() {
        return testConcentration
    }

    void setTestConcentration(Double testConcentration) {
        this.testConcentration = testConcentration
    }

    String getTestConcentrationUnit() {
        return testConcentrationUnit
    }

    void setTestConcentrationUnit(String testConcentrationUnit) {
        this.testConcentrationUnit = testConcentrationUnit
    }

    String getQualifier() {
        return qualifier
    }

    void setQualifier(String qualifier) {
        this.qualifier = qualifier
    }
}
