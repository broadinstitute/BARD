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

package bardwebquery

import com.metasieve.shoppingcart.Shoppable
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import querycart.CartAssay
import querycart.QueryCartService
import querycart.QueryItem
import querycart.QueryItemType
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Tests for SaveToCartButtonTagLib
 */
@TestFor(SaveToCartButtonTagLib)
@Unroll
@Mock([QueryItem,CartAssay,Shoppable])
class SaveToCartButtonTagLibSpec extends Specification {

    QueryCartService queryCartService

    void setup() {
        queryCartService = Mock(QueryCartService)
        tagLib.queryCartService = queryCartService
    }

    void "test for assay definition #label"() {
        given:
        def template = '<g:saveToCartButton id="${id}" ' +
                'name="${title}" ' +
                'type="'+QueryItemType.AssayDefinition.name()+'" hideLabel="${hideLabel}"/>'

        QueryItem queryItem = new CartAssay(title, id, id)
        assert queryItem.save()

        when:
        String actualResults = applyTemplate(template, [id: id, title: title, hideLabel: hideLabel]).toString()

        then:
        1 * queryCartService.isInShoppingCart(queryItem) >> {isInCart}

        if (!hideLabel) {
            assert actualResults.contains('<label class="checkbox">')
            assert actualResults.contains('Save to Cart for analysis')
        }
        else {
            assert !actualResults.contains('<label class="checkbox">')
            assert !actualResults.contains('Save to Cart for analysis')
        }
        assert actualResults.contains('input type="checkbox" name="saveToCart"')
        assert actualResults.contains('class="addToCartCheckbox"')
        assert actualResults.contains("data-cart-name=\"${title}\"")
        assert actualResults.contains('data-cart-type="'+QueryItemType.AssayDefinition.name()+'"')
        assert actualResults.contains("data-cart-id=\"${id}\"")
        if (isInCart) {
            assert actualResults.contains("checked=\"checked\"")
        }

        where:
        label            | id | title          | isInCart | hideLabel
        "in cart"        | 1  | 'Test Title'   | true     | false
        "not in cart"    | 2  | 'Test Title'   | false    | false
        "no label"       | 2  | 'Test Title'   | true     | true
    }

}
