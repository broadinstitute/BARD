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
