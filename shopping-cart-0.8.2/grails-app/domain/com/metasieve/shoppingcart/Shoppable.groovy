package com.metasieve.shoppingcart

class Shoppable implements IShoppable {
	ShoppingItem shoppingItem
	
	static constraints = {
		shoppingItem(nullable:true)
	}

    static mapping = {
        table('SC_SHOPPABLE')
        id(generator: 'sequence', params: [sequence: 'SC_SHOPPABLE_ID_SEQ'])
    }
}
