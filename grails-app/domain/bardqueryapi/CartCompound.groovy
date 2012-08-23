package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartCompound extends Shoppable {


    String smiles = "no Smiles definition"


    @Override
    String toString() {
        smiles
    }
    static constraints = {
        smiles: nullable: false
    }
}
