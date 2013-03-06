package bard.db.experiment;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.codehaus.groovy.grails.web.json.JSONArray;
import org.codehaus.groovy.grails.web.json.JSONObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/26/13
 * Time: 6:02 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonResult {
    Long resultId;
    Long resultTypeId;
    Long statsModifierId;
    String resultType;
    Float valueNum;
    Float valueMin;
    Float valueMax;
    Integer replicateNumber;
    String valueDisplay;
    String qualifier;
    String relationship;
    List<JsonResult> related;
    List<JsonResultContextItem> contextItems;

    public List<JsonResultContextItem> getContextItems() {
        return contextItems;
    }

    public void setContextItems(List<JsonResultContextItem> contextItems) {
        this.contextItems = contextItems;
    }

    public List<JsonResult> getRelated() {
        return related;
    }

    public void setRelated(List<JsonResult> related) {
        this.related = related;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getResultTypeId() {
        return resultTypeId;
    }

    public void setResultTypeId(Long resultTypeId) {
        this.resultTypeId = resultTypeId;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public Float getValueNum() {
        return valueNum;
    }

    public void setValueNum(Float valueNum) {
        this.valueNum = valueNum;
    }

    public Float getValueMin() {
        return valueMin;
    }

    public void setValueMin(Float valueMin) {
        this.valueMin = valueMin;
    }

    public Float getValueMax() {
        return valueMax;
    }

    public void setValueMax(Float valueMax) {
        this.valueMax = valueMax;
    }

    public Integer getReplicateNumber() {
        return replicateNumber;
    }

    public void setReplicateNumber(Integer replicateNumber) {
        this.replicateNumber = replicateNumber;
    }

    public String getValueDisplay() {
        return valueDisplay;
    }

    public void setValueDisplay(String valueDisplay) {
        this.valueDisplay = valueDisplay;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Long getStatsModifierId() {
        return statsModifierId;
    }

    public void setStatsModifierId(Long statsModifierId) {
        this.statsModifierId = statsModifierId;
    }
}
