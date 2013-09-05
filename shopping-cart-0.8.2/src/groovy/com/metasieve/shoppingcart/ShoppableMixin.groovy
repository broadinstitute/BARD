package com.metasieve.shoppingcart

class ShoppableMixin {
    static mixin() {
		def add = { shoppableDelegate, qty, previousSessionID ->
			if (!shoppableDelegate.shoppingItem) {
				def shoppingItem = new ShoppingItem()
				shoppingItem.save()
				shoppableDelegate.shoppingItem = shoppingItem
				shoppableDelegate.save()
			}
			
			def sessionID = previousSessionID
			if (!sessionID) {
				sessionID = SessionUtils.getSession().id
			}
			def shoppingCart = ShoppingCart.findBySessionIDAndCheckedOut(sessionID, false)

			if (!shoppingCart) {
				shoppingCart = new ShoppingCart(sessionID:sessionID)
				shoppingCart.save()
			}

			def quantity = Quantity.findByShoppingCartAndShoppingItem(shoppingCart, shoppableDelegate.shoppingItem)
			if (quantity) {
				quantity.value += qty
			} else {
				shoppingCart.addToItems(shoppableDelegate.shoppingItem)
				quantity = new Quantity(shoppingCart:shoppingCart, shoppingItem:shoppableDelegate.shoppingItem, value:qty)
			}
			quantity.save()
			
			shoppingCart.save()
		}
		
		def remove = { shoppableDelegate, qty, previousSessionID ->
			def sessionID = previousSessionID
			if (!sessionID) {
				sessionID = SessionUtils.getSession().id
			}
			def shoppingCart = ShoppingCart.findBySessionIDAndCheckedOut(sessionID, false)
			
			if (!shoppingCart) {
				return
			}
			
			def quantity = Quantity.findByShoppingCartAndShoppingItem(shoppingCart, shoppableDelegate.shoppingItem)
			if (quantity) {
				if (quantity.value - qty >= 0) {
					quantity.value -= qty
				}
				quantity.save()
			}
			
			if (quantity.value == 0) {
				// work-around for $$_javassist types in list
				def itemToRemove = shoppingCart.items.find { item ->
					if (item.id == shoppableDelegate.shoppingItem.id) {
						return true
					}
					return false
				}
				shoppingCart.removeFromItems(itemToRemove)
				quantity.delete()
			}

			shoppingCart.save()
		}

        IShoppable.metaClass.addToShoppingCart = { ->
			add(delegate, 1, null)
        }

        IShoppable.metaClass.removeFromShoppingCart = { ->
			remove(delegate, 1, null)
        }

        IShoppable.metaClass.addQuantityToShoppingCart = { qty ->
			add(delegate, qty, null)
        }

        IShoppable.metaClass.removeQuantityFromShoppingCart = { qty ->
			remove(delegate, qty, null)
        }

        IShoppable.metaClass.addToShoppingCartWithSessionID = { previousSessionID ->
			add(delegate, 1, previousSessionID)
        }

        IShoppable.metaClass.removeFromShoppingCartWithSessionID = { previousSessionID ->
			remove(delegate, 1, previousSessionID)
        }

        IShoppable.metaClass.addQuantityToShoppingCartWithSessionID = { qty, previousSessionID ->
			add(delegate, qty, previousSessionID)
        }

        IShoppable.metaClass.removeQuantityFromShoppingCartWithSessionID = { qty, previousSessionID ->
			remove(delegate, qty, previousSessionID)
        }
    }
}
