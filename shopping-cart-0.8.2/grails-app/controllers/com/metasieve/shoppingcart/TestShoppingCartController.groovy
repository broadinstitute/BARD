package com.metasieve.shoppingcart

class TestShoppingCartController {

	def shoppingCartService
	
    def index = {
		if (ShoppingCartTestProduct.count() == 0) {
			def product1 = new ShoppingCartTestProduct(name:'Test Product 1')
			product1.save()
			def product2 = new ShoppingCartTestProduct(name:'Test Product 2')
			product2.save()
			def product3 = new ShoppingCartInterfaceTestProduct(name:'Test Product 3', shoppingItem:shoppingCartService.getShoppingItem())
			product3.save()
		}
		redirect(action:show, params:params)
	}

    def show = { }

    def add = {
		def product
		if (params.class == 'class+com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct') {
			product = ShoppingCartInterfaceTestProduct.get(params.id)
		} else {
			product = Shoppable.get(params.id)
		}
		if(params.version) {
            def version = params.version.toLong()
            if(product.version > version) {
				product.errors.rejectValue("version", "shoppable.optimistic.locking.failure", message(code:"Shoppable.already.updated"))
			} else {
				product.addToShoppingCart()
			}
		} else {
			product.addToShoppingCart()
		}

		render(template:'shoppingCartContent', plugin:'shoppingCart')
	}

    def remove = {
		def product
		if (params.class == 'class+com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct') {
			product = ShoppingCartInterfaceTestProduct.get(params.id)
		} else {
			product = Shoppable.get(params.id)
		}
		
		if(params.version) {
            def version = params.version.toLong()
            if(product.version > version) {
				product.errors.rejectValue("version", "shoppable.optimistic.locking.failure", message(code:"Shoppable.already.updated"))
			} else {
				product.removeFromShoppingCart()
			}
		} else {
			product.removeFromShoppingCart()
		}
		
		render(template:'shoppingCartContent', plugin:'shoppingCart')
	}
	
	def removeAll = {
		def product
		if (params.class == 'class+com.metasieve.shoppingcart.ShoppingCartInterfaceTestProduct') {
			product = ShoppingCartInterfaceTestProduct.get(params.id)
		} else {
			product = Shoppable.get(params.id)
		}
		
		if(params.version) {
            def version = params.version.toLong()
            if(product.version > version) {
				product.errors.rejectValue("version", "shoppable.optimistic.locking.failure", message(code:"Shoppable.already.updated"))
			} else {
				product.removeQuantityFromShoppingCart(shoppingCartService.getQuantity(product))
			}
		} else {
			product.removeQuantityFromShoppingCart(shoppingCartService.getQuantity(product))
		}
		
		render(template:'shoppingCartContent', plugin:'shoppingCart')
	}
	
	def checkOut = {
		def checkedOutItems = shoppingCartService.checkOut()
		
		render(template:'shoppingCartContent', model:['checkedOutItems':checkedOutItems], plugin:'shoppingCart')
	}
}
