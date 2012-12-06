package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/26/12
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document extends JsonUtil {

    @JsonProperty("title")
    private String title;
    @JsonProperty("doi")
    private String doi;
    @JsonProperty("abs")
    private String abs;
    @JsonProperty("pubmedId")
    private long pubmedId;
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    @JsonProperty("title")
    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("doi")
    public String getDoi() {
        return doi;
    }

    @JsonProperty("doi")
    public void setDoi(String doi) {
        this.doi = doi;
    }

    @JsonProperty("abs")
    public String getAbs() {
        return abs;
    }

    @JsonProperty("abs")
    public void setAbs(String abs) {
        this.abs = abs;
    }

    @JsonProperty("pubmedId")
    public long getPubmedId() {
        return pubmedId;
    }

    @JsonProperty("pubmedId")
    public void setPubmedId(long pubmedId) {
        this.pubmedId = pubmedId;
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
