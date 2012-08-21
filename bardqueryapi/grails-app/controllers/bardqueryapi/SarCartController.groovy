package bardqueryapi

import com.metasieve.shoppingcart.ShoppingCartService
import grails.converters.JSON
import com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct
import com.metasieve.shoppingcart.Shoppable

class SarCartController {
    ShoppingCartService shoppingCartService

    def index() { }
    def add() {

//         CartAssay cartAssay = new CartAssay(request.JSON)
        CartAssay cartAssay
        if (params.class == 'class+com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct') {
            cartAssay = ShoppingCartInterfaceTestProduct.get(params.id)
        } else {
            cartAssay = Shoppable.get(params.id)
        }
        if(params.version) {
            def version = params.version.toLong()
//            if(cartAssay.version > version) {
//                cartAssay.errors.rejectValue("version", "shoppable.optimistic.locking.failure", message(code:"Shoppable.already.updated"))
//            } else {
                CartAssay newCartAssay = new CartAssay(assayTitle:"control_dummy")
                shoppingCartService.addToShoppingCart(newCartAssay)
//            }
        } else {
            shoppingCartService.addToShoppingCart(cartAssay)
        }

//        render(template:'sarCartContent', plugin:'shoppingCart')
        render(template:'/bardWebInterface/sarCartContent',model:[data:shoppingCartService,textStatus:'foo'])

//        CartAssay cartAssay = new CartAssay(assayTitle:"control_dummy")
//        shoppingCartService.addToShoppingCart(cartAssay)
//        def listOfResults =  shoppingCartService.findAll()
//        render(listOfResults  as JSON )
    }
    def remove() {
        shoppingCartService.removeFromShoppingCart(new CartAssay(request.JSON))
        render( CartAssay.findByShoppingItem(it['item']) as JSON )
    }
    def removeAll() {
        shoppingCartService.emptyShoppingCart()
        render( CartAssay.findByShoppingItem(it['item']) as JSON )
    }
}
