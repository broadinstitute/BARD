package querycart

import grails.test.mixin.TestFor
import org.apache.commons.lang.RandomStringUtils
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartAssay)
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
        assert cartAssay.name == 'Assay title'
        assert cartAssay.externalId == assayId
    }

    void "test toString #label"() {

        when:
        final String assayAsString = cartAssay.toString()
        then:
        assert assayAsString == expectedTitle
        where:
        label                  | cartAssay                        | expectedTitle
        "Empty Title"          | new CartAssay()                  | ""
        "Null String as title" | new CartAssay("null", 100)       | ""
        "With title"           | new CartAssay("Some Title", 110) | "Some Title"
    }

    void "Test equals #label"() {
        when:
        final boolean equals = cartAssay.equals(otherCartAssay)

        then:
        equals == equality
        where:
        label               | cartAssay                        | otherCartAssay                   | equality
        "Other is null"     | new CartAssay()                  | null                             | false
        "Different classes" | new CartAssay()                  | 20                               | false
        "Equality"          | new CartAssay("Some Title", 120) | new CartAssay("Some Title", 120) | true


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
        "Equality"          | new CartAssay("Some Title", 50)


    }

    void "test shopping cart assay element"() {
        when:
        CartAssay cartAssay = new CartAssay("Assay title", 50)
        assertNotNull(cartAssay)

        then:
        assert cartAssay.name == 'Assay title'
        assertNull cartAssay.shoppingItem
    }

    /**
     * constraint test.
     */
    void "test constraints on CartAssay object with title length = #stringLength"() {
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
        40001        | true
    }

    void "test adding ellipses when the assay title is too long"() {
        given:
        mockForConstraintsTests(CartAssay)
        final String assayTitle = RandomStringUtils.randomAlphabetic(stringLength)

        when:
        CartAssay cartAssay = new CartAssay(assayTitle, assayId)
        cartAssay.validate()

        then:
        cartAssay.toString().length() == properStringLength

        where:
        assayId | stringLength | properStringLength
        47      | 4001         | 4000
        47      | 80000        | 4000
        47      | 25           | 25
        2       | 0            | 0
    }


}
