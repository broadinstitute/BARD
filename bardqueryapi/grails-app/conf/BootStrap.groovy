import bardqueryapi.CartAssay
import com.metasieve.shoppingcart.*

class BootStrap {

    def init = { servletContext ->
        CartAssay cartAssay = new CartAssay( assayTitle : "Determination of inhibition of thapsigargin-initiated ATF-6 protein induction in ER stress signaling" )
        cartAssay.save()
    }
    def destroy = {
    }
}
