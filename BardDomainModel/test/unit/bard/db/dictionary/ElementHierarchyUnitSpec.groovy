package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(Element)
class ElementHierarchyUnitSpec extends Specification {
    Element parentElement = new Element(label: 'parentsName')
    Element childElement = new Element(label: 'childsName')
    ArrayList<ElementHierarchy> existingElementHierarchy = [new ElementHierarchy(childElement: childElement,relationshipType:"relationshipType" )]
    ElementHierarchy validElementHierarchy = new ElementHierarchy (childElement: childElement,relationshipType:"relationshipType" )


    private Element createParentElement(){
        new Element(label: 'parentsName')
    }


    @Unroll
    void "test constraints on parentElement"() {
        given:
        mockForConstraintsTests(ElementHierarchy, existingElementHierarchy)

        when:
        ElementHierarchy elementHierarchy = validElementHierarchy
        elementHierarchy.parentElement = name
        elementHierarchy.validate()

        then:
        TestUtils.assertFieldValidationExpectations(elementHierarchy, 'parentElement', valid, errorCode)

        where:
        name                    | valid | errorCode
        null                    | true  | null
        createParentElement()   | true  | null

    }



    @Unroll
    void "test constraints on childElement"() {
        given:
        mockForConstraintsTests(ElementHierarchy, existingElementHierarchy)

        when:
        ElementHierarchy elementHierarchy = validElementHierarchy
        elementHierarchy.childElement = name
        elementHierarchy.validate()

        then:
        TestUtils.assertFieldValidationExpectations(elementHierarchy, 'childElement', valid, errorCode)

        where:
        name                    | valid | errorCode
        null                    | false  | 'nullable'
        createParentElement()   | true  | null

    }



    @Unroll
    void "test constraints on relationshipType"() {
        given:
        mockForConstraintsTests(ElementHierarchy, existingElementHierarchy)

        when:
        ElementHierarchy elementHierarchy = validElementHierarchy
        elementHierarchy.relationshipType = name
        elementHierarchy.validate()

        then:
        TestUtils.assertFieldValidationExpectations(elementHierarchy, 'relationshipType', valid, errorCode)

        where:
        name                    | valid | errorCode
        null                    | false | 'nullable'
        "relationshipType"      | true  | null

    }




    @Unroll
    void "test constraints on dateCreated"() {
        given:
        mockForConstraintsTests(ElementHierarchy, existingElementHierarchy)

        when:
        ElementHierarchy elementHierarchy = validElementHierarchy
        elementHierarchy.dateCreated = name
        elementHierarchy.validate()

        then:
        TestUtils.assertFieldValidationExpectations(elementHierarchy, 'dateCreated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null     // why isn't this false
    }


    @Unroll
    void "test constraints on lastUpdated"() {
        given:
        mockForConstraintsTests(ElementHierarchy, existingElementHierarchy)

        when:
        ElementHierarchy elementHierarchy = validElementHierarchy
        elementHierarchy.lastUpdated = name
        elementHierarchy.validate()

        then:
        TestUtils.assertFieldValidationExpectations(elementHierarchy, 'lastUpdated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null     // why isn't this false
    }


    @Unroll
    void "test constraints on modifiedBy"() {
        given:
        mockForConstraintsTests(ElementHierarchy, existingElementHierarchy)

        when:
        ElementHierarchy elementHierarchy = validElementHierarchy
        elementHierarchy.modifiedBy = name
        elementHierarchy.validate()

        then:
        TestUtils.assertFieldValidationExpectations(elementHierarchy, 'modifiedBy', valid, errorCode)

        where:
        name                    | valid | errorCode
        null                    | true  | null
        TestUtils.createString(40)     | true  | null
        TestUtils.createString(40)+"a" | false | 'maxSize'
        "joe"                   | true  | null
    }





}
