package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartCompound extends Shoppable {


    String smiles = "no Smiles definition"

    static constraints = {
        smiles: nullable: false
    }
}
