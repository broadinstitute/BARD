package bardwebquery

import querycart.QueryCartService
import querycart.QueryItem
import querycart.QueryItemType


class SaveToCartButtonTagLib {

    QueryCartService queryCartService

    def saveToCartButton = { attrs, body ->
        
        Boolean isInCart = false
        QueryItem item = QueryItem.findByExternalIdAndQueryItemType(attrs.id as Long, attrs.type as QueryItemType)
        if (item) {
            isInCart = queryCartService.isInShoppingCart(item)
        }
        out << render(template: "/tagLibTemplates/saveToCartButton", model: [name: attrs.name, id: attrs.id, type: attrs.type,
                smiles: attrs.smiles, isInCart: isInCart, numAssays: attrs.numAssays, numActive: attrs.numActive])
    }
}