package bard.db.model

import bard.db.BardIntegrationSpec
import bard.db.audit.BardContextUtils
import bard.db.enums.ContextType
import grails.plugin.spock.IntegrationSpec
import org.hibernate.SessionFactory
import org.junit.After
import org.junit.Before
import spock.lang.Unroll

import static bard.db.model.AbstractContext.*
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/6/12
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
abstract class AbstractContextConstraintIntegrationSpec extends BardIntegrationSpec {

    def domainInstance

    @Before
    abstract void doSetup()

    @After
    void doAfter() {
        if (domainInstance.validate()) {
            domainInstance.save(flush: true)
        }
    }

    void "test contextName constraints #desc contextName: '#valueUnderTest'"() {
        final String field = 'contextName'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                          | valid | errorCode
        'too long'         | createString(CONTEXT_NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'blank not valid'  | ''                                      | false | 'blank'
        'blank not valid'  | '   '                                   | false | 'blank'

        'null valid'       | null                                    | true  | null
        'exactly at limit' | createString(CONTEXT_NAME_MAX_SIZE)     | true  | null

    }

    void "test contextType constraints #desc: '#valueUnderTest'"() {
        final String field = 'contextType'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        where:
        desc               | valueUnderTest                           | valid | errorCode
        'null invalid'     | null                                     | false | 'nullable'
        'not null'         | ContextType.BIOLOGY                      | true  | null
    }

    void "test modifiedBy constraints #desc modifiedBy: '#valueUnderTest'"() {

        final String field = 'modifiedBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
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

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

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

        where:
        desc         | valueUnderTest | valid | errorCode
        'null valid' | null           | true  | null
        'date valid' | new Date()     | true  | null
    }
}