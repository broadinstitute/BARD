package bard.core.rest.spring.project

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/14/13
 * Time: 9:26 AM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProjectStepAnnotation {

    @JsonProperty("entityId")
    private Long entityId;
    @JsonProperty("entity")
    private String entity;
    @JsonProperty("source")
    private String source;
    @JsonProperty("id")
    private Long id;
    @JsonProperty("display")
    private String display;
    @JsonProperty("contextRef")
    private String contextRef;
    @JsonProperty("key")
    private String key;
    @JsonProperty("value")
    private String value;
    @JsonProperty("extValueId")
    private String extValueId;
    @JsonProperty("url")
    private String url;
    @JsonProperty("displayOrder")
    private Long displayOrder;
    @JsonProperty("related")
    private String related;

    @JsonProperty("entityId")
    public Long getEntityId() {
        return entityId;
    }

    @JsonProperty("entityId")
    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    @JsonProperty("entity")
    public String getEntity() {
        return entity;
    }

    @JsonProperty("entity")
    public void setEntity(String entity) {
        this.entity = entity;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    @JsonProperty("display")
    public String getDisplay() {
        return display;
    }

    @JsonProperty("display")
    public void setDisplay(String display) {
        this.display = display;
    }

    @JsonProperty("contextRef")
    public String getContextRef() {
        return contextRef;
    }

    @JsonProperty("contextRef")
    public void setContextRef(String contextRef) {
        this.contextRef = contextRef;
    }

    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    @JsonProperty("key")
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonProperty("value")
    public void setValue(String value) {
        this.value = value;
    }

    @JsonProperty("extValueId")
    public String getExtValueId() {
        return extValueId;
    }

    @JsonProperty("extValueId")
    public void setExtValueId(String extValueId) {
        this.extValueId = extValueId;
    }

    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    @JsonProperty("url")
    public void setUrl(String url) {
        this.url = url;
    }

    @JsonProperty("displayOrder")
    public Long getDisplayOrder() {
        return displayOrder;
    }

    @JsonProperty("displayOrder")
    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

    @JsonProperty("related")
    public String getRelated() {
        return related;
    }

    @JsonProperty("related")
    public void setRelated(String related) {
        this.related = related;
    }


}
