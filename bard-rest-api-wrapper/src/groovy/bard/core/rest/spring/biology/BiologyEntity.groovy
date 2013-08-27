package bard.core.rest.spring.biology

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 5/16/13
 * Time: 9:03 AM
 * To change this template use File | Settings | File Templates.
 */

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BiologyEntity extends JsonUtil {

    @JsonProperty("biology")
    private String biology;
    @JsonProperty("entityId")
    private long entityId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("entity")
    private String entity;
    @JsonProperty("extId")
    private String extId;
    @JsonProperty("extRef")
    private String extRef;
    @JsonProperty("dictLabel")
    private String dictLabel;
    @JsonProperty("dictId")
    private long dictId;
    @JsonProperty("serial")
    private long serial;
    @JsonProperty("updated")
    private String updated;
    @JsonProperty("resourcePath")
    private String resourcePath;

    @JsonProperty("biology")
    public String getBiology() {
        return biology;
    }

    @JsonProperty("biology")
    public void setBiology(String biology) {
        this.biology = biology;
    }

    @JsonProperty("entityId")
    public long getEntityId() {
        return entityId;
    }

    @JsonProperty("entityId")
    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }

    @JsonProperty("name")
    String getName() {
        return name
    }

    @JsonProperty("name")
    void setName(String name) {
        this.name = name
    }

    @JsonProperty("entity")
    String getEntity() {
        return entity
    }

    @JsonProperty("entity")
    void setEntity(String entity) {
        this.entity = entity
    }

    @JsonProperty("extId")
    String getExtId() {
        return extId
    }

    @JsonProperty("extId")
    void setExtId(String extId) {
        this.extId = extId
    }

    @JsonProperty("extRef")
    String getExtRef() {
        return extRef
    }

    @JsonProperty("extRef")
    void setExtRef(String extRef) {
        this.extRef = extRef
    }

    @JsonProperty("dictLabel")
    String getDictLabel() {
        return dictLabel
    }

    @JsonProperty("dictLabel")
    void setDictLabel(String dictLabel) {
        this.dictLabel = dictLabel
    }

    @JsonProperty("dictId")
    long getDictId() {
        return dictId
    }

    @JsonProperty("dictId")
    void setDictId(long dictId) {
        this.dictId = dictId
    }

    @JsonProperty("serial")
    long getSerial() {
        return serial
    }

    @JsonProperty("serial")
    void setSerial(long serial) {
        this.serial = serial
    }

    @JsonProperty("updated")
    String getUpdated() {
        return updated
    }

    @JsonProperty("updated")
    void setUpdated(String updated) {
        this.updated = updated
    }

    @JsonProperty("resourcePath")
    String getResourcePath() {
        return resourcePath
    }

    @JsonProperty("resourcePath")
    void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath
    }

}

