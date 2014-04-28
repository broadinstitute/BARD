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

package bard.core;

import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.List;
import java.util.ArrayList;

@Deprecated
public class HillCurveValue extends Value {
    private static final long serialVersionUID = 0x8952d92d94f21b00l;
    private static final double ln10 = 2.30258509299404568401;

    // fitted parameters
    protected Double s0, sInf;
    protected Double slope; // ac50
    protected Double coef; // hill coef
    protected String concentrationUnits;

    // data
    List<Double> conc = new ArrayList<Double>();
    List<Double> response = new ArrayList<Double>();

    protected HillCurveValue() {
    }

    public HillCurveValue(Value parent) {
        super(parent);
    }

    public HillCurveValue(Value parent, String id) {
        super(parent, id);
    }

    public HillCurveValue(DataSource source) {
        this(source, null);
    }

    public HillCurveValue(DataSource source, String id) {
        super(source, id);
    }

    @Override
    public Double getValue() {
        return slope;
    }

    public String getConcentrationUnits() {
        return this.concentrationUnits;
    }

    public void setConcentrationUnits(String concentrationUnits) {
        this.concentrationUnits = concentrationUnits;
    }

    public Double getSlope() {
        return slope;
    }

    public void setSlope(Double slope) {
        this.slope = slope;
    }

    public Double getS0() {
        return s0;
    }

    public void setS0(Double s0) {
        this.s0 = s0;
    }

    public Double getSinf() {
        return sInf;
    }

    public void setSinf(Double sInf) {
        this.sInf = sInf;
    }

    public Double getCoef() {
        return coef;
    }

    public void setCoef(Double coef) {
        this.coef = coef;
    }

    public void add(Double conc, Double response) {
        this.conc.add(conc);
        this.response.add(response);
    }

    public int size() {
        return conc.size();
    }

    public Double[] getConc() {
        return conc.toArray(new Double[0]);
    }

    public Double[] getResponse() {
        return response.toArray(new Double[0]);
    }

    public Double evaluate(double conc) {
        if (slope == null || coef == null) {
            return null;
        }

        double z0 = s0 != null ? s0 : 0.;
        double zinf = sInf != null ? sInf : 100.;
        double logslope = Math.log10(slope);
        double logconc = Math.log10(conc);

        return z0 + ((zinf - z0)
                / (1. + Math.exp(ln10 * coef *
                (logslope - logconc))));
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        HillCurveValue that = (HillCurveValue) o;
        return new CompareToBuilder()
                .append(this.id, that.id)
                .append(this.getValue(), that.getValue())
                .toComparison();
    }
}
