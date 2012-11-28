package bard.core.rest.spring.util

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

import javax.annotation.Generated

import com.fasterxml.jackson.annotation.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder([
"accessed",
"etag_id",
"count",
"status",
"created",
"description",
"name",
"type",
"url",
"modified"
])
public class ETag {

    @JsonProperty("accessed")
    private long accessed;
    @JsonProperty("etag_id")
    private String etag_id;
    @JsonProperty("count")
    private long count;
    @JsonProperty("status")
    private long status;
    @JsonProperty("created")
    private long created;
    @JsonProperty("description")
    private String description;
    @JsonProperty("name")
    private String name;
    @JsonProperty("type")
    private String type;
    @JsonProperty("url")
    private String url;
    @JsonProperty("modified")
    private long modified;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("accessed")
    public long getAccessed() {
        return accessed;
    }

    @JsonProperty("accessed")
    public void setAccessed(long accessed) {
        this.accessed = accessed;
    }

    @JsonProperty("etag_id")
    public String getEtag_id() {
        return etag_id;
    }

    @JsonProperty("etag_id")
    public void setEtag_id(String etag_id) {
        this.etag_id = etag_id;
    }

    @JsonProperty("count")
    public long getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(long count) {
        this.count = count;
    }

    @JsonProperty("status")
    public long getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(long status) {
        this.status = status;
    }

    @JsonProperty("created")
    public long getCreated() {
        return created;
    }

    @JsonProperty("created")
    public void setCreated(long created) {
        this.created = created;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("modified")
    public long getModified() {
        return modified;
    }

    @JsonProperty("modified")
    public void setModified(long modified) {
        this.modified = modified;
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