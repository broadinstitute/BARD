package querycart

import com.metasieve.shoppingcart.Shoppable
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.EqualsBuilder

class CartCompound extends Shoppable {

    static int MAXIMUM_NAME_FIELD_LENGTH = 4000
    static int MAXIMUM_SMILES_FIELD_LENGTH = 1024

    int compoundId = 0
    String smiles
    String name
    Boolean nameWasTruncated = false
    Boolean smileWasTruncated = false

    CartCompound() {

    }

    CartCompound(String smiles, String name, int compoundId) {
        this.smiles = smiles
        this.name = name
        this.compoundId = compoundId
    }

    static constraints = {
        smiles(nullable: false, maxSize: MAXIMUM_SMILES_FIELD_LENGTH)
        name(nullable: false, blank: false, maxSize: MAXIMUM_NAME_FIELD_LENGTH)
        compoundId(min: 1)
    }

    public void setSmiles(String smiles) {
        if (StringUtils.isNotBlank(smiles)) {
            Integer lengthOfSmiles = smiles.length()
            Integer incomingStringLength = smiles.length()
            if (incomingStringLength > MAXIMUM_SMILES_FIELD_LENGTH) {
                lengthOfSmiles = MAXIMUM_SMILES_FIELD_LENGTH
                smileWasTruncated = true
            }
            this.smiles = smiles.substring(0, lengthOfSmiles)
        }
    }


    public void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            Integer lengthOfName = name.length()
            Integer incomingStringLength = name.length()
            if (incomingStringLength > MAXIMUM_NAME_FIELD_LENGTH) {
                lengthOfName = MAXIMUM_NAME_FIELD_LENGTH
                nameWasTruncated = true
            }
            this.name = name.substring(0, lengthOfName)
        }
    }


    boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        CartCompound that = (CartCompound) obj;
        return new EqualsBuilder().
                append(this.smiles, that.smiles).
                append(this.name, that.name).
                append(this.compoundId, that.compoundId).
                isEquals();
    }


    int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.smiles).
                toHashCode();
    }

    @Override
    String toString() {
        String returnValue
        String trimmedName = name?.trim()
        if ((trimmedName == null) ||
                (trimmedName?.length() == 0) ||
                ("null".equals(trimmedName))) {
            returnValue = "PubChem CID=${compoundId.toString()}"
        }
        else {
            returnValue = trimmedName
        }

        // if name was truncated, then add in ellipses
        if (nameWasTruncated) {
            returnValue += "..."
        }

        returnValue
    }
}
