package bard.core.rest.spring.compounds;


import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompoundAnnotations extends JsonUtil {

    @JsonProperty("anno_key")
    private List<String> anno_key = new ArrayList<String>();
    @JsonProperty("anno_val")
    private List<String> anno_val = new ArrayList<String>();

    @JsonProperty("anno_key")
    public List<String> getAnno_key() {
        return anno_key;
    }

    @JsonProperty("anno_key")
    public void setAnno_key(List<String> anno_key) {
        this.anno_key = anno_key;
    }

    @JsonProperty("anno_val")
    public List<String> getAnno_val() {
        return anno_val;
    }

    @JsonProperty("anno_val")
    public void setAnno_val(List<String> anno_val) {
        this.anno_val = anno_val;
    }
}
