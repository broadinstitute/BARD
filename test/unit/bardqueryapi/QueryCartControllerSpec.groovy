package bardqueryapi

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Unroll
import spock.lang.Specification
import com.metasieve.shoppingcart.ShoppingCartService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(QueryCartController)
@Unroll
class QueryCartControllerSpec extends Specification  {
    ShoppingCartService shoppingCartService
    QueryCartService queryCartService

    void setup() {
        this.shoppingCartService = Mock(ShoppingCartService)
        this.queryCartService = Mock(QueryCartService)
        controller.metaClass.mixin(CartAssay)
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test add"() {
        given:
        params.stt="1"
        params.class = 'class bardqueryapi.Fake'
        params.assayTitle = 'my assay'
        params.id = "2"

        when:
        controller.add()

        then:
        _ * this.queryCartService.addToShoppingCart(_, _) >> {null}
        assert true
    }
}
