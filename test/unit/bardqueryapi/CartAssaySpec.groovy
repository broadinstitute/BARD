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

    void "test shopping cart assay element"() {
        when:
            CartAssay cartAssay = new CartAssay(assayTitle: "Assay title")
            assertNotNull(cartAssay)

        then:
            assert cartAssay.assayTitle=='Assay title'
            assertNull cartAssay.shoppingItem
    }



    void "test constraints on CartAssay object"() {
        setup:
        mockForConstraintsTests(CartAssay)

        when:
        CartAssay cartAssay = new CartAssay(assayTitle: assayTitle)
        cartAssay.validate()

        then:
        cartAssay.hasErrors() == !valid

        where:
        assayTitle      |   valid
        ""              |   false
        "Some assay"    |   true
    }


}
