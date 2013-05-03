package bard.db.dictionary

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.dictionary.ElementHierarchy.MODIFIED_BY_MAX_SIZE
import static bard.db.dictionary.ElementHierarchy.RELATIONSHIP_TYPE_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/22/12
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(ElementHierarchy)
@Unroll
class ElementHierarchyConstraintUnitSpec extends Specification {

    ElementHierarchy domainInstance

    @Before
    void doSetup() {
        domainInstance = ElementHierarchy.buildWithoutSave()
    }

    void "test parentElement constraints #desc"() {

        final String field = 'parentElement'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                  | valueUnderTest    | valid | errorCode
        'null valid'          | {null}            | false  | 'nullable'
        'valid parentElement' | {Element.build()} | true  | null

    }

    void "test childElement constraints #desc"() {

        final String field = 'childElement'

        when:
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc                 | valueUnderTest    | valid | errorCode
        'null valid'         | {null}            | false | 'nullable'
        'valid childElement' | {Element.build()} | true  | null

    }

    void "test relationshipType constraints #desc relationshipType: '#valueUnderTest'"() {

        final String field = 'relationshipType'

        when:
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then:
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                               | valid | errorCode
        'null not valid'   | null                                         | false | 'nullable'
        'blank valid'      | ''                                           | false | 'blank'
        'blank valid'      | '  '                                         | false | 'blank'
        'too long'         | createString(RELATIONSHIP_TYPE_MAX_SIZE + 1) | false | 'maxSize.exceeded'

        'exactly at limit' | createString(RELATIONSHIP_TYPE_MAX_SIZE)     | true  | null
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
