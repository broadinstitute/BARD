package bard.db.model

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 8/22/13
 * Time: 3:14 PM
 * To change this template use File | Settings | File Templates.
 */
class NumericValue {
    String qualifier;
    float number;

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        NumericValue that = (NumericValue) o

        if (Float.compare(that.number, number) != 0) return false
        if (qualifier != that.qualifier) return false

        return true
    }

    int hashCode() {
        int result
        result = (qualifier != null ? qualifier.hashCode() : 0)
        result = 31 * result + (number != +0.0f ? Float.floatToIntBits(number) : 0)
        return result
    }
}
