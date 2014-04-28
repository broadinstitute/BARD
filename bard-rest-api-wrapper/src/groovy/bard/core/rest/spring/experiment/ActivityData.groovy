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

package bard.core.rest.spring.experiment

import bard.core.rest.spring.util.JsonUtil
import bard.rest.api.wrapper.Dummy
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.Node

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActivityData extends JsonUtil {
    @JsonProperty("displayName")
    private String displayName;
    @JsonProperty("dictElemId")
    private long dictElemId;
    @JsonProperty("value")
    private String value;
    @JsonProperty("responseUnit")
    private String responseUnit;
    @JsonProperty("testConc")
    private Double testConcentration;
    @JsonProperty("testConcUnit")
    private String testConcentrationUnit;
    @JsonProperty("qualifierValue")
    private String qualifier;
    @JsonIgnore
    Dummy dummy = new Dummy()

    public String getDictionaryLabel() {
        if (dictElemId) {
            final Node dictionaryElement = dummy?.dictionaryRestService?.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.label
            }
        }
        return displayName

    }

    public String getDictionaryDescription() {
        if (dictElemId) {
            final Node dictionaryElement = dummy.dictionaryRestService.findDictionaryElementById(this.dictElemId)
            if (dictionaryElement) {
                return dictionaryElement.description
            }
        }
        return null

    }

    public String toDisplay() {
        //look up the element in the CAP
        String displayName = getDisplayName()
        if (displayName) {
            displayName = displayName
        }

        final StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append(displayName ? displayName + " : " : '')

        stringBuilder.append(qualifier ?: '').append(value ?: '').append(" ").append(responseUnit ?: "").append(" ");
        if (testConcentration) {
            stringBuilder.append("  Test Concentration:").append(testConcentration).append(" ").append(testConcentrationUnit)
        }
        return stringBuilder.toString()

    }

    @JsonProperty("displayName")
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty("displayName")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty("dictElemId")
    public long getDictElemId() {
        return dictElemId;
    }

    @JsonProperty("dictElemId")
    public void setDictElemId(long dictElemId) {
        this.dictElemId = dictElemId;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("responseUnit")
    public String getResponseUnit() {
        return responseUnit;
    }

    @JsonProperty("responseUnit")
    public void setResponseUnit(String responseUnit) {
        this.responseUnit = responseUnit;
    }

    Double getTestConcentration() {
        return testConcentration
    }

    void setTestConcentration(Double testConcentration) {
        this.testConcentration = testConcentration
    }

    String getTestConcentrationUnit() {
        return testConcentrationUnit
    }

    void setTestConcentrationUnit(String testConcentrationUnit) {
        this.testConcentrationUnit = testConcentrationUnit
    }

    String getQualifier() {
        return qualifier
    }

    void setQualifier(String qualifier) {
        this.qualifier = qualifier
    }
}
