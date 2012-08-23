package bardqueryapi

import grails.test.mixin.TestFor
import spock.lang.Specification

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
