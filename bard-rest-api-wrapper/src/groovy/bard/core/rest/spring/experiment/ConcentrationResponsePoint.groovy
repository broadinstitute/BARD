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

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.util.JsonUtil
import bard.core.util.ExperimentalValueUtil
import bard.core.util.ExperimentalValueUnitUtil
import bard.core.util.ExperimentalValueTypeUtil

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/9/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConcentrationResponsePoint extends JsonUtil {
    @JsonProperty("childElements")
    private List<ActivityData> childElements = new ArrayList<ActivityData>();

    @JsonProperty("testConc")
    private Double testConcentration;
    @JsonProperty("value")
    private String value;


    @JsonProperty("testConc")
    public Double getTestConcentration() {
        return testConcentration;
    }

    @JsonProperty("testConc")
    public void setTestConcentration(Double testConcentration) {
        this.testConcentration = testConcentration;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("childElements")
    public List<ActivityData> getChildElements() {
        return childElements;
    }

    @JsonProperty("childElements")
    public void setChildElements(List<ActivityData> childElements) {
        this.childElements = childElements;
    }

    public String toDisplay(String testConcentrationUnit) {
        String responseValue = this.value
        Double concentrationValue = this.testConcentration
        String responseString = ""
        String concentrationString = ""
        if (responseValue) {
            ExperimentalValueUtil resp = new ExperimentalValueUtil(new Double(responseValue), false)
            responseString = resp.toString()
        }
        if (concentrationValue) {
            ExperimentalValueUtil experimentalValueUtil =
                new ExperimentalValueUtil(concentrationValue,
                        ExperimentalValueUnitUtil.getByValue(testConcentrationUnit),
                        ExperimentalValueTypeUtil.numeric)
            concentrationString = "@ " + experimentalValueUtil.toString()
        }
        return "${responseString} ${concentrationString}"
    }

    public String displayActivity() {
        String responseValue = this.value
        if (responseValue) {
            if (responseValue.isNumber()) {
                ExperimentalValueUtil response = new ExperimentalValueUtil(new Double(responseValue), false)
                return response.toString()
            }
        }

        return ""
    }

    public String displayConcentration(String testConcentrationUnit) {
        Double concentrationValue = this.testConcentration
        if (concentrationValue) {
            ExperimentalValueUtil experimentalValueUtil =
                new ExperimentalValueUtil(concentrationValue,
                        ExperimentalValueUnitUtil.getByValue(testConcentrationUnit),
                        ExperimentalValueTypeUtil.numeric)
            return experimentalValueUtil.toString()
        }
        return ""
    }
}
