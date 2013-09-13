package bardwebquery

import querycart.QueryCartService
import querycart.QueryItem
import querycart.QueryItemType


class SaveToCartButtonTagLib {

    QueryCartService queryCartService

    def saveToCartButton = { attrs, body ->
        
        Boolean isInCart = false

        QueryItemType queryItemType = attrs.type as QueryItemType
        QueryItem item
        if(queryItemType == QueryItemType.Compound) {
            item = QueryItem.findByExternalIdAndQueryItemType(attrs.id as Long, queryItemType)
        } else {
            item = QueryItem.findByInternalIdAndQueryItemType(attrs.id as Long, queryItemType)
        }

        if (item) {
            isInCart = queryCartService.isInShoppingCart(item)
        }

        out << render(template: "/tagLibTemplates/saveToCartButton", model: [name: attrs.name, id: attrs.id, type: attrs.type,
                smiles: attrs.smiles, isInCart: isInCart, numAssays: attrs.numAssays, numActive: attrs.numActive, hideLabel: attrs.hideLabel])
    }
}