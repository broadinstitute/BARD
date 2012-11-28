package bard.db.dictionary

import bard.db.enums.ReadyForExtraction
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.dictionary.AbstractElement.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import spock.lang.Shared

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/22/12
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractElementConstraintUnitSpec extends Specification {

    def domainInstance

    @Before
    abstract void doSetup()

    void "test elementStatus constraints #desc elementStatus: '#valueUnderTest'"() {
        final String field = 'elementStatus'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest           | valid | errorCode
        'null not value' | null                     | false | 'nullable'
        'valid valude'   | ElementStatus.Pending    | true  | null
        'valid valude'   | ElementStatus.Published  | true  | null
        'valid valude'   | ElementStatus.Deprecated | true  | null
        'valid valude'   | ElementStatus.Retired    | true  | null
    }



    void "test label constraints #desc label: '#valueUnderTest'"() {
        final String field = 'label'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest                     | valid | errorCode
        'null not value' | null                               | false | 'nullable'
        'too long'       | createString(LABEL_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value'    | createString(LABEL_MAX_SIZE)       | true  | null
        'valid value'    | "foo"                              | true  | null
    }

    void "test description constraints #desc description : '#valueUnderTest'"() {
        final String field = 'description'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest                           | valid | errorCode
        'too long'    | createString(DESCRIPTION_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(DESCRIPTION_MAX_SIZE)       | true  | null
        'null valid'  | null                                     | true  | null
        'valid value' | "foo"                                    | true  | null
    }

    void "test abbreviation constraints #desc abbreviation: '#valueUnderTest'"() {
        final String field = 'abbreviation'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

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

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

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

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest                    | valid | errorCode
        'null value'  | {null}                              | true  | null
        'valid value' | {Element.build()}    | true  | null
    }

    void "test bardURI constraints #desc bardURI: '#valueUnderTest'"() {
        final String field = 'bardURI'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.save()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest                        | valid | errorCode
        'too long'    | createString(BARD_URI_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(BARD_URI_MAX_SIZE)       | true  | null
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

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc          | valueUnderTest                            | valid | errorCode
        'too long'    | createString(EXTERNAL_URL_MAX_SIZE) + "a" | false | 'maxSize.exceeded'

        'valid value' | createString(EXTERNAL_URL_MAX_SIZE)       | true  | null
        'null value'  | null                                      | true  | null
        'valid value' | "foo"                                     | true  | null
    }

    void "test readyForExtraction constraints #desc readyForExtraction: '#valueUnderTest'"() {

        final String field = 'readyForExtraction'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest              | valid | errorCode
        'null not valid' | null                        | false | 'nullable'

        'valid valud'    | ReadyForExtraction.Pending  | true  | null
        'valid value'    | ReadyForExtraction.Ready    | true  | null
        'valid value'    | ReadyForExtraction.Started  | true  | null
        'valid value'    | ReadyForExtraction.Complete | true  | null

    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

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

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc             | valueUnderTest | valid | errorCode
        'null not valid' | null           | false | 'nullable'
        'date valid'     | new Date()     | true  | null
    }

    void "test lastUpdated constraints #desc lastUpdated: '#valueUnderTest'"() {
        final String field = 'lastUpdated'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domainspreadsheetmapping can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }
}
