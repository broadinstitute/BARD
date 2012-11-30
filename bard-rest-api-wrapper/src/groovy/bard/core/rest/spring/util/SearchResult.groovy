package bard.core.rest.spring.util

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import com.fasterxml.jackson.annotation.*
import bard.core.rest.spring.util.MetaData
import bard.core.rest.spring.util.Facet
import bard.core.Value

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult {

    private Map<String, Long> etags =[:]

    @JsonProperty("metaData")
    private MetaData metaData;
    @JsonProperty("etag")
    private String etag;
    @JsonProperty("link")
    private String link;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    @JsonProperty("metaData")
    public MetaData getMetaData() {
        return metaData;
    }

    @JsonProperty("metaData")
    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    @JsonProperty("etag")
    public String getEtag() {
        if(!etag){
            return etags.keySet()?.iterator()?.next()
        }
        return etag;
    }

    @JsonProperty("etag")
    public void setEtag(String etag) {
        this.etag = etag;
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
    @JsonIgnore
    public int getNumberOfHits(){
        return this.metaData?.nhit?:0
    }
    @JsonIgnore
    public List<Facet> getFacets() {
        return this.metaData?.facets?:[]
    }
    public Collection<Value> getFacetsToValues(){
        return getMetaData()?.facetsToValues()
    }
    Map<String, Long> getEtags() {
        return etags
    }

    void setEtags(Map<String, Long> etags) {
        this.etags = etags
    }

}