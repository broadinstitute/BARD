package bard.db.experiment.results;

import bard.db.dictionary.Element;
import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 3/29/13
 * Time: 1:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogicalKeyItem {
    Element attributeElement;
    Float valueNum;
    String qualifier;
    Float valueMin;
    Float valueMax;
    Element valueElement;
    String valueDisplay;

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        LogicalKeyItem that = (LogicalKeyItem) o

        if (attributeElement != that.attributeElement) return false
        if (qualifier != that.qualifier) return false
        if (valueDisplay != that.valueDisplay) return false
        if (valueElement != that.valueElement) return false
        if (valueMax != that.valueMax) return false
        if (valueMin != that.valueMin) return false
        if (valueNum != that.valueNum) return false

        return true
    }

    int hashCode() {
        int result
        result = (attributeElement != null ? attributeElement.hashCode() : 0)
        result = 31 * result + (valueNum != null ? valueNum.hashCode() : 0)
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0)
        result = 31 * result + (valueMin != null ? valueMin.hashCode() : 0)
        result = 31 * result + (valueMax != null ? valueMax.hashCode() : 0)
        result = 31 * result + (valueElement != null ? valueElement.hashCode() : 0)
        result = 31 * result + (valueDisplay != null ? valueDisplay.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "LogicalKeyItem{" +
                "attributeElement=" + attributeElement +
                ", valueNum=" + valueNum +
                ", qualifier='" + qualifier + '\'' +
                ", valueMin=" + valueMin +
                ", valueMax=" + valueMax +
                ", valueElement=" + valueElement +
                ", valueDisplay='" + valueDisplay + '\'' +
                '}';
    }

}
