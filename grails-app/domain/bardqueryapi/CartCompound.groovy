package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartCompound extends Shoppable {

    int compoundId  = 0
    String smiles

    CartCompound() {

    }

    CartCompound( String smiles, int compoundId ) {
        this.smiles = smiles
        this.compoundId = compoundId
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CartCompound)) return false

        CartCompound that = (CartCompound) o

        if (smiles != that.smiles) return false
        if (compoundId != that.compoundId) return false

        return true
    }


    int hashCode() {
        return (smiles != null ? smiles.hashCode() : 0)
    }

    @Override
    String toString() {
        String trimmedName = smiles?.trim()
        if ((trimmedName == null) ||
            (trimmedName?.length() == 0) ||
            ("null".equals(trimmedName)))
           "PubChem CID=${compoundId.toString()}"
        else
            trimmedName
    }

    static constraints = {
        smiles (nullable: false)
        compoundId (min : 1)
    }
}
