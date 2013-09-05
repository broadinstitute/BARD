package com.metasieve.shoppingcart

class ShoppingCartTagLib {
	static namespace = "sc"
	
	def shoppingCartService
	
	def each = { attrs, body ->
		def items = shoppingCartService.getItems()
				
		items?.sort { a, b -> a.id <=> b.id }.each { item ->
			def itemInfo = ['item':item,
							'qty':shoppingCartService.getQuantity(item)]

			out << body(itemInfo)
		}
	}
}
