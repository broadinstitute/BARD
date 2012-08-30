package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartCompound extends Shoppable {


    String smiles = "no Smiles definition"

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CartCompound)) return false

        CartCompound that = (CartCompound) o

        if (smiles != that.smiles) return false

        return true
    }

    int hashCode() {
        return (smiles != null ? smiles.hashCode() : 0)
    }

    @Override
    String toString() {
        smiles
    }

    static constraints = {
        smiles: nullable: false
    }
}
