package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartProject extends Shoppable {

    String projectName = "no project name yet specified"

    @Override
    String toString() {
        projectName
    }


    static constraints = {
        projectName  :  blank: false;
    }
}
