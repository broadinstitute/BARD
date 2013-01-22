package bard.core.rest.spring.assays

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * TODO: Context is the same as Measure and Doc. Unify them
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Measure extends JsonUtil {

    @JsonProperty("id")
    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("comps")
    private List<Comp> comps = new ArrayList<Comp>();

    @JsonProperty("id")
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
        this.id = id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("comps")
    public List<Comp> getComps() {
        return comps;
    }

    @JsonProperty("comps")
    public void setComps(List<Comp> comps) {
        this.comps = comps;
    }

}
