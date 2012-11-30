package bard.core.rest.spring.experiment

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder
import bard.core.interfaces.ExperimentRole

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:33 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ExperimentAbstract {


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
    private Object source;
    @JsonProperty("grantNo")
    private Object grantNo;
    @JsonProperty("deposited")
    private Object deposited;
    @JsonProperty("updated")
    private Object updated;
    @JsonProperty("hasProbe")
    private boolean hasProbe;
    @JsonProperty("projectIdList")
    private List<Long> projectIdList = new ArrayList<Long>();
    @JsonProperty("resourcePath")
    private String resourcePath;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("exptId")
    public long getExptId() {
        return exptId;
    }

    @JsonProperty("exptId")
    public void setExptId(long exptId) {
        this.exptId = exptId;
    }

    public long getId() {
        if(exptId){
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
     public ExperimentRole getRole(){
         return ExperimentRole.valueOf(this.getClassification().intValue())
     }
    @JsonProperty("description")
    public void setDescription(String description) {
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
    public Object getUpdated() {
        return updated;
    }

    @JsonProperty("updated")
    public void setUpdated(Object updated) {
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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
    public int getProjectCount(){
        return projectIdList.size()
    }
    public abstract Long getAdid();

}
