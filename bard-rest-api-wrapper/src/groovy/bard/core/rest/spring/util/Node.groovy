package bard.core.rest.spring.util

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo



@JsonInclude(JsonInclude.Include.NON_NULL)
public class Node extends JsonUtil implements Serializable {
    @JsonProperty("abbreviation")
    private String abbreviation;
    @JsonProperty("description")
    private String description;
    @JsonProperty("elementId")
    private long elementId;
    @JsonProperty("externalUrl")
    private String externalUrl;
    @JsonProperty("label")
    private String label;
    @JsonProperty("elementStatus")
    private String elementStatus;
    @JsonProperty("synonyms")
    private String synonyms;
    @JsonProperty("link")
    private String link;
    @JsonProperty("readyForExtraction")
    private String readyForExtraction;

    @JsonProperty("abbreviation")
    public String getAbbreviation() {
        return abbreviation;
    }

    @JsonProperty("abbreviation")
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("elementId")
    public long getElementId() {
        return elementId;
    }

    @JsonProperty("elementId")
    public void setElementId(long elementId) {
        this.elementId = elementId;
    }

    @JsonProperty("externalUrl")
    public String getExternalUrl() {
        return externalUrl;
    }

    @JsonProperty("externalUrl")
    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }

    @JsonProperty("label")
    public String getLabel() {
        return label;
    }

    @JsonProperty("label")
    public void setLabel(String label) {
        this.label = label;
    }

    @JsonProperty("elementStatus")
    public String getElementStatus() {
        return elementStatus;
    }

    @JsonProperty("elementStatus")
    public void setElementStatus(String elementStatus) {
        this.elementStatus = elementStatus;
    }

    @JsonProperty("synonyms")
    public String getSynonyms() {
        return synonyms;
    }

    @JsonProperty("synonyms")
    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    @JsonProperty("link")
    public String getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(String link) {
        this.link = link;
    }

    @JsonProperty("readyForExtraction")
    public String getReadyForExtraction() {
        return readyForExtraction;
    }

    @JsonProperty("readyForExtraction")
    public void setReadyForExtraction(String readyForExtraction) {
        this.readyForExtraction = readyForExtraction;
    }
}

