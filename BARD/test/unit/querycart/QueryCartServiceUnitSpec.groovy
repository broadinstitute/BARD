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

package querycart

import com.metasieve.shoppingcart.ShoppingCartService
import com.metasieve.shoppingcart.ShoppingItem
import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryCartService)
class QueryCartServiceUnitSpec extends Specification {
    ShoppingCartService shoppingCartService

    void setup() {

        this.shoppingCartService = Mock(ShoppingCartService)
        service.shoppingCartService = this.shoppingCartService

    }

    void "test totalNumberOfUniqueItemsInCart #label"() {
        when:
        int uniqueItems = service.totalNumberOfUniqueItemsInCart(this.shoppingCartService)
        then:
        1 * service.shoppingCartService.items >> {shoppingItems}
        assert uniqueItems == expectedNumberOfUniqueItems
        where:
        label                    | expectedNumberOfUniqueItems | shoppingItems
        "One Item in Cart"       | 1                           | [new ShoppingItem()]
        "Empty Cart"             | 0                           | []
        "Empty Cart, Null Items" | 0                           | null

    }

    void "test totalNumberOfUniqueItemsInCart with Map #label"() {

        when:
        int uniqueItems = service.totalNumberOfUniqueItemsInCart(mapOfUniqueItems, elementType)
        then:
        assert uniqueItems == expectedNumberOfUniqueItems
        where:
        label                                                               | expectedNumberOfUniqueItems | mapOfUniqueItems    | elementType
        "Empty Cart No Element Type"                                        | 0                           | [:]                 | ""
        "Empty Cart with Element Type"                                      | 0                           | [:]                 | "SomeElementType"
        "Single Item in Cart with no Element Type"                          | 2                           | [a: ["c", "d"]]     | ""
        "Single Item in Cart with Element Type in Map"                      | 2                           | [a: ["c", "d"]]     | "a"
        "Single Item in Cart with Element Type in Map with null values"     | 1                           | [a: null, c: ["x"]] | ""
        "Single Item in Cart with Element Type Not in Map"                  | 0                           | [a: ["c", "d"]]     | "z"
        "Single Item in Cart with Element Type Not in Map with null values" | 0                           | [a: null]           | "z"


    }

    void "test removeFromShoppingCart"() {
        when:
        service.removeFromShoppingCart(this.shoppingCartService, new CartProject())
        then:
        1 * shoppingCartService.removeFromShoppingCart(_) >> {}

    }

    void "test emptyShoppingCart"() {
        when:
        service.emptyShoppingCart(this.shoppingCartService)
        then:
        1 * shoppingCartService?.emptyShoppingCart() >> {}

    }

}
