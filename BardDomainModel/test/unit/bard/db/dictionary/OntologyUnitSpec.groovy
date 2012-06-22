package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(Ontology)
class OntologyUnitSpec  extends Specification {

    ArrayList<Ontology> existingOntology = [new Ontology(ontologyName : 'existingOntology',dateCreated:  new Date())]
    Ontology validOntology = new Ontology (ontologyName : 'existingOntology',dateCreated:  new Date())


    @Unroll
    void "test constraints on ontologyName"() {
        given:
        mockForConstraintsTests(Ontology, existingOntology)

        when:
        Ontology ontology = validOntology
        ontology.ontologyName = name
        ontology.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontology, 'ontologyName', valid, errorCode)

        where:
        name                                        | valid | errorCode
        null                                        | false | 'nullable'
        TestUtils.lString256+"1"                    | false | 'maxSize'
        TestUtils.lString256                        | true  | null
    }


    @Unroll
    void "test constraints on abbreviation"() {
        given:
        mockForConstraintsTests(Ontology, existingOntology)

        when:
        Ontology ontology = validOntology
        ontology.abbreviation = name
        ontology.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontology, 'abbreviation', valid, errorCode)

        where:
        name                                        | valid | errorCode
        null                                        | true  | null
        TestUtils.lString10+TestUtils.lString10     | true | null
        TestUtils.lString10+TestUtils.lString10+"1" | false | 'maxSize'
        TestUtils.lString10+TestUtils.lString10     | true | null
    }


    @Unroll
    void "test constraints on systemUrl"() {
        given:
        mockForConstraintsTests(Ontology, existingOntology)

        when:
        Ontology ontology = validOntology
        ontology.systemUrl = name
        ontology.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontology, 'systemUrl', valid, errorCode)

        where:
        name                     | valid | errorCode
        null                     | true  | null
        TestUtils.lString1000+"a"| false | 'maxSize'
        TestUtils.lString1000    | true  | null
    }


    @Unroll
    void "test constraints on dateCreated"() {
        given:
        mockForConstraintsTests(Ontology, existingOntology)

        when:
        Ontology ontology = validOntology
        ontology.modifiedBy = name
        ontology.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontology, 'dateCreated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null     // why isn't this false
    }


    @Unroll
    void "test constraints on lastUpdated"() {
        given:
        mockForConstraintsTests(Ontology, existingOntology)

        when:
        Ontology ontology = validOntology
        ontology.modifiedBy = name
        ontology.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontology, 'lastUpdated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null     // why isn't this false
    }


    @Unroll
    void "test constraints on modifiedBy"() {
        given:
        mockForConstraintsTests(Ontology, existingOntology)

        when:
        Ontology ontology = validOntology
        ontology.modifiedBy = name
        ontology.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontology, 'modifiedBy', valid, errorCode)

        where:
        name                    | valid | errorCode
        null                    | true  | null
        TestUtils.lString40     | true  | null
        TestUtils.lString40+"a" | false | 'maxSize'
        "joe"                   | true  | null
    }




}
