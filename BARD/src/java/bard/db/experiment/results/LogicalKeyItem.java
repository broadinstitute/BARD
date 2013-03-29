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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogicalKeyItem that = (LogicalKeyItem) o;

        if (attributeElement != null ? !attributeElement.equals(that.attributeElement) : that.attributeElement != null)
            return false;
        if (qualifier != null ? !qualifier.equals(that.qualifier) : that.qualifier != null) return false;
        if (valueDisplay != null ? !valueDisplay.equals(that.valueDisplay) : that.valueDisplay != null) return false;
        if (valueElement != null ? !valueElement.equals(that.valueElement) : that.valueElement != null) return false;
        if (valueMax != null ? !valueMax.equals(that.valueMax) : that.valueMax != null) return false;
        if (valueMin != null ? !valueMin.equals(that.valueMin) : that.valueMin != null) return false;
        if (valueNum != null ? !valueNum.equals(that.valueNum) : that.valueNum != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attributeElement != null ? attributeElement.hashCode() : 0;
        result = 31 * result + (valueNum != null ? valueNum.hashCode() : 0);
        result = 31 * result + (qualifier != null ? qualifier.hashCode() : 0);
        result = 31 * result + (valueMin != null ? valueMin.hashCode() : 0);
        result = 31 * result + (valueMax != null ? valueMax.hashCode() : 0);
        result = 31 * result + (valueElement != null ? valueElement.hashCode() : 0);
        result = 31 * result + (valueDisplay != null ? valueDisplay.hashCode() : 0);
        return result;
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
