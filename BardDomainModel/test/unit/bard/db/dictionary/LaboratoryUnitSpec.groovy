package bard.db.dictionary

import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.TestFor
import test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(Laboratory)
class LaboratoryUnitSpec   extends Specification  {

    ArrayList<Laboratory> existingLaboratory = [new Laboratory(element: createElement(), laboratory: 'nameOfLaboratory')]
    Laboratory validLaboratory = new Laboratory (element: createElement(), laboratory: 'nameOfLaboratory')

    private Element createElement(){
        new Element(label: 'existingLabel')
    }

    private Laboratory createLaboratory(){
        new Laboratory(element: createElement(), laboratory: 'nameOfLaboratory')
    }



    @Unroll
    void "test constraints on laboratory"() {
        given:
        mockForConstraintsTests(Laboratory, existingLaboratory)

        when:
        Laboratory laboratory = validLaboratory
        laboratory.laboratory = name
        laboratory.validate()

        then:
        TestUtils.assertFieldValidationExpectations(laboratory, 'laboratory', valid, errorCode)

        where:
        name                      | valid | errorCode
        null                      | false | 'nullable'
        TestUtils.createString(128)+"a"  | false | 'maxSize'
        TestUtils.createString(128)      | true  | null
    }



    @Unroll
    void "test constraints on element"() {
        given:
        mockForConstraintsTests(Laboratory, existingLaboratory)

        when:
        Laboratory laboratory = validLaboratory
        laboratory.element = name
        laboratory.validate()

        then:
        TestUtils.assertFieldValidationExpectations(laboratory, 'element', valid, errorCode)

        where:
        name                      | valid | errorCode
        null                      | false | 'nullable'
        createElement()           | true  | null
    }



    @Unroll
    void "test constraints on description"() {
        given:
        mockForConstraintsTests(Laboratory, existingLaboratory)

        when:
        Laboratory laboratory = validLaboratory
        laboratory.description = name
        laboratory.validate()

        then:
        TestUtils.assertFieldValidationExpectations(laboratory, 'description', valid, errorCode)

        where:
        name                     | valid | errorCode
        null                     | true  | null
        TestUtils.createString(1000)    | true  | null
        TestUtils.createString(1000)+"a"| false | 'maxSize'
        "foo"                    | true  | null
    }





    @Unroll
    void "test constraints on parent"() {
        given:
        mockForConstraintsTests(Laboratory, existingLaboratory)

        when:
        Laboratory laboratory = validLaboratory
        laboratory.parent = name
        laboratory.validate()

        then:
        TestUtils.assertFieldValidationExpectations(laboratory, 'parent', valid, errorCode)

        where:
        name                      | valid | errorCode
        null                      | true  | null
        createLaboratory()        | true  | null
    }






}
