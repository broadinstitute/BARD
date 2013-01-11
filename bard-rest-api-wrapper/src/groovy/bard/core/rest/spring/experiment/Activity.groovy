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


    @JsonIgnore
    private ResultData resultData
    @JsonProperty("resultJson")
    private String resultJson

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
    @JsonProperty("readouts")
    private List<Readout> readouts = new ArrayList<Readout>();
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("resultJson")
    public String getResultJson() {
        return resultJson
    }
    @JsonProperty("resultJson")
    public void setResultJson(String resultJson) {
        this.resultJson = resultJson
    }
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

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }
    /**
     * Convert the ResultJson string to a data object
     * Since this is expensive we want to do it only once
     * @return
     */
    public ResultData getResultData() {
        if(this.resultData != null){
            return this.resultData
        }
        if(StringUtils.isBlank(this.resultJson)){
           return null
        }
       this.resultData = objectMapper.readValue(this.resultJson, ResultData.class)

        //convert the string to data object
        return resultData
    }
    public boolean hasConcentrationSeries(){
        return getResultData()?.hasConcentrationResponseSeries()
    }

}
