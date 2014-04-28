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
