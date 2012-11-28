package bard.core.rest.spring.compounds;


import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import com.fasterxml.jackson.annotation.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder([
"anno_key",
"anno_val"
])
public class CompoundAnnotations {

    @JsonProperty("anno_key")
    private List<String> anno_key = new ArrayList<String>();
    @JsonProperty("anno_val")
    private List<String> anno_val = new ArrayList<String>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
