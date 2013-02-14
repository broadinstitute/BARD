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
public class BardExpt {

    @JsonProperty("bardExptId")
    private Long bardExptId;
    @JsonProperty("capExptId")
    private Long capExptId;
    @JsonProperty("bardAssayId")
    private Long bardAssayId;
    @JsonProperty("capAssayId")
    private Long capAssayId;
    @JsonProperty("pubchemAid")
    private Long pubchemAid;
    @JsonProperty("category")
    private Long category;
    @JsonProperty("type")
    private Long type;
    @JsonProperty("summary")
    private Long summary;
    @JsonProperty("assays")
    private Long assays;
    @JsonProperty("classification")
    private Long classification;
    @JsonProperty("substances")
    private Long substances;
    @JsonProperty("compounds")
    private Long compounds;
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

    @JsonProperty("bardExptId")
    public Long getBardExptId() {
        return bardExptId;
    }

    @JsonProperty("bardExptId")
    public void setBardExptId(Long bardExptId) {
        this.bardExptId = bardExptId;
    }

    @JsonProperty("capExptId")
    public Long getCapExptId() {
        return capExptId;
    }

    @JsonProperty("capExptId")
    public void setCapExptId(Long capExptId) {
        this.capExptId = capExptId;
    }

    @JsonProperty("bardAssayId")
    public Long getBardAssayId() {
        return bardAssayId;
    }

    @JsonProperty("bardAssayId")
    public void setBardAssayId(Long bardAssayId) {
        this.bardAssayId = bardAssayId;
    }

    @JsonProperty("capAssayId")
    public Long getCapAssayId() {
        return capAssayId;
    }

    @JsonProperty("capAssayId")
    public void setCapAssayId(Long capAssayId) {
        this.capAssayId = capAssayId;
    }

    @JsonProperty("pubchemAid")
    public Long getPubchemAid() {
        return pubchemAid;
    }

    @JsonProperty("pubchemAid")
    public void setPubchemAid(Long pubchemAid) {
        this.pubchemAid = pubchemAid;
    }

    @JsonProperty("category")
    public Long getCategory() {
        return category;
    }

    @JsonProperty("category")
    public void setCategory(Long category) {
        this.category = category;
    }

    @JsonProperty("type")
    public Long getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(Long type) {
        this.type = type;
    }

    @JsonProperty("summary")
    public Long getSummary() {
        return summary;
    }

    @JsonProperty("summary")
    public void setSummary(Long summary) {
        this.summary = summary;
    }

    @JsonProperty("assays")
    public Long getAssays() {
        return assays;
    }

    @JsonProperty("assays")
    public void setAssays(Long assays) {
        this.assays = assays;
    }

    @JsonProperty("classification")
    public Long getClassification() {
        return classification;
    }

    @JsonProperty("classification")
    public void setClassification(Long classification) {
        this.classification = classification;
    }

    @JsonProperty("substances")
    public Long getSubstances() {
        return substances;
    }

    @JsonProperty("substances")
    public void setSubstances(Long substances) {
        this.substances = substances;
    }

    @JsonProperty("compounds")
    public Long getCompounds() {
        return compounds;
    }

    @JsonProperty("compounds")
    public void setCompounds(Long compounds) {
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
    public boolean hasProbe() {
        return hasProbe;
    }

    @JsonProperty("hasProbe")
    public void hasProbe(boolean hasProbe) {
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
}
