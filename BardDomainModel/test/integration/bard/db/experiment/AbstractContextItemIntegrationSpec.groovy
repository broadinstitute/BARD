package bard.db.experiment

import grails.plugin.spock.IntegrationSpec
import org.junit.Before
import spock.lang.Unroll

import static bard.db.model.AbstractContextItem.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import bard.db.model.AbstractContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:28 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractContextItemIntegrationSpec extends IntegrationSpec {

    AbstractContextItem domainInstance

    @Before
    abstract void doSetup()

    void "test qualifier constraints #desc qualifier: '#valueUnderTest'"() {

        final String field = 'qualifier'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc                   | valueUnderTest | valid | errorCode
        'blank'                | ''             | false | 'blank'
        'blank'                | '  '           | false | 'blank'
        'bad val'              | 'aa'           | false | 'not.inList'

        'equals'               | '= '           | true  | null
        'less than'            | '< '           | true  | null
        'less than or equal'   | '<='           | true  | null
        'greter than'          | '> '           | true  | null
        'greter than or equal' | '>='           | true  | null
        '<<'                   | '<<'           | true  | null
        '>>'                   | '>>'           | true  | null
        '~ '                   | '~ '           | true  | null

        'null'                 | null           | true  | null

    }

    void "test extValueId constraints #desc extValueId: '#valueUnderTest'"() {

        final String field = 'extValueId'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                          | valid | errorCode
        'too long'         | createString(EXT_VALUE_ID_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                      | false | 'blank'
        'blank valid'      | '  '                                    | false | 'blank'

        'exactly at limit' | createString(EXT_VALUE_ID_MAX_SIZE)     | true  | null
        'null valid'       | null                                    | true  | null
    }

    void "test valueDisplay constraints #desc valueDisplay: '#valueUnderTest'"() {

        final String field = 'valueDisplay'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                           | valid | errorCode
        'too long'         | createString(VALUE_DISPLAY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank valid'      | ''                                       | false | 'blank'
        'blank valid'      | '  '                                     | false | 'blank'

        'exactly at limit' | createString(VALUE_DISPLAY_MAX_SIZE)     | true  | null
        'null valid'       | null                                     | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify the domain can be persisted to the db'
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

        and: 'verify the domain can be persisted to the db'
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

        and: 'verify the domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }
}

