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

import bard.core.DataSource
import bard.core.HillCurveValue
import bard.core.Value
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Readout extends  JsonUtil{

    @JsonProperty("name")
    private String name;
    @JsonProperty("s0")
    private Double s0;
    @JsonProperty("sInf")
    private Double sInf;
    @JsonProperty("hill")
    private Double coef;
    @JsonProperty("ac50")
    private Double slope;
    @JsonProperty("cr")
    private List<List<Double>> cr = new ArrayList<List<Double>>();
    @JsonProperty("npoint")
    private Long numberOfPoints;
    @JsonProperty("concUnit")
    private String concentrationUnits;
    @JsonProperty("responseUnit")
    private String responseUnit;
    // data
    protected List<Double> conc = new ArrayList<Double>();
    protected List<Double> response = new ArrayList<Double>();

    public void addPoint(Double conc, Double response) {
        this.conc.add(conc);
        this.response.add(response);
    }

    public int size() { return conc.size(); }

    public Double[] getConc() {
        return conc.toArray(new Double[0]);
    }

    public List<Double> getConcAsList() {
        return conc
    }

    public List<Double> getResponseAsList() {
        return response;
    }

    public Double[] getResponse() {
        return response.toArray(new Double[0]);
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

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

    @JsonProperty("hill")
    public Double getCoef() {
        return coef;
    }

    @JsonProperty("hill")
    public void setCoef(Double coef) {
        this.coef = coef;
    }

    @JsonProperty("ac50")
    public Double getSlope() {
        return slope;
    }

    @JsonProperty("ac50")
    public void setAc50(Double slope) {
        this.slope = slope;
    }

    @JsonProperty("cr")
    public List<List<Double>> getCr() {
        return cr;
    }

    @JsonProperty("cr")
    public void setCr(List<List<Double>> cr) {
        addCrcs(cr)
        this.cr = cr;
    }
    //We extract the x and y coordinates here
    public void addCrcs(List<List<Double>> cr) {
        for (List<Double> point : cr) {
            //first value is concentration
            //second value is activity
            if (!point.isEmpty()) {
                this.conc.add(point.get(0))
                if (point.size() == 2) {
                    this.response.add(point.get(1))
                } else {
                    this.response.add(0)
                }
            }
        }
        this.cr = cr;
    }

    @JsonProperty("npoint")
    public Long getNumberOfPoints() {
        return this.numberOfPoints;
    }

    @JsonProperty("npoint")
    public void setNumberOfPoints(Long numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    @JsonProperty("concUnit")
    public String getConcentrationUnits() {
        return concentrationUnits;
    }

    @JsonProperty("concUnit")
    public void setConcentrationUnits(String concentrationUnits) {
        this.concentrationUnits = concentrationUnits;
    }

    @JsonProperty("responseUnit")
    public String getResponseUnit() {
        return responseUnit;
    }

    @JsonProperty("responseUnit")
    public void setResponseUnit(String responseUnit) {
        this.responseUnit = responseUnit;
    }

    HillCurveValue toHillCurveValue() {
        final Value parent = new Value(DataSource.DEFAULT)
        HillCurveValue hillCurveValue = new HillCurveValue(parent, this.name)
        hillCurveValue.coef = this.coef
        hillCurveValue.slope = this.slope
        hillCurveValue.conc = this.conc?:[]
        hillCurveValue.response = this.response?:[]
        hillCurveValue.s0 = this.s0
        hillCurveValue.sinf = this.sInf
        hillCurveValue.concentrationUnits = this.concentrationUnits
        return hillCurveValue
    }
}

