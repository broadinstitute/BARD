package bard.db.dictionary
import spock.lang.Specification
import spock.lang.Unroll
import grails.test.mixin.TestFor

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 7/13/12
 * Time: 9:37 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(Stage)
class StageSpec  extends Specification {


    ArrayList<Stage> existingStage = [new Stage(stage: "stageName", element: createElement())]
    Stage validStage = new Stage(stage: "stageName", element: createElement())


    private Element createElement() {
        new Element(label: 'existingLabel')
    }


    @Unroll
    void "test constraints on stage"() {
        given:
        mockForConstraintsTests(Stage, existingStage)

        when:
        Stage stage = validStage
        stage.stage = name
        stage.validate()

        then:
        TestUtils.assertFieldValidationExpectations(stage, 'stage', valid, errorCode)

        where:
        name                              | valid | errorCode
        null                              | false | 'nullable'
        TestUtils.createString(128) + "a" | false | 'maxSize'
        TestUtils.createString(128)       | true  | null
    }


    @Unroll
    void "test constraints on element"() {
        given:
        mockForConstraintsTests(Stage, existingStage)

        when:
        Stage stage = validStage
        stage.element = name
        stage.validate()

        then:
        TestUtils.assertFieldValidationExpectations(stage, 'element', valid, errorCode)

        where:
        name            | valid | errorCode
        null            | false | 'nullable'
        createElement() | true  | null
    }




    @Unroll
    void "test constraints on description"() {
        given:
        mockForConstraintsTests(Stage, existingStage)

        when:
        Stage stage = validStage
        stage.description = name
        stage.validate()

        then:
        TestUtils.assertFieldValidationExpectations(stage, 'description', valid, errorCode)

        where:
        name                               | valid | errorCode
        null                               | true  | null
        TestUtils.createString(1000)       | true  | null
        TestUtils.createString(1000) + "a" | false | 'maxSize'
        "foo"                              | true  | null
    }



}
