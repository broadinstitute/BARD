package bard.core.rest.spring.util

import bard.core.DataSource
import bard.core.IntValue
import bard.core.Value
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/23/12
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Facet {

    @JsonProperty("facetName")
    private String facetName;
    @JsonProperty("counts")
    private Counts counts;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("facetName")
    public String getFacetName() {
        return facetName;
    }

    @JsonProperty("facetName")
    public void setFacetName(String facetName) {
        this.facetName = facetName;
    }

    @JsonProperty("counts")
    public Counts getCounts() {
        return counts;
    }

    @JsonProperty("counts")
    public void setCounts(Counts counts) {
        this.counts = counts;
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

    public Value toValue() {
        final Value facet = new Value(DataSource.DEFAULT, this.facetName);
        final Counts counts = this.getCounts()
        final Map<String, Object> additionalProperties = counts.getAdditionalProperties()
        boolean hasAtleastOneValue = 0//We will ignore empty facets
        for (String key : additionalProperties.keySet()) {
            final Object object = additionalProperties.get(key)
            if (object != null) {
                new IntValue(facet, key, (Integer) object);
                hasAtleastOneValue = true
            }
        }
        if (hasAtleastOneValue) {
            return facet
        }
        return null
    }
}
