package bardqueryapi

import com.metasieve.shoppingcart.Shoppable

class CartCompound extends Shoppable {

    static int MAXIMUM_NAME_FIELD_LENGTH = 255

    int compoundId  = 0
    String smiles
    Boolean nameWasTruncated = false

    CartCompound() {

    }

    CartCompound( String smiles, int compoundId ) {
        this.smiles = smiles
        this.compoundId = compoundId
    }


    public void setSmiles(String smiles)    {
        if (smiles != null)  {
            Integer lengthOfSmiles
            Integer incomingStringLength = smiles.length()
            if  (incomingStringLength<=MAXIMUM_NAME_FIELD_LENGTH) {
                lengthOfSmiles = incomingStringLength
            }  else {
                lengthOfSmiles = MAXIMUM_NAME_FIELD_LENGTH
                nameWasTruncated = true
            }
            this.smiles = smiles.substring(0,lengthOfSmiles)
        }
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
        String returnValue
        String trimmedName = smiles?.trim()
        if ((trimmedName == null) ||
            (trimmedName?.length() == 0) ||
            ("null".equals(trimmedName)))
            returnValue = "PubChem CID=${compoundId.toString()}"
        else
            returnValue = trimmedName

        // if name was truncated, then add in ellipses
        if (nameWasTruncated)
            returnValue += "..."

        returnValue
    }

    static constraints = {
        smiles (nullable: false, maxSize: MAXIMUM_NAME_FIELD_LENGTH)
        compoundId (min : 1)
    }
}
