package querycart

import com.metasieve.shoppingcart.Shoppable
import com.metasieve.shoppingcart.ShoppingCartService
import grails.plugins.springsecurity.Secured
import querycart.CartAssay
import querycart.CartCompound
import querycart.CartProject
import querycart.QueryCartService

@Secured(['isFullyAuthenticated()'])
class QueryCartController {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    // add a single element the shopping cart
    def add() {

        def somethingWasAdded = null
        int somethingReallyChanged = Integer.parseInt(params.stt)
        if (params.class == 'class querycart.CartAssay') {

           // somethingWasAdded = queryCartService.addToShoppingCart(new CartAssay(params.assayTitle, params.id))
            somethingWasAdded = handleAddingToShoppingCart(new CartAssay(params.assayTitle, params.id) )

        } else if (params.class == 'class querycart.CartCompound') {

            CartCompound cartCompound = new CartCompound(smiles: params.smiles, name: params.name, compoundId: params.id)
//            somethingWasAdded = queryCartService.addToShoppingCart(cartCompound)
            somethingWasAdded = handleAddingToShoppingCart(cartCompound)

        } else if (params.class == 'class querycart.CartProject') {

//            somethingWasAdded = queryCartService.addToShoppingCart(new CartProject(params.projectName, params.id))
            somethingWasAdded = handleAddingToShoppingCart(new CartProject(params.projectName, params.id))

        }

        if (somethingWasAdded != null) {  // something was added, so the display must change
            if (somethingReallyChanged == 0) {
                render(template: '/bardWebInterface/queryCartIndicator', model: modelForSummary)
            } else {
                render(template: '/bardWebInterface/sarCartContent', model: modelForDetails)  // refresh the cart display via Ajax
            }
        }
    }

    def handleAddingToShoppingCart(Shoppable shoppable) {
        def returnValue
        if (shoppable) {
            try {
                returnValue = queryCartService.addToShoppingCart(shoppable)
            } catch (Exception exception) {
                log.error("Error performing assay search", exception)
            }
        } else {
            log.error("Received unexpected null shoppable")
        }
        returnValue
    }

    def updateOnscreenCart() {
        int somethingReallyChanged = Integer.parseInt(params.stt)
        if (somethingReallyChanged == 0) {
            return updateSummary()
        }
        return updateDetails()  // refresh the cart display via Ajax


    }

    def updateSummary() {
        render(template: '/bardWebInterface/queryCartIndicator', model: modelForSummary)
    }

    def updateDetails() {
        render(template: '/bardWebInterface/sarCartContent', model: modelForDetails)
    }

    Map getModelForSummary() {
            Map<String, List> mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
            Integer totalItemCount = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)
            Integer numberOfAssays = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems, QueryCartService.cartAssay)
            Integer numberOfCompounds = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems, QueryCartService.cartCompound)
            Integer numberOfProjects = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems, QueryCartService.cartProject)
            return ['totalItemCount': totalItemCount, 'numberOfAssays': numberOfAssays, 'numberOfCompounds': numberOfCompounds, 'numberOfProjects': numberOfProjects];
     }

    Map getModelForDetails() {
        Map<String, List> mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
        Integer totalItemCount = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)
        List compounds = mapOfUniqueItems.get(QueryCartService.cartCompound)
        List assayDefinitions = mapOfUniqueItems.get(QueryCartService.cartAssay)
        List projects = mapOfUniqueItems.get(QueryCartService.cartProject)
        return ['totalItemCount': totalItemCount, 'compounds': compounds, 'assayDefinitions': assayDefinitions, 'projects': projects]
    }

    // remove a single element
    def remove() {
        int idToRemove = Integer.parseInt(params.id)
        try{
            def shoppingItem = Shoppable.get(idToRemove)
            queryCartService.removeFromShoppingCart(shoppingItem)
        } catch (Exception e) {
            log.error("Problem removing item $idToRemove")
        }
        render(template: '/bardWebInterface/sarCartContent', model: modelForDetails)  // refresh the cart display
    }

    // empty out everything from the shopping cart
    def removeAll() {
        queryCartService.emptyShoppingCart()
        render(template: '/bardWebInterface/sarCartContent', model: modelForDetails) // refresh the cart display
    }
}
