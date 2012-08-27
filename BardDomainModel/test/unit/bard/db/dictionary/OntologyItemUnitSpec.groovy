package bard.db.dictionary

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import test.TestUtils

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(OntologyItem)
class OntologyItemUnitSpec  extends Specification {

    ArrayList<OntologyItem> existingOntologyItem = [new OntologyItem(ontology:createOntology (),dateCreated: new Date())]
    OntologyItem validOntologyItem = new OntologyItem (ontology:createOntology (),dateCreated: new Date())

    Ontology createOntology () {
       new Ontology(ontologyName : 'existingOntology',dateCreated:  new Date())
    }


    @Unroll
    void "test constraints on itemReference"() {
        given:
        mockForConstraintsTests(OntologyItem, existingOntologyItem)

        when:
        OntologyItem ontologyItem = validOntologyItem
        ontologyItem.itemReference = name
        ontologyItem.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontologyItem, 'itemReference', valid, errorCode)

        where:
        name                        | valid | errorCode
        TestUtils.createString(10)         | true  | null
        TestUtils.createString(10)+"a"     | false | 'maxSize'
        null                        | true  | null
    }


    @Unroll
    void "test constraints on dateCreated"() {
        given:
        mockForConstraintsTests(OntologyItem, existingOntologyItem)

        when:
        OntologyItem ontologyItem = validOntologyItem
        ontologyItem.dateCreated = name
        ontologyItem.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontologyItem, 'dateCreated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null     // why isn't this false
    }


    @Unroll
    void "test constraints on lastUpdated"() {
        given:
        mockForConstraintsTests(OntologyItem, existingOntologyItem)

        when:
        OntologyItem ontologyItem = validOntologyItem
        ontologyItem.lastUpdated = name
        ontologyItem.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontologyItem, 'lastUpdated', valid, errorCode)

        where:
        name                     | valid | errorCode
        new Date()               | true  |  null
        null                     | true  |  null     // why isn't this false
    }


    @Unroll
    void "test constraints on modifiedBy"() {
        given:
        mockForConstraintsTests(OntologyItem, existingOntologyItem)

        when:
        OntologyItem ontologyItem = validOntologyItem
        ontologyItem.modifiedBy = name
        ontologyItem.validate()

        then:
        TestUtils.assertFieldValidationExpectations(ontologyItem, 'modifiedBy', valid, errorCode)

        where:
        name                    | valid | errorCode
        null                    | true  | null
        TestUtils.createString(40)     | true  | null
        TestUtils.createString(40)+"a" | false | 'maxSize'
        "joe"                   | true  | null
    }




}
