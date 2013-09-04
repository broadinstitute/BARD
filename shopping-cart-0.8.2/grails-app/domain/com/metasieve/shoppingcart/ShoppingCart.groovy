package com.metasieve.shoppingcart

class ShoppingCart {
    static hasMany = [items: ShoppingItem]

    String sessionID
    String lastURL

    Boolean checkedOut = false

    static constraints = {
        lastURL(url: true, nullable: true, blank: true)
        sessionID(blank: false)
    }

    static mapping = {
        table('SC_SHOPPING_CART')
        id(generator: 'sequence', params: [sequence: 'SC_SHOPPING_CART_ID_SEQ'])
        items(joinTable: [name: 'SC_SC_SHOPPING_ITEM', key: 'SC_SHOPPING_CART_ID', column: "SC_SHOPPING_ITEM_ID"]    )
    }
}
