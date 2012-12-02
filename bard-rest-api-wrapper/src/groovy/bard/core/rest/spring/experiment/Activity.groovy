package bard.core.rest.spring.experiment

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Activity extends JsonUtil {

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
    public List<Readout> getReadouts() {
        return readouts;
    }

    @JsonProperty("readouts")
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
}
