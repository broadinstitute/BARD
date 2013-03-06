package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.dictionary.OntologyItem.ITEM_REFERENCE_MAX_SIZE
import static bard.db.dictionary.OntologyItem.MODIFIED_BY_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([OntologyItem, Element])
@Mock([OntologyItem, Element])
@Unroll
class OntologyItemConstraintUnitSpec extends Specification {

    OntologyItem domainInstance

    @Before
    void doSetup() {
        domainInstance = OntologyItem.buildWithoutSave()
    }

    void "test ontology constraints #desc"() {

        final String field = 'ontology'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest       | valid | errorCode
        'null not valid' | { null }             | false | 'nullable'
        'valid ontology' | { Ontology.build() } | true  | null

    }

    void "test element constraints #desc"() {

        final String field = 'element'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest      | valid | errorCode
        'null not valid' | { null }            | true  | null
        'valid element'  | { Element.build() } | true  | null

    }

    void "test itemReference constraints #desc itemReference: '#valueUnderTest'"() {

        final String field = 'itemReference'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                            | valid | errorCode
        'too long'         | createString(ITEM_REFERENCE_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                        | false | 'blank'
        'blank valid'      | '  '                                      | false | 'blank'

        'exactly at limit' | createString(ITEM_REFERENCE_MAX_SIZE)     | true  | null
        'null valid'       | null                                      | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                         | valid | errorCode
        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                     | false | 'blank'
        'blank valid'      | '  '                                   | false | 'blank'

        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
        'null valid'       | null                                   | true  | null
    }

    void "test dateCreated constraints #desc dateCreated: '#valueUnderTest'"() {
        final String field = 'dateCreated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }

}