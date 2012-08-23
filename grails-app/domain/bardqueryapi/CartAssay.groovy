package bardqueryapi


import com.metasieve.shoppingcart.Shoppable

class CartAssay extends Shoppable {

    String assayTitle = "unknown assay name"

    @Override
    String toString() {
        assayTitle
    }

    static constraints = {
        assayTitle :  blank: false;
    }
}
