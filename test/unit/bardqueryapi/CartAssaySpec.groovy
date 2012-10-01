package bardqueryapi

import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
@Unroll
class CartAssaySpec extends Specification {

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
        assert cartAssay.assayTitle == 'Assay title'
        assertNull cartAssay.shoppingItem
    }

    /**
     * constraint test.  Note that the choice of ctor makes a difference in whether the setter is used
     */
    void "test constraints on CartAssay object"() {
        given:
        mockForConstraintsTests(CartAssay)

        final String assayTitle = RandomStringUtils.randomAlphabetic(stringLength)

        when:
        CartAssay cartAssay = new CartAssay(assayTitle, 47 as Long)
        cartAssay.validate()

        then:
        cartAssay.hasErrors() == !valid

        where:
        stringLength | valid
        0            | false
        20           | true
        4000         | true
        40001        | false
    }

    // Note: In this case we are using a setter, and therefore we must NOT mockForConstraintsTests (otherwise
    // the setter will never be hit.
    void "test adding ellipses when the assay title is too long"() {
        given:
        final String assayTitle = RandomStringUtils.randomAlphabetic(stringLength)

        when:
        CartAssay cartAssay = new CartAssay(assayTitle, 47 as Long)
        cartAssay.setAssayTitle(assayTitle)

        then:
        cartAssay.toString().length() == properStringLength

        where:
        compoundId | stringLength | properStringLength
        47         | 4001         | 4003
        47         | 80000        | 4003
        47         | 25           | 25
    }


}
