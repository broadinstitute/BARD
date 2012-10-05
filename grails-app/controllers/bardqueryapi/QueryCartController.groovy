package bardqueryapi

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
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

            CartCompound cartCompound = new CartCompound( smiles:params.smiles, name: params.name, compoundId: params.id )
            somethingWasAdded = queryCartService.addToShoppingCart( cartCompound )

        } else if (params.class == 'class bardqueryapi.CartProject') {

            somethingWasAdded = queryCartService.addToShoppingCart( new CartProject( params.projectName,params.id  ) )

        }

        if (somethingWasAdded != null)  // something was added, so the display must change
            if (stt==0)
                render(template: '/bardWebInterface/queryCartIndicator', model: getModelForSummary())
            else
                render(template:'/bardWebInterface/sarCartContent', model: getModelForDetails())  // refresh the cart display via Ajax


        return

    }



    def updateOnscreenCart() {
        int stt = Integer.parseInt(params.stt)
        if (stt==0)
            return updateSummary()
        else
            return updateDetails()  // refresh the cart display via Ajax
    }

    def updateSummary() {
        render(template: '/bardWebInterface/queryCartIndicator', model: getModelForSummary())
    }

    def updateDetails() {
        render(template:'/bardWebInterface/sarCartContent', model: getModelForDetails())
    }

    Map getModelForSummary() {
        Map<String,List> mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
        Integer totalItemCount = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)
        Integer numberOfAssays = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartAssay)
        Integer numberOfCompounds = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartCompound)
        Integer numberOfProjects = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems,QueryCartService.cartProject)
        return ['totalItemCount':totalItemCount, 'numberOfAssays':numberOfAssays, 'numberOfCompounds': numberOfCompounds, 'numberOfProjects': numberOfProjects];
    }

    Map getModelForDetails() {
        Map<String,List> mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
        Integer totalItemCount = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)
        List compounds = mapOfUniqueItems.get(QueryCartService.cartCompound)
        List assayDefinitions = mapOfUniqueItems.get(QueryCartService.cartAssay)
        List projects = mapOfUniqueItems.get(QueryCartService.cartProject)
        return ['totalItemCount':totalItemCount, 'compounds':compounds, 'assayDefinitions':assayDefinitions, 'projects':projects]
    }

    // remove a single element
    def remove() {
        int idToRemove = Integer.parseInt(params.id)
        def shoppingItem = Shoppable.get(idToRemove)
        queryCartService.removeFromShoppingCart(shoppingItem)
        render(template:'/bardWebInterface/sarCartContent', model: getModelForDetails())  // refresh the cart display
    }

    // empty out everything from the shopping cart
    def removeAll() {
        queryCartService.emptyShoppingCart()
        render( template:'/bardWebInterface/sarCartContent', model: getModelForDetails() ) // refresh the cart display
    }
}
