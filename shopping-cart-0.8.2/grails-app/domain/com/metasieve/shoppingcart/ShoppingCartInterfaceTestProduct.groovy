package com.metasieve.shoppingcart

class ShoppingCartInterfaceTestProduct implements IShoppable {
	String name
	
	ShoppingItem shoppingItem
	
	String toString() {
		return name
	}
    static mapping = {
        table 'shopcartintertestprod'
    }
}
