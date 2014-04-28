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

package bard.core.rest.spring.compounds

import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import bard.core.rest.spring.assays.Assay

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompoundSummary extends JsonUtil {

    @JsonProperty("ntest")
    private int ntest;

    @JsonProperty("testedExptdata")
    private List<Activity> testedExptdata = new ArrayList<Activity>();

    @JsonProperty("testedAssays")
    private List<Assay> testedAssays = new ArrayList<Assay>();


    @JsonProperty("nhit")
    private int nhit;

    @JsonProperty("hitAssays")
    private List<Assay> hitAssays = new ArrayList<Assay>();

    @JsonProperty("hitExptdata")
    private List<Activity> hitExptdata = new ArrayList<Activity>();



    @JsonProperty("testedExptdata")
    public List<Activity> getTestedExptdata() {
        return testedExptdata;
    }

    @JsonProperty("testedExptdata")
    public void setTestedExptdata(List<Activity> testedExptdata) {
        this.testedExptdata = testedExptdata;
    }

    @JsonProperty("testedAssays")
    public List<Assay> getTestedAssays() {
        return testedAssays;
    }

    @JsonProperty("testedAssays")
    public void setTestedAssays(List<Assay> testedAssays) {
        this.testedAssays = testedAssays;
    }

    @JsonProperty("nhit")
    public int getNhit() {
        return nhit;
    }

    @JsonProperty("nhit")
    public void setNhit(int nhit) {
        this.nhit = nhit;
    }

    @JsonProperty("hitAssays")
    public List<Assay> getHitAssays() {
        return hitAssays;
    }

    @JsonProperty("hitAssays")
    public void setHitAssays(List<Assay> hitAssays) {
        this.hitAssays = hitAssays;
    }

    @JsonProperty("ntest")
    public int getNtest() {
        return ntest;
    }

    @JsonProperty("ntest")
    public void setNtest(int ntest) {
        this.ntest = ntest;
    }

    @JsonProperty("hitExptdata")
    public List<Activity> getHitExptdata() {
        return hitExptdata;
    }

    @JsonProperty("hitExptdata")
    public void setHitExptdata(List<Activity> hitExptdata) {
        this.hitExptdata = hitExptdata;
    }

//    CompoundBioActivity buildBioActivitySummary(){
//        CompoundBioActivity compoundBioActivity = new CompoundBioActivity()
//        compoundBioActivity.numberActive = this.nhit
//        compoundBioActivity.numberTested=this.ntest
//
//
//
//    }

}
