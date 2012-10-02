package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import org.apache.commons.lang.StringUtils

class CartCompound extends Shoppable {

    static int MAXIMUM_NAME_FIELD_LENGTH = 4000
    static int MAXIMUM_SMILES_FIELD_LENGTH = 1024

    int compoundId  = 0
    String smiles
    String name
    Boolean nameWasTruncated = false
    Boolean smileWasTruncated = false

    CartCompound() {

    }

    CartCompound( String smiles, String name, int compoundId ) {
        this.smiles = smiles
        this.name = name
        this.compoundId = compoundId
    }

    static constraints = {
        smiles (nullable: false, maxSize: MAXIMUM_SMILES_FIELD_LENGTH)
        name (nullable: false, maxSize: MAXIMUM_NAME_FIELD_LENGTH)
        compoundId (min : 1)
    }


    public void setSmiles(String smiles)    {
        if (smiles != null)  {
            Integer lengthOfSmiles
            Integer incomingStringLength = smiles.length()
            if  (incomingStringLength<=MAXIMUM_SMILES_FIELD_LENGTH) {
                lengthOfSmiles = incomingStringLength
            }  else {
                lengthOfSmiles = MAXIMUM_SMILES_FIELD_LENGTH
                smileWasTruncated = true
            }
            this.smiles = smiles.substring(0,lengthOfSmiles)
        }
    }


    public void setName(String name)    {
        if (StringUtils.isNotBlank(name))  {
            Integer lengthOfName
            Integer incomingStringLength = name.length()
            if  (incomingStringLength<=MAXIMUM_NAME_FIELD_LENGTH) {
                lengthOfName = incomingStringLength
            }  else {
                lengthOfName = MAXIMUM_NAME_FIELD_LENGTH
                nameWasTruncated = true
            }
            this.name = name.substring(0,lengthOfName)
        }
    }


    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CartCompound)) return false

        CartCompound that = (CartCompound) o

        if (smiles != that.smiles) return false
        if (name != that.name) return false
        if (compoundId != that.compoundId) return false

        return true
    }


    int hashCode() {
        return (smiles != null ? smiles.hashCode() : 0)
    }

    @Override
    String toString() {
        String returnValue
        String trimmedName = name?.trim()
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


}
