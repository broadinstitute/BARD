package querycart

import com.metasieve.shoppingcart.ShoppingCartService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class QueryCartController {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    def refreshSummaryView() {
        render(template: '/bardWebInterface/queryCartIndicator', model: modelForSummary)
    }

    def refreshDetailsView() {
        render(template: '/bardWebInterface/sarCartContent', model: modelForDetails)
    }

    def addItem() {

        Map errorResponse = [:]

        QueryItemType itemType = this.parseQueryItemTypeFromParams(errorResponse)
        if (errorResponse.size() > 0) {
            return render(errorResponse)
        }

        Long id = this.parseIdFromParams(errorResponse)
        if (errorResponse.size() > 0) {
            return render(errorResponse)
        }

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
///CLOVER:OFF
                default:
                    return render(status: 400, text: "Unsupported QueryItemType [${itemType}]")
///CLOVER:ON
            }
        }

        if (!item.validate()) {
            response.status = 500
            return render(item.errors.allErrors.collect {
                message(error:it,encodeAs:'HTML')
            } as JSON)
        }

        queryCartService.addToShoppingCart(item)

        return render(status: 200, text: "Added item [${item}] to cart")
    }

    def removeItem() {

        Map errorResponse = [:]

        QueryItemType itemType = this.parseQueryItemTypeFromParams(errorResponse)
        if (errorResponse.size() > 0) {
            return render(errorResponse)
        }

        Long id = this.parseIdFromParams(errorResponse)
        if (errorResponse.size() > 0) {
            return render(errorResponse)
        }

        QueryItem item = QueryItem.findByExternalIdAndQueryItemType(id, itemType)
        if (item) {
            queryCartService.removeFromShoppingCart(item)
        }

        return render(status: 200, text: "Removed item [${item}] from cart")
    }

    def removeAll() {
        queryCartService.emptyShoppingCart()
        return render(status: 200, text: "Removed all items from cart")
    }

    def isInCart() {

        Map errorResponse = [:]

        QueryItemType itemType = this.parseQueryItemTypeFromParams(errorResponse)
        if (errorResponse.size() > 0) {
            return render(errorResponse)
        }

        Long id = this.parseIdFromParams(errorResponse)
        if (errorResponse.size() > 0) {
            return render(errorResponse)
        }

        Boolean result = false
        QueryItem shoppingItem = QueryItem.findByExternalIdAndQueryItemType(id, itemType)
        if (shoppingItem) {
            result = queryCartService.isInShoppingCart(shoppingItem)
        }
        render result
    }

    private QueryItemType parseQueryItemTypeFromParams(Map errorResponse) {
        QueryItemType itemType = null
        try {
            itemType = params.type as QueryItemType
            if (itemType == null) {
                errorResponse.status = 400
                errorResponse.text = 'Null QueryItemType passed as params.type'
            }
        }
        catch(IllegalArgumentException e) {
            log.error("Invalid QueryItemType ${params.type}", e)
            errorResponse.status = 400
            errorResponse.text = "Invalid QueryItemType [${params.type}] passed as params.type"
        }
        return itemType
    }

    private Long parseIdFromParams(Map errorResponse) {
        Long id = null
        try {
            id = params.id as Long
            if (id == null) {
                errorResponse.status = 400
                errorResponse.text = 'Null ID passed as params.id'
            }
        }
        catch(NumberFormatException e) {
            log.error("Invalid ID ${params.id}", e)
            errorResponse.status = 400
            errorResponse.text = "Invalid ID [${params.id}] passed as params.id"
        }
        return id
    }

    private Map getModelForSummary() {
        Map<String, List> mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
        Integer totalItemCount = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)
        Integer numberOfAssays = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems, QueryCartService.cartAssay)
        Integer numberOfCompounds = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems, QueryCartService.cartCompound)
        Integer numberOfProjects = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems, QueryCartService.cartProject)
        return ['totalItemCount': totalItemCount, 'numberOfAssays': numberOfAssays, 'numberOfCompounds': numberOfCompounds, 'numberOfProjects': numberOfProjects];
    }

    private Map getModelForDetails() {
        Map<String, List> mapOfUniqueItems = queryCartService.groupUniqueContentsByType(shoppingCartService)
        Integer totalItemCount = queryCartService.totalNumberOfUniqueItemsInCart(mapOfUniqueItems)
        List compounds = mapOfUniqueItems.get(QueryCartService.cartCompound)
        List assayDefinitions = mapOfUniqueItems.get(QueryCartService.cartAssay)
        List projects = mapOfUniqueItems.get(QueryCartService.cartProject)
        return ['totalItemCount': totalItemCount, 'compounds': compounds, 'assayDefinitions': assayDefinitions, 'projects': projects]
    }

}
