package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartAssay extends Shoppable {
    String assayTitle = "unknown"

    static constraints = {
        assayTitle :  blank: false;
    }
}
