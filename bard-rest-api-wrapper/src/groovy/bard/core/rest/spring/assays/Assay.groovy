package bard.core.rest.spring.assays

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Assay extends AbstractAssay {
    @JsonProperty("documents")
    List<String> documentIds = new ArrayList<String>();
    @JsonProperty("targets")
    List<String> targetIds = new ArrayList<String>();
    @JsonProperty("experiments")
    List<String> experimentIds = new ArrayList<String>();
    @JsonProperty("projects")
    List<String> projectIds = new ArrayList<String>();

}
