package bardqueryapi


import com.metasieve.shoppingcart.Shoppable

class CartAssay extends Shoppable {

    Long assayId  = 0
    String assayTitle

    @Override
    String toString() {
        assayTitle
    }
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
        assayTitle   blank: false
    }
}
