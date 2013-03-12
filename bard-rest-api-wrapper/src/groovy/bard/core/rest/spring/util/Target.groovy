package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Target extends JsonUtil{

    @JsonProperty("classes")
    private List<TargetClassification> targetClassifications = new ArrayList<TargetClassification>();
    @JsonProperty("url")
    private String url;
    @JsonProperty("acc")
    private String acc;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("status")
    private String status;
    @JsonProperty("geneId")
    private long geneId;
    @JsonProperty("taxId")
    private long taxId;
    @JsonProperty("resourcePath")
    private String resourcePath;


    @JsonProperty("classes")
    public List<TargetClassification> getTargetClassifications() {
        return targetClassifications
    }
    @JsonProperty("classes")
    public void setTargetClassifications(List<TargetClassification> targetClassifications) {
        this.targetClassifications = targetClassifications
    }
    @JsonProperty("url")
    public String getUrl() {
        return url
    }
    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url
    }
    @JsonProperty("acc")
    public String getAcc() {
        return acc;
    }

    @JsonProperty("acc")
    public void setAcc(String acc) {
        this.acc = acc;
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

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("geneId")
    public long getGeneId() {
        return geneId;
    }

    @JsonProperty("geneId")
    public void setGeneId(long geneId) {
        this.geneId = geneId;
    }

    @JsonProperty("taxId")
    public long getTaxId() {
        return taxId;
    }

    @JsonProperty("taxId")
    public void setTaxId(long taxId) {
        this.taxId = taxId;
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
