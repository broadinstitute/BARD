package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: ben
 * Date: 6/9/13
 * Time: 11:15 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class MinimumAnnotation {
    @JsonProperty("assay footprint")
    private String assayFootprint
    @JsonProperty("detection method type")
    private String detectionMethodType
    @JsonProperty("detection instrument name")
    private String detectionInstrumentName
    @JsonProperty("assay format")
    private String assayFormat
    @JsonProperty("assay type")
    private String assayType

    @JsonProperty("assay footprint")
    public String getAssayFootprint() {
        return assayFootprint
    }
    @JsonProperty("assay footprint")
    public void setAssayFootprint(String assayFootprint) {
        this.assayFootprint = assayFootprint
    }

    @JsonProperty("detection method type")
    public String getDetectionMethodType() {
        return detectionMethodType
    }
    @JsonProperty("detection method type")
    public void setDetectionMethodType(String detectionMethodType) {
        this.detectionMethodType = detectionMethodType
    }

    @JsonProperty("detection instrument name")
    public String getDetectionInstrumentName() {
        return detectionInstrumentName
    }
    @JsonProperty("detection instrument name")
    public void setDetectionInstrumentName(String detectionInstrumentName) {
        this.detectionInstrumentName = detectionInstrumentName
    }

    @JsonProperty("assay format")
    public String getAssayFormat() {
        return assayFormat
    }
    @JsonProperty("assay format")
    public void setAssayFormat(String assayFormat) {
        this.assayFormat = assayFormat
    }

    @JsonProperty("assay type")
    public String getAssayType() {
        return assayType
    }
    @JsonProperty("assay type")
    public void setAssayType(String assayType) {
        this.assayType = assayType
    }

}
