package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService

class SarCartController {
    ShoppingCartService shoppingCartService

    def index() { }

    // add a single element the shopping cart
    def add() {
         CartAssay cartAssay  //provide a default

         if(params.version) {
            if (params.class == 'class bardqueryapi.CartAssay') {
                CartAssay newCartAssay = new CartAssay( assayTitle:params.assayTitle )
                shoppingCartService.addToShoppingCart(newCartAssay)
            }  else if (params.class == 'class bardqueryapi.CartCompound') {
                CartCompound newCartCompound = new CartCompound( smiles:params.smiles )
                shoppingCartService.addToShoppingCart(newCartCompound)
            } else if (params.class == 'class bardqueryapi.CartProject') {
                CartProject newCartProject = new CartProject( projectName:params.projectName )
                shoppingCartService.addToShoppingCart(newCartProject)
            }
        } else {
            shoppingCartService.addToShoppingCart(cartAssay)
        }
        render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display
    }

    // remove a single element
    def remove() {
        int idToRemove = Integer.parseInt(params.id)
        def shoppingItem = Shoppable.get(idToRemove)
        shoppingCartService.removeFromShoppingCart(shoppingItem)
        render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display
    }

    // empty out everything from the shopping cart
    def removeAll() {
        shoppingCartService.emptyShoppingCart()
        render( template:'/bardWebInterface/sarCartContent' ) // refresh the cart display
    }
}
