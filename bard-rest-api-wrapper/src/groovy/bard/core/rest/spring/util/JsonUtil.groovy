package bard.core.rest.spring.util

import org.apache.commons.lang3.builder.ToStringBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.EqualsBuilder
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 12/1/12
 * Time: 11:37 AM
 * To change this template use File | Settings | File Templates.
 */
class JsonUtil {
    Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
