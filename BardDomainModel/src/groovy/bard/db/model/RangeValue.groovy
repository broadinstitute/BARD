package bard.db.model

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 8/22/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
class RangeValue {
    float valueMin
    float valueMax

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        RangeValue that = (RangeValue) o

        if (Float.compare(that.valueMax, valueMax) != 0) return false
        if (Float.compare(that.valueMin, valueMin) != 0) return false

        return true
    }

    int hashCode() {
        int result
        result = (valueMin != +0.0f ? Float.floatToIntBits(valueMin) : 0)
        result = 31 * result + (valueMax != +0.0f ? Float.floatToIntBits(valueMax) : 0)
        return result
    }
}
