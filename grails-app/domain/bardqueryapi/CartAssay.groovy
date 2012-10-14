package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class CartAssay extends Shoppable {

    static int MAXIMUM_ASSAY_TITLE_FIELD_LENGTH = 4000

    Long assayId = 0
    String assayTitle
    Boolean assayTitleWasTruncated = false

    CartAssay() {

    }

    CartAssay(String assayTitle, String assayIdStr) {
        this.assayTitle = assayTitle
        this.assayId = Long.parseLong(assayIdStr)
    }

    CartAssay(String assayTitle, int assayId) {
        this.assayTitle = assayTitle
        this.assayId = assayId as Long
    }

    CartAssay(String assayTitle, Long assayId) {
        this.assayTitle = assayTitle
        this.assayId = assayId
    }

    static constraints = {
        assayTitle(blank: false, maxSize: MAXIMUM_ASSAY_TITLE_FIELD_LENGTH)
    }


    @Override
    String toString() {
        String trimmedName = assayTitle?.trim()
        if (StringUtils.isBlank(assayTitle) || ("null".equals(trimmedName))) {
            return ""
        }
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << trimmedName

        // if name was truncated, then add in ellipses
        if (assayTitleWasTruncated) {
            stringBuilder <<= "..."
        }
        stringBuilder.toString()
    }



    public void setAssayTitle(String assayTitle) {
        if (StringUtils.isNotBlank(assayTitle)) {
            Integer lengthOfName = assayTitle.length()
            if (lengthOfName > MAXIMUM_ASSAY_TITLE_FIELD_LENGTH) {
                lengthOfName = MAXIMUM_ASSAY_TITLE_FIELD_LENGTH
                assayTitleWasTruncated = true
            }
            this.assayTitle = assayTitle.substring(0, lengthOfName)
        }
        else{
            this.assayTitle=assayTitle
        }
    }
    /**
     *  equals
     * @param o
     * @return
     */
    boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        CartAssay that = (CartAssay) obj;
        return new EqualsBuilder().
                append(this.assayId, that.assayId).
                append(this.assayTitle, that.assayTitle).
                isEquals();
    }

    /**
     *  hashCode
     * @return
     */
    int hashCode() {
        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                append(this.assayTitle).
                append(this.assayId).
                toHashCode();
    }

}
