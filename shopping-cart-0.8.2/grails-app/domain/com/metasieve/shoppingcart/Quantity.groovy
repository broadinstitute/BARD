package com.metasieve.shoppingcart

class Quantity {
	static belongsTo = [shoppingCart:ShoppingCart, shoppingItem:ShoppingItem]
	
	Integer value = 0
	
	String toString() {
		return "${value}"
	}
}
