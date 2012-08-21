package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct
import com.metasieve.shoppingcart.ShoppingCartService
import grails.converters.JSON

class SarCartController {
    ShoppingCartService shoppingCartService

    def index() { }
    def add() {
        CartAssay cartAssay
        if (params.class == 'class+com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct') {
            cartAssay = ShoppingCartInterfaceTestProduct.get(params.id)
        } else {
            cartAssay = Shoppable.get(params.id)
        }
        if(params.version) {
                CartAssay newCartAssay = new CartAssay(assayTitle:"control_dummy")
                shoppingCartService.addToShoppingCart(newCartAssay)
        } else {
            shoppingCartService.addToShoppingCart(cartAssay)
        }
        render(template:'/bardWebInterface/sarCartContent')
    }
    def remove() {
        int idToRemove = Integer.parseInt(params.id)
        def shoppingItem = Shoppable.get(idToRemove)
        shoppingCartService.removeFromShoppingCart(shoppingItem)
        render(template:'/bardWebInterface/sarCartContent')
    }
    def removeAll() {
        shoppingCartService.emptyShoppingCart()
        render( CartAssay.findByShoppingItem(it['item']) as JSON )
    }
}
