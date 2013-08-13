package bard.core.rest.spring.compounds

import bard.core.rest.spring.util.JsonUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 3/8/13
 * Time: 10:28 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
class ProbeAnnotation extends JsonUtil {
    @JsonProperty("entityId")
    private String entityId;
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
    @JsonProperty("contextGroup")
    private String contextGroup;
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
    public String getEntityId() {
        return entityId;
    }

    @JsonProperty("entityId")
    public void setEntityId(String entityId) {
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

    @JsonProperty("contextGroup")
    public String getContextGroup() {
        return contextGroup;
    }

    @JsonProperty("contextGroup")
    public void setContextGroup(String contextGroup) {
        this.contextGroup = contextGroup;
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
    public boolean isProbeLink(){
        boolean isProbe = false
        if(this.url?.startsWith(PROBE_URL_PREFIX)){
            isProbe =  true;
        }
        return isProbe;
    }
    public boolean isPubChemCIDLink(){
        boolean isCID = false
        if(this.url?.startsWith(PUBCHEM_CID_PREFIX)){
            isCID =  true;
        }
        return isCID;
    }
    public boolean isPubChemSIDLink(){
        boolean isSID = false
        if(this.url?.startsWith(PUBCHEM_SID_PREFIX)){
            isSID =  true;
        }
        return isSID;
    }
    static final String PROBE_URL_PREFIX = "https://mli.nih.gov/mli/?dl_id="
    static final String PUBCHEM_CID_PREFIX = "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?cid="
    static final String PUBCHEM_SID_PREFIX = "http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid="
}