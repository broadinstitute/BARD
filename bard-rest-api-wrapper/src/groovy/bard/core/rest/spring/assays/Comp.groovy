package bard.core.rest.spring.assays

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Serialized usually from an ID search or contained in an expanded element (e.g Experiment)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Comp extends JsonUtil {
    @JsonProperty("entityId")
    private Object entityId;
    @JsonProperty("entity")
    private String entity;
    @JsonProperty("source")
    private String source;
    @JsonProperty("id")
    private long id;
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
    private long displayOrder;
    @JsonProperty("related")
    private String related;

    @JsonProperty("entityId")
    public Object getEntityId() {
        return entityId;
    }

    @JsonProperty("entityId")
    public void setEntityId(Object entityId) {
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
    public long getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(long id) {
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
    public long getDisplayOrder() {
        return displayOrder;
    }

    @JsonProperty("displayOrder")
    public void setDisplayOrder(long displayOrder) {
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

    static List<List<Comp>> splitForColumnLayout(List<Comp> comps) {
        int totalNumContextItems = comps.size()
        int half = Math.ceil(totalNumContextItems / 2) //make the first column the longest in case of an odd number of elements.

        if (totalNumContextItems) {
            List<Comp> firstColumnComps = comps[0..(half - 1)]
            List<Comp> secondColumnComps = comps - firstColumnComps
            def splitComps = [firstColumnComps, secondColumnComps].findAll() // eliminates any empty lists
            return splitComps
        }

        return []
    }
}
