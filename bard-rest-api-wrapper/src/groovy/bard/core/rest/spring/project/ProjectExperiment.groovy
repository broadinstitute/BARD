package bard.core.rest.spring.project

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/14/13
 * Time: 9:25 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectExperiment {
    private final List<ProjectStep> precedingProjectSteps = new ArrayList<ProjectStep>();
    private final List<ProjectStep> followingProjectSteps = new ArrayList<ProjectStep>();


    @JsonProperty("bardExptId")
    private long bardExptId;
    @JsonProperty("capExptId")
    private long capExptId;
    @JsonProperty("bardAssayId")
    private long bardAssayId;
    @JsonProperty("capAssayId")
    private long capAssayId;
    @JsonProperty("pubchemAid")
    private long pubchemAid;
    @JsonProperty("category")
    private long category;
    @JsonProperty("type")
    private long type;
    @JsonProperty("summary")
    private long summary;
    @JsonProperty("assays")
    private long assays;
    @JsonProperty("classification")
    private long classification;
    @JsonProperty("substances")
    private long substances;
    @JsonProperty("compounds")
    private long compounds;
    @JsonProperty("activeCompounds")
    private long activeCompounds;
    @JsonProperty("confidenceLevel")
    private long confidenceLevel;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private Object description;
    @JsonProperty("source")
    private Object source;
    @JsonProperty("grantNo")
    private Object grantNo;
    @JsonProperty("deposited")
    private Object deposited;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("hasProbe")
    private boolean hasProbe;
    @JsonProperty("projectIdList")
    private List<Long> projectIdList = new ArrayList<Long>();
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("bardExptId")
    public long getBardExptId() {
        return bardExptId;
    }

    @JsonProperty("bardExptId")
    public void setBardExptId(long bardExptId) {
        this.bardExptId = bardExptId;
    }

    @JsonProperty("capExptId")
    public long getCapExptId() {
        return capExptId;
    }

    @JsonProperty("capExptId")
    public void setCapExptId(long capExptId) {
        this.capExptId = capExptId;
    }

    @JsonProperty("bardAssayId")
    public long getBardAssayId() {
        return bardAssayId;
    }

    @JsonProperty("bardAssayId")
    public void setBardAssayId(long bardAssayId) {
        this.bardAssayId = bardAssayId;
    }

    @JsonProperty("capAssayId")
    public long getCapAssayId() {
        return capAssayId;
    }

    @JsonProperty("capAssayId")
    public void setCapAssayId(long capAssayId) {
        this.capAssayId = capAssayId;
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

    @JsonProperty("assays")
    public long getAssays() {
        return assays;
    }

    @JsonProperty("assays")
    public void setAssays(long assays) {
        this.assays = assays;
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

    @JsonProperty("activeCompounds")
    public long getActiveCompounds() {
        return activeCompounds;
    }

    @JsonProperty("activeCompounds")
    public void setActiveCompounds(long activeCompounds) {
        this.activeCompounds = activeCompounds;
    }

    @JsonProperty("confidenceLevel")
    public long getConfidenceLevel() {
        return confidenceLevel;
    }

    @JsonProperty("confidenceLevel")
    public void setConfidenceLevel(long confidenceLevel) {
        this.confidenceLevel = confidenceLevel;
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
    public Object getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(Object description) {
        this.description = description;
    }

    @JsonProperty("source")
    public Object getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(Object source) {
        this.source = source;
    }

    @JsonProperty("grantNo")
    public Object getGrantNo() {
        return grantNo;
    }

    @JsonProperty("grantNo")
    public void setGrantNo(Object grantNo) {
        this.grantNo = grantNo;
    }

    @JsonProperty("deposited")
    public Object getDeposited() {
        return deposited;
    }

    @JsonProperty("deposited")
    public void setDeposited(Object deposited) {
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

    public void addPrecedingProjectStep(ProjectStep projectStep){
        this.precedingProjectSteps.add(projectStep)
    }
    public void addFollowingProjectStep(ProjectStep projectStep){
        this.followingProjectSteps.add(projectStep)
    }
    public List<ProjectStep> getPrecedingProjectSteps(){
        return this.precedingProjectSteps;
    }
    public List<ProjectStep> getFollowingProjectSteps(){
        return this.followingProjectSteps;
    }

}
