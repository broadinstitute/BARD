package com.metasieve.shoppingcart

class Shoppable implements IShoppable {
	ShoppingItem shoppingItem
	
	static constraints = {
		shoppingItem(nullable:true)
	}

    static mapping = {
        id(generator: 'sequence', params: [sequence: 'SHOPPABLE_ID_SEQ'])
    }
}
