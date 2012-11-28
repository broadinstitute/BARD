package bard.core.rest.spring.util

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

import javax.annotation.Generated

import com.fasterxml.jackson.annotation.*

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder([
"title",
"doi",
"abs",
"pubmedId",
"resourcePath"
])
public class Document {

    @JsonProperty("title")
    private String title;
    @JsonProperty("doi")
    private String doi;
    @JsonProperty("abs")
    private String abs;
    @JsonProperty("pubmedId")
    private long pubmedId;
    @JsonProperty("resourcePath")
    private String resourcePath;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("doi")
    public String getDoi() {
        return doi;
    }

    @JsonProperty("doi")
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @JsonProperty("abs")
    public String getAbs() {
        return abs;
    }

    @JsonProperty("abs")
    public void setAbs(String abs) {
        this.abs = abs;
    }

    @JsonProperty("pubmedId")
    public long getPubmedId() {
        return pubmedId;
    }

    @JsonProperty("pubmedId")
    public void setPubmedId(long pubmedId) {
        this.pubmedId = pubmedId;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
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
