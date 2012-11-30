package bard.core.rest.spring.util

import bard.core.Value
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

import javax.annotation.Generated

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/23/12
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
public class MetaData {

    @JsonProperty("nhit")
    private Integer nhit;
    @JsonProperty("facets")
    private List<Facet> facets = new ArrayList<Facet>();
    @JsonProperty("queryTime")
    private Integer queryTime;
    @JsonProperty("elapsedTime")
    private Integer elapsedTime;
    @JsonProperty("matchingFields")
    private Object matchingFields;
    @JsonProperty("scores")
    private Object scores;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("nhit")
    public Integer getNhit() {
        return nhit;
    }

    @JsonProperty("nhit")
    public void setNhit(Integer nhit) {
        this.nhit = nhit;
    }

    @JsonProperty("facets")
    public List<Facet> getFacets() {
        return facets;
    }

    @JsonProperty("facets")
    public void setFacets(List<Facet> facets) {
        this.facets = facets;
    }

    @JsonProperty("queryTime")
    public Integer getQueryTime() {
        return queryTime;
    }

    @JsonProperty("queryTime")
    public void setQueryTime(Integer queryTime) {
        this.queryTime = queryTime;
    }

    @JsonProperty("elapsedTime")
    public Integer getElapsedTime() {
        return elapsedTime;
    }

    @JsonProperty("elapsedTime")
    public void setElapsedTime(Integer elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @JsonProperty("matchingFields")
    public Object getMatchingFields() {
        return matchingFields;
    }

    @JsonProperty("matchingFields")
    public void setMatchingFields(Object matchingFields) {
        this.matchingFields = matchingFields;
    }

    @JsonProperty("scores")
    public Object getScores() {
        return scores;
    }

    @JsonProperty("scores")
    public void setScores(Object scores) {
        this.scores = scores;
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

    Collection<Value> facetsToValues() {
        Collection<Value> values = []
        for (Facet facet : this.facets) {
            Value value = facet.toValue()
            if (value) {
                values.add(value)
            }
        }
        return values;
    }

}
