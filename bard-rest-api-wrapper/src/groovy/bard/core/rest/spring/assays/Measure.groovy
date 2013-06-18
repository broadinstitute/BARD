package bard.core.rest.spring.assays

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Measure extends JsonUtil {

    long id;
    @JsonProperty("name")
    String name;
    @JsonProperty("comps")
    List<Annotation> comps = new ArrayList<Annotation>()

    @JsonIgnore
    Measure parent
    @JsonIgnore
    List<Measure> children = new ArrayList<Measure>()
    @JsonIgnore
    List<Context> relatedContexts = new ArrayList<Context>()
}
