package bard.db.dictionary

import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Shared
import spock.lang.Unroll

import static bard.db.dictionary.Descriptor.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/15/12
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractDescriptorConstraintIntegrationSpec extends IntegrationSpec {

    def domainInstance
    @Shared def parent

    @Before
    abstract void doSetup()

    @After
    void doAfter() {
        if (domainInstance.validate()) {
            domainInstance == domainInstance.save(flush: true)
        }
    }

    void "test parent constraints #desc parent: '#valueUnderTest'"() {
        final String field = 'parent'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc           | valueUnderTest | valid | errorCode
        'null valid'   | null           | true  | null
        'valid parent' | parent         | true  | null
    }

    void "test element constraints #desc element: '#valueUnderTest'"() {
        final String field = 'element'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest    | valid | errorCode
        'null not value' | {null}            | false | 'nullable'
        'valid Element'  | {Element.build()} | true  | null
    }

    void "test elementStatus constraints #desc elementStatus: '#valueUnderTest'"() {
        final String field = 'elementStatus'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest           | valid | errorCode
        'null not value' | null                     | false | 'nullable'
        'valid value'   | ElementStatus.Pending    | true  | null
        'valid value'   | ElementStatus.Published  | true  | null
        'valid value'   | ElementStatus.Deprecated | true  | null
        'valid value'   | ElementStatus.Retired    | true  | null
    }

    void "test label constraints #desc label: '#valueUnderTest'"() {
        final String field = 'label'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc             | valueUnderTest                     | valid | errorCode
        'null not value' | null                               | false | 'nullable'
        'too long'       | createString(LABEL_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value'    | createString(LABEL_MAX_SIZE)       | true  | null
        'valid value'    | "foo"                              | true  | null
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

    void "test description constraints #desc description : '#valueUnderTest'"() {
        final String field = 'description'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest                           | valid | errorCode
        'too long'    | createString(DESCRIPTION_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(DESCRIPTION_MAX_SIZE)       | true  | null
        'null valid'  | null                                     | true  | null
        'valid value' | "foo"                                    | true  | null
    }

    void "test fullPath constraints #desc fullPath : '#valueUnderTest'"() {
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

    void "test abbreviation constraints #desc abbreviation: '#valueUnderTest'"() {
        final String field = 'abbreviation'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest                            | valid | errorCode
        'too long'    | createString(ABBREVIATION_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(ABBREVIATION_MAX_SIZE)       | true  | null
        'null value'  | null                                      | true  | null
        'valid value' | "foo"                                     | true  | null
    }

    void "test synonyms constraints #desc synonyms: '#valueUnderTest'"() {
        final String field = 'synonyms'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest                        | valid | errorCode
        'too long'    | createString(SYNONYMS_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(SYNONYMS_MAX_SIZE)       | true  | null
        'null value'  | null                                  | true  | null
        'valid value' | "foo"                                 | true  | null
    }

    void "test externalURL constraints #desc externalURL: '#valueUnderTest'"() {
        final String field = 'externalURL'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest                        | valid | errorCode
        'too long'    | createString(SYNONYMS_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(SYNONYMS_MAX_SIZE)       | true  | null
        'null value'  | null                                  | true  | null
        'valid value' | "foo"                                 | true  | null
    }

    void "test unit constraints #desc unit: '#valueUnderTest'"() {
        final String field = 'unit'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest.call()
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc          | valueUnderTest      | valid | errorCode
        'null value'  | {null}              | true  | null
        'valid value' | { Element.build() } | true  | null
    }

}
