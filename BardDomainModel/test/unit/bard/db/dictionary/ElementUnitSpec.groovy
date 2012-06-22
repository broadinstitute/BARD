package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/21/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(Element)
class ElementUnitSpec extends Specification {

    def existingElement = [new Element(label: 'existingName')]
    def validElement = new Element (label:'name',elementStatus:'Pending',readyForExtraction:'Ready')

    @Unroll
    void "test required non blank name"() {
        given:
        mockForConstraintsTests(Element, existingElement)
        //129.times{longString += "a" }

        when:
        def element = validElement
        element.label = name
        element.validate()

        then:
        UnitSpecUtils.assertFieldValidationExpectations(element, 'name', valid, errorCode)

        where:
        name           | valid | errorCode
        "foo"          | true  | null

    }
}

class UnitSpecUtils {

    /**
     * With the spock data driven constraints tests the then: block was looking the same over all the domain classes.
     *
     * So just moving this to a single spot.
     * @param domainObject the Domain instance object under test
     * @param fieldName the name of the field we're looking to test
     * @param valid  true if the domainObject is expected to be pass validation, false otherwise
     * @param errorCode  the expected code for the constraint that caused the domainObject to fail validation
     */
    static void assertFieldValidationExpectations(Object domainObject, fieldName, Boolean valid, String errorCode) {
        //println("domainObject.errors: ${domainObject.hasErrors()}" )
        //println(domainObject.dump())
        assert domainObject.errors[fieldName] == errorCode
        assert domainObject.hasErrors() == !valid
        assert domainObject.errors.hasFieldErrors(fieldName) == !valid
    }

}
