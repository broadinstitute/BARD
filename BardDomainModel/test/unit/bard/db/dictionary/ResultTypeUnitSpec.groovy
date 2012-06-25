package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(InstanceDescriptor)
class ResultTypeUnitSpec extends Specification {

    ArrayList<ResultType> existingResultType = [new ResultType(resultTypeName: "resultTypeName", element: createElement(), resultTypeStatus: "resultTypeStatus")]
    ResultType validResultType = new ResultType(resultTypeName: "resultTypeName", element: createElement(), resultTypeStatus: "resultTypeStatus")

    private Element createElement() {
        new Element(label: 'existingLabel')
    }

    private ResultType createResultType() {
        new ResultType(resultTypeName: "resultTypeName", element: createElement(), resultTypeStatus: "resultTypeStatus")
    }

    private Unit createUnit() {
        new Unit()
    }





    @Unroll
    void "test constraints on parent"() {
        given:
        mockForConstraintsTests(ResultType, existingResultType)

        when:
        ResultType resultType = validResultType
        resultType.parent = name
        resultType.validate()

        then:
        TestUtils.assertFieldValidationExpectations(resultType, 'parent', valid, errorCode)

        where:
        name               | valid | errorCode
        null               | true  | null
        createResultType() | true  | null
    }


    @Unroll
    void "test constraints on resultTypeName"() {
        given:
        mockForConstraintsTests(ResultType, existingResultType)

        when:
        ResultType resultType = validResultType
        resultType.resultTypeName = name
        resultType.validate()

        then:
        TestUtils.assertFieldValidationExpectations(resultType, 'resultTypeName', valid, errorCode)

        where:
        name                              | valid | errorCode
        null                              | false | 'nullable'
        TestUtils.createString(128) + "a" | false | 'maxSize'
        TestUtils.createString(128)       | true  | null
    }



    @Unroll
    void "test constraints on element"() {
        given:
        mockForConstraintsTests(ResultType, existingResultType)

        when:
        ResultType resultType = validResultType
        resultType.element = name
        resultType.validate()

        then:
        TestUtils.assertFieldValidationExpectations(resultType, 'element', valid, errorCode)

        where:
        name            | valid | errorCode
        null            | false | 'nullable'
        createElement() | true  | null
    }




    @Unroll
    void "test constraints on description"() {
        given:
        mockForConstraintsTests(ResultType, existingResultType)

        when:
        ResultType resultType = validResultType
        resultType.description = name
        resultType.validate()

        then:
        TestUtils.assertFieldValidationExpectations(resultType, 'description', valid, errorCode)

        where:
        name                               | valid | errorCode
        null                               | true  | null
        TestUtils.createString(1000)       | true  | null
        TestUtils.createString(1000) + "a" | false | 'maxSize'
        "foo"                              | true  | null
    }



    @Unroll
    void "test constraints on abbreviation"() {
        given:
        mockForConstraintsTests(ResultType, existingResultType)

        when:
        ResultType resultType = validResultType
        resultType.abbreviation = name
        resultType.validate()

        then:
        TestUtils.assertFieldValidationExpectations(resultType, 'abbreviation', valid, errorCode)

        where:
        name                             | valid | errorCode
        null                             | true  | null
        TestUtils.createString(20)       | true  | null
        TestUtils.createString(20) + "1" | false | 'maxSize'
        "foo"                            | true  | null
    }

    @Unroll
    void "test constraints on synonyms"() {
        given:
        mockForConstraintsTests(ResultType, existingResultType)

        when:
        ResultType resultType = validResultType
        resultType.synonyms = name
        resultType.validate()

        then:
        TestUtils.assertFieldValidationExpectations(resultType, 'synonyms', valid, errorCode)

        where:
        name                               | valid | errorCode
        null                               | true  | null
        TestUtils.createString(1000) + "a" | false | 'maxSize'
        TestUtils.createString(1000)       | true  | null
    }


}
