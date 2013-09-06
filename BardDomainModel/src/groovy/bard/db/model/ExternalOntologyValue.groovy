package bard.db.model

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 8/22/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class ExternalOntologyValue {
    String valueDisplay;
    String extValueId;

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ExternalOntologyValue that = (ExternalOntologyValue) o

        if (extValueId != that.extValueId) return false
        if (valueDisplay != that.valueDisplay) return false

        return true
    }

    int hashCode() {
        int result
        result = (valueDisplay != null ? valueDisplay.hashCode() : 0)
        result = 31 * result + (extValueId != null ? extValueId.hashCode() : 0)
        return result
    }
}
