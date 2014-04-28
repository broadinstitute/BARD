/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
