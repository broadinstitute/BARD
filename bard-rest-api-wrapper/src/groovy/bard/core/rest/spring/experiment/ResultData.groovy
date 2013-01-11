package bard.core.rest.spring.experiment

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultData extends JsonUtil {

    @JsonProperty("responseClass")
    private String responseClass;
    @JsonProperty("bardExptId")
    private long bardExptId;
    @JsonProperty("sid")
    private long sid;
    @JsonProperty("cid")
    private long cid;
    @JsonProperty("priorityElements")
    private List<PriorityElement> priorityElements = new ArrayList<PriorityElement>();
    @JsonProperty("rootElements")
    private List<RootElement> rootElements = new ArrayList<RootElement>();

    @JsonProperty("responseClass")
    public String getResponseClass() {
        return responseClass;
    }

    @JsonProperty("responseClass")
    public void setResponseClass(String responseClass) {
        this.responseClass = responseClass;
    }

    @JsonProperty("bardExptId")
    public long getBardExptId() {
        return bardExptId;
    }

    @JsonProperty("bardExptId")
    public void setBardExptId(long bardExptId) {
        this.bardExptId = bardExptId;
    }

    @JsonProperty("sid")
    public long getSid() {
        return sid;
    }

    @JsonProperty("sid")
    public void setSid(long sid) {
        this.sid = sid;
    }

    @JsonProperty("cid")
    public long getCid() {
        return cid;
    }

    @JsonProperty("cid")
    public void setCid(long cid) {
        this.cid = cid;
    }

    @JsonProperty("priorityElements")
    public List<PriorityElement> getPriorityElements() {
        return priorityElements;
    }

    @JsonProperty("priorityElements")
    public void setPriorityElements(List<PriorityElement> priorityElements) {
        this.priorityElements = priorityElements;
    }

    @JsonProperty("rootElements")
    public List<RootElement> getRootElements() {
        return rootElements;
    }

    @JsonProperty("rootElements")
    public void setRootElements(List<RootElement> rootElements) {
        this.rootElements = rootElements;
    }

    public ResponseClassEnum getResponseClassEnum() {
       return ResponseClassEnum.toEnum(this.responseClass)
    }
    public boolean hasConcentrationResponseSeries(){
        for(PriorityElement priorityElement : this.priorityElements){
            if(priorityElement.concentrationResponseSeries){
                return true;
            }
        }
        return false
    }
}
enum ResponseClassEnum {
    SP("Single Point"),
    CR_SER("Concentration Response"),
    CR_NO_SER("Probable Concentration Response but no Series"),
    UNCLASS("Unclassified"),
    MULTCONC("Multiple Test Concentrations")

    final String description

    ResponseClassEnum(String description) {
        this.description = description
    }

    static ResponseClassEnum toEnum(String representation){
       return ResponseClassEnum.valueOf(representation)
    }


}

