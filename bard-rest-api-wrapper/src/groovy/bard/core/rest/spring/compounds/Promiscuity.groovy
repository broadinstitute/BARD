package bard.core.rest.spring.compounds;


import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Promiscuity extends JsonUtil {

    @JsonProperty("hscafs")
    private List<PromiscuityScaffold> promiscuityScaffolds = new ArrayList<PromiscuityScaffold>();
    @JsonProperty("cid")
    private long cid;

    @JsonProperty("hscafs")
    public List<PromiscuityScaffold> getPromiscuityScaffolds() {
        return promiscuityScaffolds;
    }

    @JsonProperty("hscafs")
    public void setPromiscuityScaffolds(List<PromiscuityScaffold> promiscuityScaffolds) {
        this.promiscuityScaffolds = promiscuityScaffolds;
    }

    @JsonProperty("cid")
    public long getCid() {
        return cid;
    }

    @JsonProperty("cid")
    public void setCid(long cid) {
        this.cid = cid;
    }


}

