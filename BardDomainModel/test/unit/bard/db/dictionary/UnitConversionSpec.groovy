package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 7/13/12
 * Time: 11:01 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(InstanceDescriptor)
class UnitConversionSpec  extends Specification  {

    ArrayList<UnitConversion> existingUnitConversion = [new UnitConversion(fromUnit: new Unit(), toUnit:new Unit(),  dateCreated: new Date() )]
    UnitConversion validUnitConversion = new UnitConversion(fromUnit: new Unit(), toUnit:new Unit(),  dateCreated: new Date() )


    private Unit createUnit() {
        new Unit(unit : 'unit name', element: createElement(),nodeId : 1L,parentNodeId:1L )
    }

    private Element createElement() {
        new Element(label: 'existingLabel')
    }


    @Unroll
    void "test constraints on multiplier"() {
        given:
        mockForConstraintsTests(UnitConversion, existingUnitConversion)

        when:
        UnitConversion unitConversion = validUnitConversion
        unitConversion.multiplier = name
        unitConversion.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unitConversion, 'multiplier', valid, errorCode)

        where:
        name               | valid | errorCode
        null               | true  | null
        4.7f               | true  | null
    }



    @Unroll
    void "test constraints on offset"() {
        given:
        mockForConstraintsTests(UnitConversion, existingUnitConversion)

        when:
        UnitConversion unitConversion = validUnitConversion
        unitConversion.offset = name
        unitConversion.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unitConversion, 'offset', valid, errorCode)

        where:
        name               | valid | errorCode
        null               | true  | null
        4.7f               | true  | null
    }



    @Unroll
    void "test constraints on Formula"() {
        given:
        mockForConstraintsTests(UnitConversion, existingUnitConversion)

        when:
        UnitConversion unitConversion = validUnitConversion
        unitConversion.formula = name
        unitConversion.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unitConversion, 'formula', valid, errorCode)

        where:
        name                                | valid | errorCode
        null                                | true  | null
        TestUtils.createString(256) + "a"   | false | 'maxSize'
        TestUtils.createString(256)         | true  | null
        "Some formula"                      | true  | null
    }



    @Unroll
    void "test constraints on dateCreated"() {
        given:
        mockForConstraintsTests(UnitConversion, existingUnitConversion)

        when:
        UnitConversion unitConversion = validUnitConversion
        unitConversion.dateCreated = name
        unitConversion.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unitConversion, 'dateCreated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null
    }


    @Unroll
    void "test constraints on lastUpdated"() {
        given:
        mockForConstraintsTests(UnitConversion, existingUnitConversion)

        when:
        UnitConversion unitConversion = validUnitConversion
        unitConversion.lastUpdated = name
        unitConversion.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unitConversion, 'lastUpdated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null
    }





    @Unroll
    void "test constraints on modifiedBy"() {
        given:
        mockForConstraintsTests(UnitConversion, existingUnitConversion)

        when:
        UnitConversion unitConversion = validUnitConversion
        unitConversion.modifiedBy = name
        unitConversion.validate()

        then:
        TestUtils.assertFieldValidationExpectations(unitConversion, 'modifiedBy', valid, errorCode)

        where:
        name                                | valid | errorCode
        null                                | true  | null
        TestUtils.createString(40) + "a"    | false | 'maxSize'
        TestUtils.createString(40)          | true  | null
        "joe"                               | true  | null
    }




}
