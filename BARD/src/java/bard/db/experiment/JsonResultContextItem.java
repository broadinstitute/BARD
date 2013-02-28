package bard.db.experiment;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 2/26/13
 * Time: 6:00 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonResultContextItem {
    Long itemId;
    String attribute;
    String qualifier;
    Float valueNum;
    Float valueMin;
    Float valueMax;
    String valueDisplay;
    Long valueElementId;

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
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

    public String getValueDisplay() {
        return valueDisplay;
    }

    public void setValueDisplay(String valueDisplay) {
        this.valueDisplay = valueDisplay;
    }

    public Long getValueElementId() {
        return valueElementId;
    }

    public void setValueElementId(Long valueElementId) {
        this.valueElementId = valueElementId;
    }
}
