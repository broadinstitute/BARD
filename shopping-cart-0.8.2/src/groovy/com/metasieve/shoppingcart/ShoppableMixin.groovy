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
