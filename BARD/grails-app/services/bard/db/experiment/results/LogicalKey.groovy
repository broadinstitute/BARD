package bard.db.experiment.results;

import bard.db.dictionary.Element;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/29/13
 * Time: 1:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogicalKey {
    Integer replicateNumber;
    Long substanceId;

    Element resultType;
    Element statsModifier;
    Float valueNum;
    String qualifier;
    Float valueMin;
    Float valueMax;
    Element valueElement;
    String valueDisplay;

    java.util.Set<LogicalKeyItem> items = new HashSet();

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        LogicalKey that = (LogicalKey) o

        if (items != that.items) return false
        if (qualifier != that.qualifier) return false
        if (replicateNumber != that.replicateNumber) return false
        if (resultType != that.resultType) return false
        if (statsModifier != that.statsModifier) return false
        if (substanceId != that.substanceId) return false
        if (valueDisplay != that.valueDisplay) return false
        if (valueElement != that.valueElement) return false
        if (valueMax != that.valueMax) return false
        if (valueMin != that.valueMin) return false
        if (valueNum != that.valueNum) return false

        return true
    }

    int hashCode() {
        int result
        result = (replicateNumber != null ? replicateNumber.hashCode() : 0)
        result = 31 * result + (substanceId != null ? substanceId.hashCode() : 0)
        result = 31 * result + (resultType != null ? resultType.hashCode() : 0)
        result = 31 * result + (statsModifier != null ? statsModifier.hashCode() : 0)
        result = 31 * result + (valueNum != null ? valueNum.hashCode() : 0)
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0)
        result = 31 * result + (valueMin != null ? valueMin.hashCode() : 0)
        result = 31 * result + (valueMax != null ? valueMax.hashCode() : 0)
        result = 31 * result + (valueElement != null ? valueElement.hashCode() : 0)
        result = 31 * result + (valueDisplay != null ? valueDisplay.hashCode() : 0)
        result = 31 * result + (items != null ? items.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "{" +
                "replicateNumber=" + replicateNumber +
                ", substance=" + substanceId +
                ", resultType=" + (resultType == null ? null : resultType.getLabel()) +
                ", statsModifier=" + (statsModifier == null ? null :statsModifier.getLabel()) +
                ", valueNum=" + valueNum +
                ", qualifier='" + qualifier + '\'' +
                ", valueMin=" + valueMin +
                ", valueMax=" + valueMax +
                ", valueElement=" + valueElement +
                ", valueDisplay='" + valueDisplay + '\'' +
                ", items=" + items +
                '}';
    }

    public Integer getReplicateNumber() {
        return replicateNumber;
    }

    public void setReplicateNumber(Integer replicateNumber) {
        this.replicateNumber = replicateNumber;
    }

    public Long getSubstanceId() {
        return substanceId;
    }

    public void setSubstanceId(Long substanceId) {
        this.substanceId = substanceId;
    }

    public Element getResultType() {
        return resultType;
    }

    public void setResultType(Element resultType) {
        this.resultType = resultType;
    }

    public Element getStatsModifier() {
        return statsModifier;
    }

    public void setStatsModifier(Element statsModifier) {
        this.statsModifier = statsModifier;
    }

    public Float getValueNum() {
        return valueNum;
    }

    public void setValueNum(Float valueNum) {
        this.valueNum = valueNum;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
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

    public Element getValueElement() {
        return valueElement;
    }

    public void setValueElement(Element valueElement) {
        this.valueElement = valueElement;
    }

    public String getValueDisplay() {
        return valueDisplay;
    }

    public void setValueDisplay(String valueDisplay) {
        this.valueDisplay = valueDisplay;
    }

    public Set<LogicalKeyItem> getItems() {
        return items;
    }

    public void setItems(Set<LogicalKeyItem> items) {
        this.items = items;
    }
}
