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

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 1/9/13
 * Time: 7:02 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurveFitParameters extends JsonUtil {

    @JsonProperty("s0")
    private Double s0;
    @JsonProperty("sInf")
    private Double sInf;
    @JsonProperty("hillCoef")
    private Double hillCoef;

    //TODO: this is dynamic, for now lets assume it is not
    //we should get the list of possible values
    @JsonProperty("logEc50")
    private Double logEc50;

    @JsonProperty("s0")
    public Double getS0() {
        return s0;
    }

    @JsonProperty("s0")
    public void setS0(Double s0) {
        this.s0 = s0;
    }

    @JsonProperty("sInf")
    public Double getSInf() {
        return sInf;
    }

    @JsonProperty("sInf")
    public void setSInf(Double sInf) {
        this.sInf = sInf;
    }

    @JsonProperty("hillCoef")
    public Double getHillCoef() {
        return hillCoef;
    }

    @JsonProperty("hillCoef")
    public void setHillCoef(Double hillCoef) {
        this.hillCoef = hillCoef;
    }

    @JsonProperty("logEc50")
    public Double getLogEc50() {
        return logEc50;
    }

    @JsonProperty("logEc50")
    public void setLogEc50(Double logEc50) {
        this.logEc50 = logEc50;
    }
}
