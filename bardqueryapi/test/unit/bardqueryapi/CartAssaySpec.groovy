package bardqueryapi

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import com.metasieve.shoppingcart.ShoppingCartService
import com.metasieve.shoppingcart.Shoppable

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
class CartAssaySpec extends Specification  {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test shopping cart unit"() {
        when:
        CartAssay cartAssay = new CartAssay(assayTitle: "foo")
        assertNotNull(cartAssay)

        then:
        assert cartAssay.assayTitle=='foo'
        assertNull cartAssay.shoppingItem
    }
}
