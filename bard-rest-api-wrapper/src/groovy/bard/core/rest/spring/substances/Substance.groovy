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

package bard.core.rest.spring.substances;


import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Substance extends JsonUtil {
    @JsonProperty("sid")
    private long sid;
    @JsonProperty("cid")
    private long cid;
    @JsonProperty("depRegId")
    private String depRegId;
    @JsonProperty("sourceName")
    private String sourceName;
    @JsonProperty("url")
    private String url;
    @JsonProperty("patentIds")
    private List<String> patentIds;
    @JsonProperty("smiles")
    private String smiles;
    @JsonProperty("deposited")
    private String deposited;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("resourcePath")
    private String resourcePath;


    public long getId() {
        return this.sid
    }

    @JsonProperty("sid")
    public long getSid() {
        return sid;
    }

    @JsonProperty("sid")
    public void setSid(long sid) {
        this.sid = sid;
    }

    @JsonProperty("cid")
    public long getCid() {
        return cid;
    }

    @JsonProperty("cid")
    public void setCid(long cid) {
        this.cid = cid;
    }

    @JsonProperty("depRegId")
    public String getDepRegId() {
        return depRegId;
    }

    @JsonProperty("depRegId")
    public void setDepRegId(String depRegId) {
        this.depRegId = depRegId;
    }

    @JsonProperty("sourceName")
    public String getSourceName() {
        return sourceName;
    }

    @JsonProperty("sourceName")
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("patentIds")
    public List<String> getPatentIds() {
        return patentIds;
    }

    @JsonProperty("patentIds")
    public void setPatentIds(List<String> patentIds) {
        this.patentIds = patentIds;
    }

    @JsonProperty("smiles")
    public String getSmiles() {
        return smiles;
    }

    @JsonProperty("smiles")
    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    @JsonProperty("deposited")
    public String getDeposited() {
        return deposited;
    }

    @JsonProperty("deposited")
    public void setDeposited(String deposited) {
        this.deposited = deposited;
    }

    @JsonProperty("updated")
    public String getUpdated() {
        return updated;
    }

    @JsonProperty("updated")
    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
}
