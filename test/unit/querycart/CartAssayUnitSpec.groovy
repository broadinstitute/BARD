package querycart

import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll
import querycart.CartAssay
import bardqueryapi.BardWebInterfaceController

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
@Unroll
class CartAssayUnitSpec extends Specification {

    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test constructor with integer"() {
        given:
        int assayId = 2
        when:
        CartAssay cartAssay = new CartAssay("Assay title", assayId)

        then:
        assert cartAssay.assayTitle == 'Assay title'
        assert cartAssay.assayId
    }

    void "test toString"() {

        when:
        final String assayAsString = cartAssay.toString()
        then:
        assert assayAsString == expectedTitle
        where:
        label                  | cartAssay                               | expectedTitle
        "Empty Title"          | new CartAssay()                         | ""
        "Null String as title" | new CartAssay(assayTitle: "null")       | ""
        "With title"           | new CartAssay(assayTitle: "Some Title") | "Some Title"
    }

    void "Test equals #label"() {
        when:
        final boolean equals = cartAssay.equals(otherCartAssay)

        then:
        equals == equality
        where:
        label               | cartAssay                               | otherCartAssay                          | equality
        "Other is null"     | new CartAssay()                         | null                                    | false
        "Different classes" | new CartAssay()                         | 20                                      | false
        "Equality"          | new CartAssay(assayTitle: "Some Title") | new CartAssay(assayTitle: "Some Title") | true


    }

    void "Test hashCode #label"() {
        when:
        final int code = cartAssay.hashCode()

        then:
        assert code
        where:
        label               | cartAssay
        "Other is null"     | new CartAssay()
        "Different classes" | new CartAssay()
        "Equality"          | new CartAssay(assayTitle: "Some Title")


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
        CartAssay cartAssay = new CartAssay(assayTitle, assayId)

        when:
        cartAssay.setAssayTitle(assayTitle)

        then:
        cartAssay.toString().length() == properStringLength

        where:
        assayId | stringLength | properStringLength
        47      | 4001         | 4003
        47      | 80000        | 4003
        47      | 25           | 25
        2       | 0            | 0
    }


}
