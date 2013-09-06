package com.metasieve.shoppingcart

class ShoppingItem {

    static mapping = {
        table('SC_SHOPPING_ITEM')
        id(generator: 'sequence', params: [sequence: 'SC_SHOPPING_ITEM_ID_SEQ'])
    }

}
