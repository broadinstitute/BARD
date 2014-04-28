package com.metasieve.shoppingcart

class ShoppingCartInterfaceTestProduct implements IShoppable {
	String name
	
	ShoppingItem shoppingItem
	
	String toString() {
		return name
	}
    static mapping = {
        table 'SC_INTERFACE_TEST_PROD'
        id(generator: 'sequence', params: [sequence: 'SC_INTERFACE_TEST_PROD_ID_SEQ'])
    }
}
