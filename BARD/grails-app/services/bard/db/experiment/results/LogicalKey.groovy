package bard.db.experiment.results

import bard.db.dictionary.Element

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/29/13
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
class LogicalKey {
    Integer replicateNumber;
    Long substanceId

    Element resultType
    Element statsModifier
    Float valueNum
    String qualifier;
    Float valueMin
    Float valueMax
    Element valueElement
    String valueDisplay

    Set<LogicalKeyItem> items = [] as Set

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
        result = 31 * result + substanceId.hashCode()
        result = 31 * result + resultType.hashCode()
        result = 31 * result + (statsModifier != null ? statsModifier.hashCode() : 0)
        result = 31 * result + (valueNum != null ? valueNum.hashCode() : 0)
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0)
        result = 31 * result + (valueMin != null ? valueMin.hashCode() : 0)
        result = 31 * result + (valueMax != null ? valueMax.hashCode() : 0)
        result = 31 * result + (valueElement != null ? valueElement.hashCode() : 0)
        result = 31 * result + (valueDisplay != null ? valueDisplay.hashCode() : 0)
        result = 31 * result + items.hashCode()
        return result
    }

    @Override
    public String toString() {
        return "{" +
                "replicateNumber=" + replicateNumber +
                ", substance=" + substanceId +
                ", resultType=" + resultType?.label +
                ", statsModifier=" + statsModifier?.label +
                ", valueNum=" + valueNum +
                ", qualifier='" + qualifier + '\'' +
                ", valueMin=" + valueMin +
                ", valueMax=" + valueMax +
                ", valueElement=" + valueElement +
                ", valueDisplay='" + valueDisplay + '\'' +
                ", items=" + items +
                '}';
    }
}
