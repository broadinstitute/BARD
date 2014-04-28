package com.metasieve.shoppingcart

class Quantity {
	static belongsTo = [shoppingCart:ShoppingCart, shoppingItem:ShoppingItem]
	
	Integer value = 0
	
	String toString() {
		return "${value}"
	}

    static mapping = {
        table(name:'SC_QUANTITY')
        id(generator: 'sequence', params: [sequence: 'SC_QUANTITY_ID_SEQ'])
    }
}
