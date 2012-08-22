package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct
import com.metasieve.shoppingcart.ShoppingCartService

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
            if (params.class == 'class bardqueryapi.CartAssay') {
                CartAssay newCartAssay = new CartAssay( assayTitle:params.assayTitle )
                shoppingCartService.addToShoppingCart(newCartAssay)
            }  else if (params.class == 'class bardqueryapi.CartCompound') {
                CartCompound newCartCompound = new CartCompound( smiles:params.smiles )
                shoppingCartService.addToShoppingCart(newCartCompound)
            }
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
        render( template:'/bardWebInterface/sarCartContent' )
    }
}
