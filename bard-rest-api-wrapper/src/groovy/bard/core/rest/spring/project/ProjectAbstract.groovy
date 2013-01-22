package bard.core.rest.spring.project

import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.util.Document
import bard.core.rest.spring.util.JsonUtil
import bard.core.rest.spring.util.Target
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectAbstract extends JsonUtil {


    private long projectId;
    @JsonProperty("category")
    private long category;
    @JsonProperty("type")
    private long type;
    @JsonProperty("classification")
    private long classification;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("source")
    private String source;
    @JsonProperty("gobp_id")
    private String gobp_id;
    @JsonProperty("gobp_term")
    private String gobp_term;
    @JsonProperty("gomf_term")
    private String gomf_term;
    @JsonProperty("gomf_id")
    private String gomf_id;
    @JsonProperty("gocc_id")
    private String gocc_id;
    @JsonProperty("gocc_term")
    private String gocc_term;
    @JsonProperty("av_dict_label")
    private List<String> av_dict_label = new ArrayList<String>();
    @JsonProperty("ak_dict_label")
    private List<String> ak_dict_label = new ArrayList<String>();
    @JsonProperty("kegg_disease_names")
    private List<String> kegg_disease_names = new ArrayList<String>();
    @JsonProperty("kegg_disease_cat")
    private List<String> kegg_disease_cat = new ArrayList<String>();
    @JsonProperty("grantNo")
    private String grantNo;
    @JsonProperty("deposited")
    private String deposited;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("probes")
    private List<Compound> probes = new ArrayList<Compound>();
    @JsonProperty("probeIds")
    private List<String> probeIds = new ArrayList<String>();

    @JsonProperty("targets")
    private List<Target> targets = new ArrayList<Target>();
    @JsonProperty("resourcePath")
    private String resourcePath;
    @JsonProperty("experimentCount")
    private long experimentCount;

    @JsonProperty("projectId")
    public long getProjectId() {
        return projectId;
    }

    public long getId() {
        return this.getProjectId()
    }

    @JsonProperty("projectId")
    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @JsonProperty("proj_id")
    public long getProjId() {
        return this.projectId;
    }


    @JsonProperty("proj_id")
    public void setProjId(long projId) {
        this.projectId = projId;
    }

    @JsonProperty("category")
    public long getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(long category) {
        this.category = category;
    }

    @JsonProperty("type")
    public long getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(long type) {
        this.type = type;
    }

    @JsonProperty("classification")
    public long getClassification() {
        return classification;
    }

    @JsonProperty("classification")
    public void setClassification(long classification) {
        this.classification = classification;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("gobp_id")
    public String getGobp_id() {
        return gobp_id;
    }

    @JsonProperty("gobp_id")
    public void setGobp_id(String gobp_id) {
        this.gobp_id = gobp_id;
    }

    @JsonProperty("gobp_term")
    public String getGobp_term() {
        return gobp_term;
    }

    @JsonProperty("gobp_term")
    public void setGobp_term(String gobp_term) {
        this.gobp_term = gobp_term;
    }

    @JsonProperty("gomf_term")
    public String getGomf_term() {
        return gomf_term;
    }

    @JsonProperty("gomf_term")
    public void setGomf_term(String gomf_term) {
        this.gomf_term = gomf_term;
    }

    @JsonProperty("gomf_id")
    public String getGomf_id() {
        return gomf_id;
    }

    @JsonProperty("gomf_id")
    public void setGomf_id(String gomf_id) {
        this.gomf_id = gomf_id;
    }

    @JsonProperty("gocc_id")
    public String getGocc_id() {
        return gocc_id;
    }

    @JsonProperty("gocc_id")
    public void setGocc_id(String gocc_id) {
        this.gocc_id = gocc_id;
    }

    @JsonProperty("gocc_term")
    public String getGocc_term() {
        return gocc_term;
    }

    @JsonProperty("gocc_term")
    public void setGocc_term(String gocc_term) {
        this.gocc_term = gocc_term;
    }

    @JsonProperty("av_dict_label")
    public List<String> getAv_dict_label() {
        return av_dict_label;
    }

    @JsonProperty("av_dict_label")
    public void setAv_dict_label(List<String> av_dict_label) {
        this.av_dict_label = av_dict_label;
    }

    @JsonProperty("ak_dict_label")
    public List<String> getAk_dict_label() {
        return ak_dict_label;
    }

    @JsonProperty("ak_dict_label")
    public void setAk_dict_label(List<String> ak_dict_label) {
        this.ak_dict_label = ak_dict_label;
    }

    @JsonProperty("kegg_disease_names")
    public List<String> getKegg_disease_names() {
        return kegg_disease_names;
    }

    @JsonProperty("kegg_disease_names")
    public void setKegg_disease_names(List<String> kegg_disease_names) {
        this.kegg_disease_names = kegg_disease_names;
    }

    @JsonProperty("kegg_disease_cat")
    public List<String> getKegg_disease_cat() {
        return kegg_disease_cat;
    }

    @JsonProperty("kegg_disease_cat")
    public void setKegg_disease_cat(List<String> kegg_disease_cat) {
        this.kegg_disease_cat = kegg_disease_cat;
    }

    @JsonProperty("grantNo")
    public String getGrantNo() {
        return grantNo;
    }

    @JsonProperty("grantNo")
    public void setGrantNo(String grantNo) {
        this.grantNo = grantNo;
    }

    @JsonProperty("deposited")
    public String getDeposited() {
        return deposited;
    }

    @JsonProperty("deposited")
    public void setDeposited(String deposited) {
        this.deposited = deposited;
    }

    @JsonProperty("updated")
    public String getUpdated() {
        return updated;
    }

    @JsonProperty("updated")
    public void setUpdated(String updated) {
        this.updated = updated;
    }

    @JsonProperty("probes")
    public List<Compound> getProbes() {
        return probes;
    }

    @JsonProperty("probes")
    public void setProbes(List<Compound> probes) {
        this.probes = probes;
    }

    @JsonProperty("probeIds")
    public List<String> getProbeIds() {
        return probeIds;
    }

    @JsonProperty("probeIds")
    public void setProbeIds(List<String> probeIds) {
        this.probeIds = probeIds;
    }

    @JsonProperty("targets")
    public List<Target> getTargets() {
        return targets;
    }

    @JsonProperty("targets")
    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @JsonProperty("experimentCount")
    public long getExperimentCount() {
        return experimentCount;
    }

    @JsonProperty("experimentCount")
    public void setExperimentCount(long experimentCount) {
        this.experimentCount = experimentCount;
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
