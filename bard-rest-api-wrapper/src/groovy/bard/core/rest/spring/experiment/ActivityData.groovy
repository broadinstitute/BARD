package bard.core.rest.spring.experiment

import bard.core.rest.spring.util.DictionaryElement
import bard.core.rest.spring.util.JsonUtil
import bard.rest.api.wrapper.Dummy
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityData extends JsonUtil {
    @JsonProperty("displayName")
    private String pubChemDisplayName;
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
    @JsonIgnore
    Dummy dummy = new Dummy()

    public String getDictionaryLabel() {
        if (dictElemId) {
            final DictionaryElement dictionaryElement = dummy.dataExportRestService.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.label
            }
        }
        return pubChemDisplayName

    }

    public String getDictionaryDescription() {
        if (dictElemId) {
            final DictionaryElement dictionaryElement = dummy.dataExportRestService.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.description
            }
        }
        return pubChemDisplayName

    }

    public String toDisplay() {
        //look up the element in the CAP
        String displayName = getDictionaryLabel()
        final StringBuilder stringBuilder = new StringBuilder()
        if ("Score" != displayName && "Activity_Score" != displayName && "Outcome" != displayName && "PubChem activity score" != displayName) {
            stringBuilder.append(displayName ? displayName + " : " : '')

            stringBuilder.append(qualifier ?: '').append(value ?: '').append(" ").append(responseUnit ?: "").append(" ");
            if (testConcentration) {
                stringBuilder.append("  Test Concentration:").append(testConcentration).append(" ").append(testConcentrationUnit)
            }
        }
        return stringBuilder.toString()

    }

    @JsonProperty("displayName")
    public String getPubChemDisplayName() {
        return pubChemDisplayName;
    }

    @JsonProperty("displayName")
    public void setPubChemDisplayName(String pubChemDisplayName) {
        this.pubChemDisplayName = pubChemDisplayName;
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
