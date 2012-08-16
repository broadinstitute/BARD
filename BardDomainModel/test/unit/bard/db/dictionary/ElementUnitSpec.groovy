package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/21/12
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(Element)
class ElementUnitSpec extends Specification {

    ArrayList<Element> existingElement = [new Element(label: 'existingName')]
    Element validElement = new Element(label: 'name', elementStatus: 'Pending', readyForExtraction: 'Ready')

    private Unit createUnit() {
        new Unit()
    }


    @Unroll
    void "test constraints on label"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.label = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'label', valid, errorCode)

        where:
        name                       | valid | errorCode
        TestUtils.createString(129)       | true  | null
        TestUtils.createString(129) + "a" | false | 'maxSize'
        null                       | false | 'nullable'
        "foo"                      | true  | null

    }


    @Unroll
    void "test constraints on description"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.description = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'description', valid, errorCode)

        where:
        name                        | valid | errorCode
        null                        | true  | null
        TestUtils.createString(1000)       | true  | null
        TestUtils.createString(1000) + "a" | false | 'maxSize'
        "foo"                       | true  | null
    }





    @Unroll
    void "test constraints on abbreviation"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.abbreviation = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'abbreviation', valid, errorCode)

        where:
        name                                            | valid | errorCode
        null                                            | true  | null
        TestUtils.createString(20)        | true  | null
        TestUtils.createString(20) + "1" | false | 'maxSize'
        "foo"                                           | true  | null
    }

    @Unroll
    void "test constraints on synonyms"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.synonyms = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'synonyms', valid, errorCode)

        where:
        name                        | valid | errorCode
        null                        | true  | null
        TestUtils.createString(1000)       | true  | null
        TestUtils.createString(1000) + "a" | false | 'maxSize'
        "foo"                       | true  | null
    }


    @Unroll
    void "test constraints on externalURL"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.externalURL = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'externalURL', valid, errorCode)

        where:
        name                        | valid | errorCode
        null                        | true  | null
        TestUtils.createString(1000)       | true  | null
        TestUtils.createString(1000) + "a" | false | 'maxSize'
        "foo"                       | true  | null
    }

    @Unroll
    void "test constraints on dateCreated"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.dateCreated = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'dateCreated', valid, errorCode)

        where:
        name       | valid | errorCode
        new Date() | true  | null
        null       | true  | null     // why isn't this false
    }


    @Unroll
    void "test constraints on lastUpdated"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.lastUpdated = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'lastUpdated', valid, errorCode)

        where:
        name       | valid | errorCode
        new Date() | true  | null
        null       | true  | null     // why isn't this false
    }


    @Unroll
    void "test constraints on modifiedBy"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.modifiedBy = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'modifiedBy', valid, errorCode)

        where:
        name                             | valid | errorCode
        null                             | true  | null
        TestUtils.createString(40)       | true  | null
        TestUtils.createString(40) + "a" | false | 'maxSize'
        "joe"                            | true  | null
    }



    @Unroll
    void "test constraints on unit"() {
        given:
        mockForConstraintsTests(Element, existingElement)


        when:
        Element element = validElement
        element.unit = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'modifiedBy', valid, errorCode)

        where:
        name         | valid | errorCode
        null         | true  | null
        createUnit() | true  | null
    }


    @Unroll
    void "test constraints on elementStatus"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.elementStatus = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'elementStatus', valid, errorCode)

        where:
        name         | valid | errorCode
        "Pending"    | true  | null
        "Published"  | true  | null
        "Deprecated" | true  | null
        "Retired"    | true  | null
        "foo"        | false | 'inList'
    }



    @Unroll
    void "test constraints on readyForExtraction"() {
        given:
        mockForConstraintsTests(Element, existingElement)

        when:
        Element element = validElement
        element.readyForExtraction = name
        element.validate()

        then:
        TestUtils.assertFieldValidationExpectations(element, 'readyForExtraction', valid, errorCode)

        where:
        name       | valid | errorCode
        "Ready"    | true  | null
        "Started"  | true  | null
        "Complete" | true  | null
        "foo"      | false | 'inList'
    }


}
