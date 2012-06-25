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
        TestUtils.createString(257)                    | false | 'maxSize'
        TestUtils.createString(256)                    | true  | null
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
        TestUtils.createString(20)     | true | null
        TestUtils.createString(20)+"1" | false | 'maxSize'
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
        TestUtils.createString(1000)+"a"| false | 'maxSize'
        TestUtils.createString(1000)    | true  | null
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
        TestUtils.createString(40)     | true  | null
        TestUtils.createString(40)+"a" | false | 'maxSize'
        "joe"                   | true  | null
    }




}
