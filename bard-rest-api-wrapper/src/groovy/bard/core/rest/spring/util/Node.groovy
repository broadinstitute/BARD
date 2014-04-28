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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo



@JsonInclude(JsonInclude.Include.NON_NULL)
public class Node extends JsonUtil implements Serializable {
    @JsonProperty("abbreviation")
    private String abbreviation;
    @JsonProperty("description")
    private String description;
    @JsonProperty("elementId")
    private long elementId;
    @JsonProperty("externalUrl")
    private String externalUrl;
    @JsonProperty("label")
    private String label;
    @JsonProperty("elementStatus")
    private String elementStatus;
    @JsonProperty("synonyms")
    private String synonyms;
    @JsonProperty("link")
    private List link;
    @JsonProperty("readyForExtraction")
    private String readyForExtraction;

    @JsonProperty("abbreviation")
    public String getAbbreviation() {
        return abbreviation;
    }

    @JsonProperty("abbreviation")
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("elementId")
    public long getElementId() {
        return elementId;
    }

    @JsonProperty("elementId")
    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    @JsonProperty("externalUrl")
    public String getExternalUrl() {
        return externalUrl;
    }

    @JsonProperty("externalUrl")
    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("elementStatus")
    public String getElementStatus() {
        return elementStatus;
    }

    @JsonProperty("elementStatus")
    public void setElementStatus(String elementStatus) {
        this.elementStatus = elementStatus;
    }

    @JsonProperty("synonyms")
    public String getSynonyms() {
        return synonyms;
    }

    @JsonProperty("synonyms")
    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    @JsonProperty("link")
    public List getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(List link) {
        this.link = link;
    }

    @JsonProperty("readyForExtraction")
    public String getReadyForExtraction() {
        return readyForExtraction;
    }

    @JsonProperty("readyForExtraction")
    public void setReadyForExtraction(String readyForExtraction) {
        this.readyForExtraction = readyForExtraction;
    }
}

