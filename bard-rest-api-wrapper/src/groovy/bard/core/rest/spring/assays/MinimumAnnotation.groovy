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
    String assayFootprint
    @JsonProperty("detection method type")
    String detectionMethodType
    @JsonProperty("detection instrument name")
    String detectionInstrumentName
    @JsonProperty("assay format")
    String assayFormat
    @JsonProperty("assay type")
    String assayType
    @JsonProperty("cultured cell name")
    String culturedCellName
    @JsonProperty("species name")
    String speciesName
    @JsonProperty("excitation wavelength")
    String excitationWavelength
    @JsonProperty("emission wavelength")
    String emissionWavelength
    @JsonProperty("absorbance wavelength")
    String absorbanceWavelength
    @JsonProperty("measurement wavelength")
    String measurementWavelength

}
