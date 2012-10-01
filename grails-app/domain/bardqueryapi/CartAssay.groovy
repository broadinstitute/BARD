package bardqueryapi


import com.metasieve.shoppingcart.Shoppable

class CartAssay extends Shoppable {

    static int MAXIMUM_ASSAY_TITLE_FIELD_LENGTH = 4000

    Long assayId  = 0
    String assayTitle
    Boolean assayTitleWasTruncated = false

    CartAssay(){

    }

    CartAssay (String assayTitle, String assayIdStr)   {
        this.assayTitle =  assayTitle
        this.assayId =  Long.parseLong(assayIdStr)
    }

    CartAssay (String assayTitle, int assayId)   {
        this.assayTitle =  assayTitle
        this.assayId =  assayId as Long
    }

    CartAssay (String assayTitle, Long assayId)   {
        this.assayTitle =  assayTitle
        this.assayId =  assayId
    }


    @Override
    String toString() {
        StringBuilder stringBuilder  = new StringBuilder()
        String trimmedName = assayTitle?.trim()
        if ( (trimmedName == null) ||
                (trimmedName?.length() == 0) ||
                ("null".equals(trimmedName)))
            stringBuilder << ""
        else
            stringBuilder << trimmedName

        // if name was truncated, then add in ellipses
        if (assayTitleWasTruncated)
            stringBuilder <<= "..."

        stringBuilder.toString()
    }



    public void setAssayTitle(String assayTitle)    {
        if (assayTitle != null)  {
            Integer lengthOfName
            Integer incomingStringLength = assayTitle.length()
            if  (incomingStringLength<=MAXIMUM_ASSAY_TITLE_FIELD_LENGTH) {
                lengthOfName = incomingStringLength
            }  else {
                lengthOfName =MAXIMUM_ASSAY_TITLE_FIELD_LENGTH
                assayTitleWasTruncated = true
            }
            this.assayTitle = assayTitle.substring(0,lengthOfName)
        }
    }

    /**
     *  equals
     * @param o
     * @return
     */
    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof CartAssay)) return false

        CartAssay cartAssay = (CartAssay) o

        if (assayTitle != cartAssay.assayTitle) return false

        return true
    }

    /**
     *  hashCode
     * @return
     */
    int hashCode() {
        return (assayTitle != null ? assayTitle.hashCode() : 0)
    }

    static constraints = {
        assayTitle  ( blank: false, maxSize: MAXIMUM_ASSAY_TITLE_FIELD_LENGTH)
    }
}
