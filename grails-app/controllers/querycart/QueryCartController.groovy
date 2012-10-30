package querycart

import com.metasieve.shoppingcart.ShoppingCartService
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class QueryCartController {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    def addItem() {
        QueryItemType itemType = params.type as QueryItemType
        Long id = params.id as Long
        String name = params.name

        QueryItem item = QueryItem.findByExternalIdAndQueryItemType(id, itemType)
        if (!item) {
            switch(itemType) {
                case QueryItemType.Compound:
                    String smiles = params.smiles
                    item = new CartCompound(smiles, name, id)
                    break
                case QueryItemType.Project:
                    item = new CartProject(name, id)
                    break
                case QueryItemType.AssayDefinition:
                    item = new CartAssay(name, id)
                    break
                default:
                    throw new UnsupportedOperationException("Unsupported QueryItemType " + itemType)
            }
        }
        queryCartService.addToShoppingCart(item)

        return updateOnscreenCart()
    }

    def removeItem() {
        QueryItemType itemType = params.type as QueryItemType
        Long id = params.id as Long
        QueryItem item = QueryItem.findByExternalIdAndQueryItemType(id, itemType)
        if (item) {
            queryCartService.removeFromShoppingCart(item)
        }

        return updateOnscreenCart()
    }

    // empty out everything from the shopping cart
    def removeAll() {
        queryCartService.emptyShoppingCart()
        render(template: '/bardWebInterface/sarCartContent', model: modelForDetails) // refresh the cart display
    }

    def isInCart() {
        Long idToCheck = params.id as Long
        QueryItemType itemType = params.type as QueryItemType

        Boolean result = false
        QueryItem shoppingItem = QueryItem.findByExternalIdAndQueryItemType(idToCheck, itemType)
        if (shoppingItem) {
            result = queryCartService.isInShoppingCart(shoppingItem)
        }
        render result
    }

    def updateOnscreenCart() {
        boolean showCartDetails = Boolean.parseBoolean(params.showCartDetails)
        if (showCartDetails) {
            return updateDetails()  // refresh the cart display via Ajax
        }
        return updateSummary()
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

}
