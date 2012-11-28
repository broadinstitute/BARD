package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperimentData {

    @JsonProperty("collection")
    private List<Activity> activities = new ArrayList<Activity>();
    @JsonProperty("link")
    private String link;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("collection")
    public List<Activity> getActivities() {
        return activities;
    }

    @JsonProperty("collection")
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
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
