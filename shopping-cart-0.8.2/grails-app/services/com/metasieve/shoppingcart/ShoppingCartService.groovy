package com.metasieve.shoppingcart

class ShoppingCartService {

    boolean transactional = true

    def createShoppingCart() {
		def sessionID = SessionUtils.getSession().id

		def shoppingCart = new ShoppingCart(sessionID:sessionID)
		shoppingCart.save()
		
		return shoppingCart
    }

    def addToShoppingCart(IShoppable product, Integer qty = 1, ShoppingCart previousShoppingCart = null) {
		if (!product.shoppingItem) {
			def shoppingItem = new ShoppingItem()
			shoppingItem.save()
			product.shoppingItem = shoppingItem
			product.save()
		}
		
		def shoppingCart = getShoppingCart()

		def quantity = Quantity.findByShoppingCartAndShoppingItem(shoppingCart, product.shoppingItem)
		if (quantity) {
			quantity.value += qty
		} else {
			shoppingCart.addToItems(product.shoppingItem)
			quantity = new Quantity(shoppingCart:shoppingCart, shoppingItem:product.shoppingItem, value:qty)
		}
		quantity.save()
		
		shoppingCart.save()
    }

    def removeFromShoppingCart(IShoppable product, Integer qty = 1, ShoppingCart previousShoppingCart = null) {
		def shoppingCart = getShoppingCart()
		
		if (!shoppingCart) {
			return
		}
		
		def quantity = Quantity.findByShoppingCartAndShoppingItem(shoppingCart, product.shoppingItem)
		if (quantity) {
			if (quantity.value - qty >= 0) {
				quantity.value -= qty
			}
			quantity.save()
		}
		
		if (quantity.value == 0) {
			// work-around for $$_javassist types in list
			def itemToRemove = shoppingCart.items.find { item ->
				if (item.id == product.shoppingItem.id) {
					return true
				}
				return false
			}
			shoppingCart.removeFromItems(itemToRemove)
			quantity.delete()
		}
		
		shoppingCart.save()
    }

    def getQuantity(IShoppable product, ShoppingCart previousShoppingCart = null) {
		def shoppingCart = getShoppingCart()
		def quantity = Quantity.findByShoppingCartAndShoppingItem(shoppingCart, product.shoppingItem)
		
		return quantity?.value
    }

    def getQuantity(ShoppingItem shoppingItem, ShoppingCart previousShoppingCart = null) {
		def shoppingCart = getShoppingCart()
		def quantity = Quantity.findByShoppingCartAndShoppingItem(shoppingCart, shoppingItem)
		
		return quantity?.value
    }

    def setLastURL(def url, ShoppingCart previousShoppingCart = null) {
		def shoppingCart = getShoppingCart()
		shoppingCart.lastURL = url
		shoppingCart.save()
    }

    def emptyShoppingCart(ShoppingCart previousShoppingCart = null) {
		def shoppingCart = getShoppingCart()
		shoppingCart.items = []
		
		def quantities = Quantity.findAllByShoppingCart(shoppingCart)
		quantities.each { quantity -> quantity.delete() }
		
		shoppingCart.save()
    }

    Set getItems(ShoppingCart previousShoppingCart = null) {
		def shoppingCart = getShoppingCart()
		return shoppingCart.items
    }

    Set checkOut(ShoppingCart previousShoppingCart = null) {
		def shoppingCart = getShoppingCart()
		
		def checkedOutItems = []
		shoppingCart.items.each { item ->
			def checkedOutItem = [:]
			checkedOutItem['item'] = item
			checkedOutItem['qty'] = getQuantity(item)
			checkedOutItems.add(checkedOutItem)
		}
		
		shoppingCart.checkedOut = true
		shoppingCart.save()
		
		return checkedOutItems
    }

	def getShoppingCart(def previousSessionID = null) {
		def sessionID = previousSessionID
		if (!sessionID) {
			sessionID = SessionUtils.getSession().id
		}
		
		def shoppingCart = ShoppingCart.findBySessionIDAndCheckedOut(sessionID, false)
		
		if (!shoppingCart) {
			shoppingCart = createShoppingCart()
		}
		
		return shoppingCart
	}
	
	def getShoppingItem() {
		def shoppingItem = new ShoppingItem()
		shoppingItem.save()
		return shoppingItem
	}
}
