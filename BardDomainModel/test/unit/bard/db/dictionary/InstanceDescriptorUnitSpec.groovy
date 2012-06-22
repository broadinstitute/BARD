package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(InstanceDescriptor)

class InstanceDescriptorUnitSpec  extends Specification {

    ArrayList<InstanceDescriptor> existingInstanceDescriptor = [new InstanceDescriptor(label: 'existingLabel')]
    InstanceDescriptor validInstanceDescriptor = new InstanceDescriptor (element: createElement(), label:'name',elementStatus:'Pending')

    private InstanceDescriptor createInstanceDescriptor(){
        new InstanceDescriptor(label: 'existingLabel')
    }

    private Element createElement(){
        new Element(label: 'existingLabel')
    }

    private Unit createUnit(){
        new Unit()
    }



    @Unroll
    void "test constraints on parents"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.parent = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'parent', valid, errorCode)

        where:
        name                      | valid | errorCode
        null                      | true  | null
        createInstanceDescriptor()| true  | null
    }



    @Unroll
    void "test constraints on element"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.element = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'element', valid, errorCode)

        where:
        name                      | valid | errorCode
        null                      | false  | 'nullable'
        createElement()           | true  | null
    }



    @Unroll
    void "test constraints on label"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.label = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'label', valid, errorCode)

        where:
        name                      | valid | errorCode
        null                      | false | 'nullable'
        TestUtils.lString128+"a"  | false | 'maxSize'
        TestUtils.lString128      | true  | null
    }



    @Unroll
    void "test constraints on description"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.description = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'description', valid, errorCode)

        where:
        name                     | valid | errorCode
        null                     | true  | null
        TestUtils.lString1000    | true  | null
        TestUtils.lString1000+"a"| false | 'maxSize'
        "foo"                    | true  | null
    }



    @Unroll
    void "test constraints on abbreviation"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.abbreviation = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'abbreviation', valid, errorCode)

        where:
        name                                        | valid | errorCode
        null                                        | true  | null
        TestUtils.lString10+TestUtils.lString10     | true | null
        TestUtils.lString10+TestUtils.lString10+"1" | false | 'maxSize'
        "foo"                                       | true  | null
    }

    @Unroll
    void "test constraints on synonyms"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.synonyms = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'synonyms', valid, errorCode)

        where:
        name                     | valid | errorCode
        null                     | true  | null
        TestUtils.lString1000    | true  | null
        TestUtils.lString1000+"a"| false | 'maxSize'
        "foo"                    | true  | null
    }

    @Unroll
    void "test constraints on externalURL"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.externalURL = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'externalURL', valid, errorCode)

        where:
        name                     | valid | errorCode
        null                     | true  | null
        TestUtils.lString1000    | true  | null
        TestUtils.lString1000+"a"| false | 'maxSize'
        "foo"                    | true  | null
    }



    @Unroll
    void "test constraints on unit"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.externalURL = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'unit', valid, errorCode)

        where:
        name                    | valid | errorCode
        null                    | true  | null
        createUnit()            | true  | null
    }


    @Unroll
    void "test constraints on elementStatus"() {
        given:
        mockForConstraintsTests(InstanceDescriptor, existingInstanceDescriptor)

        when:
        InstanceDescriptor instanceDescriptor = validInstanceDescriptor
        instanceDescriptor.elementStatus = name
        instanceDescriptor.validate()

        then:
        TestUtils.assertFieldValidationExpectations(instanceDescriptor, 'elementStatus', valid, errorCode)

        where:
        name                     | valid | errorCode
        "Pending"                | true  | null
        "Published"              | true  | null
        "Deprecated"             | true  | null
        "Retired"                | true  | null
        "foo"                    | false  | 'inList'
    }



}
