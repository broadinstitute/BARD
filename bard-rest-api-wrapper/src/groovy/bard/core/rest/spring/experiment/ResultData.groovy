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

    public boolean hasConcentrationResponseSeries() {
        return getResponseClassEnum() == ResponseClassEnum.CR_SER
    }

    public boolean hasPriorityElements() {
        if (this.priorityElements) {
            return true
        }
        return false
    }

    public String getOutcome() {
        for (RootElement rootElement : this.rootElements) {
            if (rootElement.hasOutcome()) {
                return rootElement.value
            }
        }
        return ""
    }

    public boolean isMapped() {
        return getResponseClassEnum().isMapped()
    }

    boolean hasPlot() {
        return this.responseClassEnum == ResponseClassEnum.CR_SER
    }
}
enum ResponseClassEnum {
    SP("Single Point", true),
    CR_SER("Concentration Response", true),
    CR_NO_SER("Probable Concentration Response but no Series", true),
    UNCLASS("Unclassified", false),
    MULTCONC("Multiple Test Concentrations", true)

    final String description
    final boolean isMapped

    ResponseClassEnum(String description, boolean isMapped) {
        this.description = description
        this.isMapped = isMapped
    }

    boolean isMapped() {
        return this.isMapped
    }

    static ResponseClassEnum toEnum(String representation) {
        return ResponseClassEnum.valueOf(representation)
    }


}

