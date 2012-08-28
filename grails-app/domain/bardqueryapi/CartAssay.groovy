package bardqueryapi


import com.metasieve.shoppingcart.Shoppable

class CartAssay extends Shoppable {

    String assayTitle = "unknown assay name"

    @Override
    String toString() {
        assayTitle
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CartAssay)) return false

        CartAssay cartAssay = (CartAssay) o

        if (assayTitle != cartAssay.assayTitle) return false

        return true
    }

    int hashCode() {
        return (assayTitle != null ? assayTitle.hashCode() : 0)
    }

    static constraints = {
        assayTitle   blank: false
    }
}
