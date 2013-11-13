package querycart

import bard.db.project.Project
import bard.db.registration.Assay
import bardqueryapi.BardUtilitiesService
import bardqueryapi.InetAddressUtil
import com.metasieve.shoppingcart.ShoppingCartService
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import bardqueryapi.ETagsService
import org.codehaus.groovy.grails.commons.GrailsApplication


@Mixin(InetAddressUtil)
class QueryCartController {

    static final String VIEW_LIST="?view=List"
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService
    BardUtilitiesService bardUtilitiesService
    ETagsService eTagsService
    GrailsApplication grailsApplication  //inject GrailsApplication

    def createCompositeETag() {
        String compositeETag = ""
        final List<Long> cids = queryCartService.retrieveCartCompoundIdsFromShoppingCart()
        final List<Long> pids = queryCartService.retrieveCartProjectIdsFromShoppingCart()
        final List<Long> adids = queryCartService.retrieveCartAssayIdsFromShoppingCart()
        if (cids || pids || adids) {
            compositeETag = eTagsService.createCompositeETags(cids, pids, adids)
        }
        render text: compositeETag, contentType: 'text/plain'
    }
    def toDesktopClient() {
        final String thickClientURL = grailsApplication.config.ncgc.thickclient.root.url
        final String thickClientETagURL = grailsApplication.config.ncgc.thickclient.etags.url

        String compositeETag = ""
        final List<Long> cids = queryCartService.retrieveCartCompoundIdsFromShoppingCart()
        final List<Long> pids = queryCartService.retrieveCartProjectIdsFromShoppingCart()
        final List<Long> adids = queryCartService.retrieveCartAssayIdsFromShoppingCart()
        if (cids || pids || adids) {
            compositeETag = eTagsService.createCompositeETags(cids, pids, adids)
        }
        if (compositeETag){
            redirect(url: thickClientETagURL + compositeETag + VIEW_LIST)
            return
        }
        redirect(url: thickClientURL)
    }
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

        QueryItem item = queryCartService.getQueryItem(id, itemType, name, params.smiles, params.numActive, params.numAssays)
        queryCartService.addToShoppingCart(item)

        return render(status: 200, text: "Added item [${item}] to cart")
    }

    def addItems() {
        def items = JSON.parse(params.items)
        for (def item in items) {
            params.id = item.id
            params.type = item.type
            params.name = item.name
            params.smiles = item.smiles
            params.numActive = item.numActive
            params.numAssays = item.numAssays

            addItem()

            if (response.status != 200) {
                return response
            }
        }
        render(status: 200, text: "Added items [${params.items.dump()}] to cart")
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

        QueryItem item = queryCartService.findQueryItemById(id, itemType)
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
        QueryItem shoppingItem = queryCartService.findQueryItemById(id, itemType)
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
        catch (IllegalArgumentException e) {
            final String errorMessage = "Invalid QueryItemType ${params.type}." + getUserIpAddress(bardUtilitiesService.username)

            log.error(errorMessage, e)
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
        catch (NumberFormatException e) {
            log.error("Invalid ID ${params.id}.  " + getUserIpAddress(bardUtilitiesService.username), e)
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

class CartItemsCommand {
    CartItemDTO[] items
}
class CartItemDTO {
    Long id
    String type
    String name
    String smiles
    Integer numActive
    Integer numAssays
}