package bard.core.rest.spring.util

import bard.core.Value
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResult extends JsonUtil {

    Map<String, Long> etags = [:]

    @JsonProperty("metaData")
    private MetaData metaData;
    @JsonProperty("etag")
    private String etag;
    @JsonProperty("link")
    private String link;


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
        if (!etag) {
            if (etags) {
                return etags.keySet().iterator().next()
            }
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

    @JsonIgnore
    public int getNumberOfHits() {
        return this.metaData?.nhit ?: 0
    }

    @JsonIgnore
    public List<Facet> getFacets() {
        return this.metaData?.facets ?: []
    }

    public Collection<Value> getFacetsToValues() {
        final MetaData metaData = getMetaData()
        if (metaData) {
            return metaData.facetsToValues()
        }
        return []
    }

    Map<String, Long> getEtags() {
        return etags
    }

    void setEtags(Map<String, Long> etags) {
        this.etags = etags
    }
    /**
     * Use the id of each response to get the score
     * @param key
     * @return
     */
    public Double getScore(String key) {
        return this.metaData.getScore(key)
    }
    /**
     * Use the id of each response to get the matching field
     * @param key
     * @return
     */
    public NameDescription getMatchingField(String key) {
        this.metaData.getMatchingField(key)
    }
}