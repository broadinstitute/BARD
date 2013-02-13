package com.metasieve.shoppingcart

class Shoppable implements IShoppable {
	ShoppingItem shoppingItem
	
	static constraints = {
		shoppingItem(nullable:true)
	}
}
