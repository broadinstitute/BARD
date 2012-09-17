package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService

class QueryCartController {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    // add a single element the shopping cart
    def add() {

        def somethingWasAdded
        int stt = Integer.parseInt(params.stt)
        if (params.class == 'class bardqueryapi.CartAssay') {

            somethingWasAdded = queryCartService.addToShoppingCart( new CartAssay( params.assayTitle, params.id ) )

        }  else if (params.class == 'class bardqueryapi.CartCompound') {

            CartCompound cartCompound = new CartCompound( smiles:params.smiles, name: params.name )
            if ( params.cid != null )
                cartCompound.compoundId =  Integer.parseInt(params.cid)
            somethingWasAdded = queryCartService.addToShoppingCart( cartCompound )

        } else if (params.class == 'class bardqueryapi.CartProject') {

            somethingWasAdded = queryCartService.addToShoppingCart( new CartProject( params.projectName,params.id  ) )

        }

        if (somethingWasAdded != null)  // something was added, so the display must change
            if (stt==0)
                render(template: '/bardWebInterface/queryCartIndicator')
            else
                render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display via Ajax


        return

    }



    def updateOnscreenCart() {
        int stt = Integer.parseInt(params.stt)
        if (stt==0)
            render(template: '/bardWebInterface/queryCartIndicator')
        else
            render(template:'/bardWebInterface/sarCartContent')  // refresh the cart display via Ajax
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
