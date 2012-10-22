package bardwebquery

import querycart.QueryCartService


class SaveToCartButtonTagLib {

    QueryCartService queryCartService

    def saveToCartButton = { attrs, body ->
        
        Boolean isInCart = queryCartService.isInShoppingCart()
        out << render(template: "saveToCartButton", model: [name: attrs.name, id: attrs.id, isInCart: isInCart])
    }
}