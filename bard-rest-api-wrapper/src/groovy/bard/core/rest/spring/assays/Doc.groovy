package bard.core.rest.spring.assays

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a document annotation.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Doc extends JsonUtil {

    @JsonProperty("id")
    long id;
    @JsonProperty("name")
    String name;
    @JsonProperty("comps")
    List<Annotation> comps = new ArrayList<Annotation>();

    @JsonIgnore
    public String getDisplayString() {
        if (!comps.isEmpty()) {
            return comps.first().display
        }
        return ""
    }

    @JsonIgnore
    public String getUrlValue() {
        if (!comps.isEmpty()) {
            return comps.first().value
        }
        return ""
    }
}
