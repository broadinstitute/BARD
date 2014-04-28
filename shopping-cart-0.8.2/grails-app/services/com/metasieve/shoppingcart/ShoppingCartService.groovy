/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
