package bard.db.dictionary

import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.TestFor

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 7/13/12
 * Time: 10:42 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(Unit)
class UnitSpec   extends Specification {


    ArrayList<Unit> existingUnit = [new Unit(unit : 'unit name', element: createElement(),nodeId : 1L,parentNodeId:1L )]
    Unit validUnit = new Unit(unit : 'unit name', element: createElement(),nodeId : 1L,parentNodeId:1L )

    private Element createElement() {
        new Element(label: 'existingLabel')
    }

    @Unroll
    void "test constraints on unit"() {
        given:
        mockForConstraintsTests(Unit, existingUnit)

        when:
        Unit unit = validUnit
        unit.unit = name
        unit.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unit, 'unit', valid, errorCode)

        where:
        name                              | valid | errorCode
        null                              | false | 'nullable'
        TestUtils.createString(128) + "a" | false | 'maxSize'
        TestUtils.createString(128)       | true  | null
    }



    @Unroll
    void "test constraints on description"() {
        given:
        mockForConstraintsTests(Unit, existingUnit)

        when:
        Unit unit = validUnit
        unit.description = name
        unit.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unit, 'description', valid, errorCode)

        where:
        name                                | valid | errorCode
        null                                | true  | null
        TestUtils.createString(1000)        | true  | null
        TestUtils.createString(1000) + "a"  | false | 'maxSize'
        "foo"                               | true  | null
    }



    @Unroll
    void "test constraints on element"() {
        given:
        mockForConstraintsTests(Unit, existingUnit)

        when:
        Unit unit = validUnit
        unit.element = name
        unit.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unit, 'element', valid, errorCode)

        where:
        name            | valid | errorCode
        null            | false | 'nullable'
        createElement() | true  | null
    }




    @Unroll
    void "test constraints on nodeId"() {
        given:
        mockForConstraintsTests(Unit, existingUnit)

        when:
        Unit unit = validUnit
        unit.nodeId = name
        unit.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unit, 'nodeId', valid, errorCode)

        where:
        name                           | valid | errorCode
        null                           | false | 'nullable'
        47L                            | true  | null

    }





    @Unroll
    void "test constraints on parentNodeId"() {
        given:
        mockForConstraintsTests(Unit, existingUnit)

        when:
        Unit unit = validUnit
        unit.parentNodeId = name
        unit.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unit, 'parentNodeId', valid, errorCode)

        where:
        name                           | valid | errorCode
        null                           | false | 'nullable'
        47L                            | true  | null

    }




}
