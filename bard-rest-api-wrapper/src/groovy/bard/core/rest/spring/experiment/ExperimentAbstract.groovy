package bard.core.rest.spring.experiment

import bard.core.interfaces.ExperimentRole
import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ExperimentAbstract extends JsonUtil {


    private long id;
    @JsonProperty("exptId")
    private long exptId;
    @JsonProperty("pubchemAid")
    private long pubchemAid;
    @JsonProperty("category")
    private long category;
    @JsonProperty("type")
    private long type;
    @JsonProperty("summary")
    private long summary;
    @JsonProperty("classification")
    private long classification;
    @JsonProperty("substances")
    private long substances;
    @JsonProperty("compounds")
    private long compounds;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("source")
    private String source;
    @JsonProperty("grantNo")
    private String grantNo;
    @JsonProperty("deposited")
    private String deposited;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("hasProbe")
    private boolean hasProbe;
    @JsonProperty("projectIdList")
    private List<Long> projectIdList = new ArrayList<Long>();
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("exptId")
    public long getExptId() {
        return exptId;
    }

    @JsonProperty("exptId")
    public void setExptId(long exptId) {
        this.exptId = exptId;
    }

    public long getId() {
        if (exptId) {
            this.id = exptId
        }
        return id;
    }

    @JsonProperty("pubchemAid")
    public long getPubchemAid() {
        return pubchemAid;
    }

    @JsonProperty("pubchemAid")
    public void setPubchemAid(long pubchemAid) {
        this.pubchemAid = pubchemAid;
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

    @JsonProperty("summary")
    public long getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(long summary) {
        this.summary = summary;
    }

    @JsonProperty("classification")
    public long getClassification() {
        return classification;
    }

    @JsonProperty("classification")
    public void setClassification(long classification) {
        this.classification = classification;
    }

    @JsonProperty("substances")
    public long getSubstances() {
        return substances;
    }

    @JsonProperty("substances")
    public void setSubstances(long substances) {
        this.substances = substances;
    }

    @JsonProperty("compounds")
    public long getCompounds() {
        return compounds;
    }

    @JsonProperty("compounds")
    public void setCompounds(long compounds) {
        this.compounds = compounds;
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

    public ExperimentRole getRole() {
        return ExperimentRole.valueOf(this.getClassification().intValue())
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

    @JsonProperty("hasProbe")
    public boolean isHasProbe() {
        return hasProbe;
    }

    @JsonProperty("hasProbe")
    public void setHasProbe(boolean hasProbe) {
        this.hasProbe = hasProbe;
    }

    @JsonProperty("projectIdList")
    public List<Long> getProjectIdList() {
        return projectIdList;
    }

    @JsonProperty("projectIdList")
    public void setProjectIdList(List<Long> projectIdList) {
        this.projectIdList = projectIdList;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public int getProjectCount() {
        return projectIdList.size()
    }

    public abstract Long getAdid();

}
