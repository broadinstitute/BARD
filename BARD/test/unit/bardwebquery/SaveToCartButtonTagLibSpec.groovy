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