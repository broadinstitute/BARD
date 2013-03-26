package bard.db.dictionary

import bard.db.BardIntegrationSpec
import grails.plugin.spock.IntegrationSpec
import org.junit.Before
import spock.lang.Unroll

import static bard.db.dictionary.StageTree.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/27/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class StageTreeConstraintIntegrationSpec extends BardIntegrationSpec {
    StageTree domainInstance

    @Before
    void doSetup() {
        domainInstance = StageTree.buildWithoutSave()
        domainInstance.element.save()
    }

    void "test parent constraints #desc parent: '#valueUnderTest'"() {
        final String field = 'parent'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest      | valid | errorCode
        'null valid'  | {null}              | true  | null
        'valid value' | {StageTree.build()} | true  | null
    }

    void "test element constraints #desc element: '#valueUnderTest'"() {
        final String field = 'element'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest    | valid | errorCode
        'null value'  | {null}            | false | 'nullable'
        'valid value' | {Element.build()} | true  | null
    }

    void "test leaf constraints #desc leaf: '#valueUnderTest'"() {
        final String field = 'leaf'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'true valid'     | true           | true  | null
        'false valid'    | false          | true  | null
    }

    void "test fullPath constraints #desc fullPath : size #valueUnderTest.size()"() {
        final String field = 'fullPath'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest                         | valid | errorCode
        'too long'    | createString(FULL_PATH_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(FULL_PATH_MAX_SIZE)       | true  | null
        'null valid'  | null                                   | true  | null
        'valid value' | "foo"                                  | true  | null
    }

    void "test label constraints #desc label size #valueUnderTest.size()"() {
        final String field = 'label'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest                     | valid | errorCode
        'null not valid' | null                               | false | 'nullable'
        'too long'       | createString(LABEL_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value'    | createString(LABEL_MAX_SIZE)       | true  | null
        'valid value'    | "foo"                              | true  | null
    }

    void "test elementStatus constraints #desc elementStatus : '#valueUnderTest'"() {
        final String field = 'elementStatus'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest                              | valid | errorCode
        'null not valid' | null                                        | false | 'nullable'
        'too long'       | createString(ELEMENT_STATUS_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value'    | createString(ELEMENT_STATUS_MAX_SIZE)       | true  | null
        'valid value'    | "foo"                                       | true  | null
    }

    void "test description constraints #desc size #valueUnderTest.size()"() {
        final String field = 'description'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest                           | valid | errorCode
        'too long'    | createString(DESCRIPTION_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'null valid'  | null                                     | true  | null
        'valid value' | createString(DESCRIPTION_MAX_SIZE)       | true  | null
        'valid value' | "foo"                                    | true  | null
    }

}