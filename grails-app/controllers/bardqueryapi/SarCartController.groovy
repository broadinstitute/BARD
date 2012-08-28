package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService

class SarCartController {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    // add a single element the shopping cart
    def add() {

        def somethingWasAdded
            if (params.class == 'class bardqueryapi.CartAssay') {
                CartAssay newCartAssay = new CartAssay( assayTitle:params.assayTitle )
                somethingWasAdded = queryCartService.addToShoppingCart(newCartAssay)
            }  else if (params.class == 'class bardqueryapi.CartCompound') {
                CartCompound newCartCompound = new CartCompound( smiles:params.smiles )
                somethingWasAdded = queryCartService.addToShoppingCart(newCartCompound)
            } else if (params.class == 'class bardqueryapi.CartProject') {
                CartProject newCartProject = new CartProject( projectName:params.projectName )
                somethingWasAdded = queryCartService.addToShoppingCart(newCartProject)
            }
        if (somethingWasAdded != null)
           render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display
    }

    // remove a single element
    def remove() {
        int idToRemove = Integer.parseInt(params.id)
        def shoppingItem = Shoppable.get(idToRemove)
        queryCartService.removeFromShoppingCart(shoppingItem)
        render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display
    }

    // empty out everything from the shopping cart
    def removeAll() {
        queryCartService.emptyShoppingCart()
        render( template:'/bardWebInterface/sarCartContent' ) // refresh the cart display
    }
}
