package bard.core.rest.spring.assays

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AbstractAssay extends JsonUtil {
    @JsonProperty("comments")
    private String comments;
    @JsonProperty("aid")
    private long aid;
    @JsonProperty("bardAssayId")
    private long bardAssayId;
    @JsonProperty("capAssayId")
    private long capAssayId;
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
    @JsonProperty("name")
    private String name;


    @JsonProperty("title")
    private String title;
    @JsonProperty("source")
    private String source;
    @JsonProperty("grantNo")
    private String grantNo;
    @JsonProperty("deposited")
    private String deposited;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("kegg_disease_names")
    private List<String> kegg_disease_names = new ArrayList<String>();
    @JsonProperty("kegg_disease_cat")
    private List<String> kegg_disease_cat = new ArrayList<String>();
    @JsonProperty("resourcePath")
    private String resourcePath;
    @JsonProperty("description")
    private String description;
    @JsonProperty("protocol")
    private String protocol;


    @JsonProperty("assay_id")
    public long getAssayId() {
        return this.assayId;
    }


    @JsonProperty("assay_id")
    public void setAssayId(long assayId) {
        this.assayId = assayId;
    }

    @JsonProperty("assay_id")
    private long assayId;

    public long getId() {
        if (this.bardAssayId) {
            return bardAssayId
        }
        return this.assayId
    }

    @JsonProperty("aid")
    public long getAid() {
        return this.aid;
    }

    @JsonProperty("aid")
    public void setAid(long aid) {
        this.aid = aid;
    }

    @JsonProperty("bardAssayId")
    public long getBardAssayId() {
        return this.bardAssayId;
    }

    @JsonProperty("bardAssayId")
    public void setBardAssayId(long bardAssayId) {
        this.bardAssayId = bardAssayId;
    }

    @JsonProperty("capAssayId")
    public long getCapAssayId() {
        return this.capAssayId;
    }

    @JsonProperty("capAssayId")
    public void setCapAssayId(long capAssayId) {
        this.capAssayId = capAssayId;
    }

    @JsonProperty("category")
    public long getCategory() {
        return this.category;
    }

    @JsonProperty("category")
    public void setCategory(long category) {
        this.category = category;
    }

    @JsonProperty("type")
    public long getType() {
        return this.type;
    }

    @JsonProperty("type")
    public void setType(long type) {
        this.type = type;
    }

    @JsonProperty("summary")
    public long getSummary() {
        return this.summary;
    }

    @JsonProperty("summary")
    public void setSummary(long summary) {
        this.summary = summary;
    }

    @JsonProperty("assays")
    public long getAssays() {
        return this.assays;
    }

    @JsonProperty("assays")
    public void setAssays(long assays) {
        this.assays = assays;
    }

    @JsonProperty("classification")
    public long getClassification() {
        return this.classification;
    }

    @JsonProperty("classification")
    public void setClassification(long classification) {
        this.classification = classification;
    }

    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("source")
    public String getSource() {
        return this.source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("grantNo")
    public String getGrantNo() {
        return this.grantNo;
    }

    @JsonProperty("grantNo")
    public void setGrantNo(String grantNo) {
        this.grantNo = grantNo;
    }

    @JsonProperty("deposited")
    public String getDeposited() {
        return this.deposited;
    }

    @JsonProperty("deposited")
    public void setDeposited(String deposited) {
        this.deposited = deposited;
    }

    @JsonProperty("updated")
    public String getUpdated() {
        return this.updated;
    }

    @JsonProperty("updated")
    public void setUpdated(String updated) {
        this.updated = updated;
    }



    @JsonProperty("kegg_disease_names")
    public List<String> getKegg_disease_names() {
        return this.kegg_disease_names;
    }

    @JsonProperty("kegg_disease_names")
    public void setKegg_disease_names(List<String> kegg_disease_names) {
        this.kegg_disease_names = kegg_disease_names;
    }

    @JsonProperty("kegg_disease_cat")
    public List<String> getKegg_disease_cat() {
        return this.kegg_disease_cat;
    }

    @JsonProperty("kegg_disease_cat")
    public void setKegg_disease_cat(List<String> kegg_disease_cat) {
        this.kegg_disease_cat = kegg_disease_cat;
    }

    @JsonProperty("resourcePath")
    public String getResourcePath() {
        return this.resourcePath;
    }

    @JsonProperty("resourcePath")
    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @JsonProperty("description")
    public String getDescription() {
        return this.description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("protocol")
    public String getProtocol() {
        return this.protocol;
    }

    @JsonProperty("protocol")
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @JsonProperty("comments")
    public String getComments() {
        return this.comments;
    }

    @JsonProperty("comments")
    public void setComments(String comments) {
        this.comments = comments;
    }
    @JsonProperty("title")
    public String getTitle() {
        return title
    }
    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title
    }

}
