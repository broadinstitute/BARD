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
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.StringUtils
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.annotation.JsonIgnore

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Activity extends JsonUtil {
    @JsonIgnore
    ObjectMapper objectMapper = new ObjectMapper()

    @JsonProperty("exptDataId")
    private String exptDataId;
    @JsonProperty("eid")
    private Long eid;
    @JsonProperty("cid")
    private Long cid;
    @JsonProperty("sid")
    private Long sid;
    @JsonProperty("bardExptId")
    private Long bardExptId;
    @JsonProperty("bardAssayId")
    private Long bardAssayId;
    @JsonProperty("capExptId")
    private Long capExptId;
    @JsonProperty("capAssayId")
    private Long capAssayId;
    @JsonProperty("capProjId")
    private List<Long> capProjId = new ArrayList<Long>();
    @JsonProperty("bardProjId")
    private List<Long> bardProjId = new ArrayList<Long>();
    @JsonProperty("runset")
    private String runset;
    @JsonProperty("outcome")
    private Long outcome;
    @JsonProperty("score")
    private Long score;
    @JsonProperty("classification")
    private Long classification;
    @JsonProperty("potency")
    private String potency;

    @JsonIgnore
    private ResultData resultData
    @JsonProperty("resultJson")
    private String resultJson

    @JsonProperty("readouts")
    private List<Readout> readouts = new ArrayList<Readout>();
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("exptDataId")
    public String getExptDataId() {
        return exptDataId;
    }

    @JsonProperty("exptDataId")
    public void setExptDataId(String exptDataId) {
        this.exptDataId = exptDataId;
    }

    @JsonProperty("eid")
    public Long getEid() {
        return eid;
    }

    @JsonProperty("eid")
    public void setEid(Long eid) {
        this.eid = eid;
    }

    @JsonProperty("cid")
    public Long getCid() {
        return cid;
    }

    @JsonProperty("cid")
    public void setCid(Long cid) {
        this.cid = cid;
    }

    @JsonProperty("sid")
    public Long getSid() {
        return sid;
    }

    @JsonProperty("sid")
    public void setSid(Long sid) {
        this.sid = sid;
    }

    @JsonProperty("bardExptId")
    public Long getBardExptId() {
        return bardExptId;
    }

    @JsonProperty("bardExptId")
    public void setBardExptId(Long bardExptId) {
        this.bardExptId = bardExptId;
    }

    @JsonProperty("bardAssayId")
    public Long getBardAssayId() {
        return bardAssayId;
    }

    @JsonProperty("bardAssayId")
    public void setBardAssayId(Long bardAssayId) {
        this.bardAssayId = bardAssayId;
    }

    @JsonProperty("capExptId")
    public Long getCapExptId() {
        return capExptId;
    }

    @JsonProperty("capExptId")
    public void setCapExptId(Long capExptId) {
        this.capExptId = capExptId;
    }

    @JsonProperty("capAssayId")
    public Long getCapAssayId() {
        return capAssayId;
    }

    @JsonProperty("capAssayId")
    public void setCapAssayId(Long capAssayId) {
        this.capAssayId = capAssayId;
    }

    @JsonProperty("capProjId")
    public List<Long> getCapProjId() {
        return capProjId;
    }

    @JsonProperty("capProjId")
    public void setCapProjId(List<Long> capProjId) {
        this.capProjId = capProjId;
    }

    @JsonProperty("bardProjId")
    public List<Long> getBardProjId() {
        return bardProjId;
    }

    @JsonProperty("bardProjId")
    public void setBardProjId(List<Long> bardProjId) {
        this.bardProjId = bardProjId;
    }

    @JsonProperty("runset")
    public String getRunset() {
        return runset;
    }

    @JsonProperty("runset")
    public void setRunset(String runset) {
        this.runset = runset;
    }

    @JsonProperty("outcome")
    public Long getOutcome() {
        return outcome;
    }

    @JsonProperty("outcome")
    public void setOutcome(Long outcome) {
        this.outcome = outcome;
    }

    @JsonProperty("score")
    public Long getScore() {
        return score;
    }

    @JsonProperty("score")
    public void setScore(Long score) {
        this.score = score;
    }

    @JsonProperty("classification")
    public Long getClassification() {
        return classification;
    }

    @JsonProperty("classification")
    public void setClassification(Long classification) {
        this.classification = classification;
    }

    @JsonProperty("potency")
    public String getPotency() {
        return potency;
    }

    @JsonProperty("potency")
    public void setPotency(String potency) {
        this.potency = potency;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @JsonProperty("resultJson")
    public String getResultJson() {
        return resultJson
    }

    @JsonProperty("resultJson")
    public void setResultJson(String resultJson) {
        this.resultJson = resultJson
    }


    @JsonProperty("readouts")
    @Deprecated
    public List<Readout> getReadouts() {
        return readouts;
    }

    @JsonProperty("readouts")
    @Deprecated
    public void setReadouts(List<Readout> readouts) {
        this.readouts = readouts;
    }
    /**
     * Convert the ResultJson string to a data object
     * Since this is expensive we want to do it only once
     * @return
     */
    public ResultData getResultData() {
        if (this.resultData != null) {
            return this.resultData
        }
        if (StringUtils.isBlank(this.resultJson)) {
            return null
        }
        this.resultData = objectMapper.readValue(this.resultJson, ResultData.class)

        //convert the string to data object
        return resultData
    }

    public boolean hasConcentrationSeries() {
        final ResultData resultData = getResultData()
        if (resultData) {
            return resultData.hasConcentrationResponseSeries()
        }
        return false
    }

}
