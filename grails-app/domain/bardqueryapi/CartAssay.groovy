package bardqueryapi


import com.metasieve.shoppingcart.Shoppable

class CartAssay extends Shoppable {

    String assayTitle = "unknown assay name"

    @Override
    String toString() {
        assayTitle
    }

    boolean equals(o) {
        if(o==null)  return false
        if (this.is(o)) return true

        CartAssay cartAssay = (CartAssay) o

        if (assayTitle != cartAssay.assayTitle) return false

        return true
    }

    int hashCode() {
        int result
        result = (assayTitle != null ? assayTitle.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        return result
    }

    static constraints = {
        assayTitle   blank: false
    }
}
