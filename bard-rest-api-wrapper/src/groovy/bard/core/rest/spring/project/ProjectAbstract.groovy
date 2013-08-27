package bard.core.rest.spring.project
import bard.core.rest.spring.biology.BiologyEntity
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectAbstract extends JsonUtil {

    Double score

    Map<Long, String> experimentTypes;

    long bardProjectId;
    long capProjectId

    long category;
    long type;
    long classification;
    String name;
    String description;
    String source;
    String grantNo;
    String deposited;
    String updated;
    List<Compound> probes = new ArrayList<Compound>();
    List<String> probeIds = new ArrayList<String>();
    long experimentCount;
    String resourcePath;
    List<BiologyEntity> targets = new ArrayList<BiologyEntity>();

    // These seem to be deprecated
    String gobp_id;
    String gobp_term;
    String gomf_term;
    String gomf_id;
    String gocc_id;
    String gocc_term;
    List<String> av_dict_label = new ArrayList<String>();
    List<String> ak_dict_label = new ArrayList<String>();
    List<String> kegg_disease_names = new ArrayList<String>();
    List<String> kegg_disease_cat = new ArrayList<String>();


    public long getId() {
        return this.getBardProjectId()
    }

    @JsonProperty("projectId")
    public long getProjId() {
        return this.bardProjectId;
    }

    @JsonProperty("projectId")
    public void setProjId(long projId) {
        this.bardProjectId = projId;
    }

    // Note that keeping a targets property is a work-around for an issue with the way that ProjectRestService de-serializes the response
    // from a post of project ids using a type cast instead of Jackson.
    public List<BiologyEntity> getBiology() {
        return this.targets
    }

    public boolean hasProbes() {
        if (this.probes) {
            return true
        }
        if (this.probeIds) {
            return true
        }
        return false
    }
}
