package bard.core.rest.spring.assays

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 *  TODO: Context is the same as Measure and Doc. Unify them
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class  Doc extends JsonUtil {

    @JsonProperty("id")
    long id;
    @JsonProperty("name")
    String name;
    @JsonProperty("comps")
    List<Annotation> comps = new ArrayList<Annotation>();

}
