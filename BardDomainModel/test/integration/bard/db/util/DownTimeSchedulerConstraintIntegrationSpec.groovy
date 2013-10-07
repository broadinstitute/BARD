package bard.db.util

import bard.db.BardIntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
class DownTimeSchedulerConstraintIntegrationSpec extends BardIntegrationSpec {
    DownTimeScheduler domainInstance

    @Before
    void doSetup() {
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        String createdBy = "Me"
        domainInstance = DownTimeScheduler.buildWithoutSave(createdBy: createdBy, downTime: new Date(), displayValue: "display")
    }

    void "test createdBy by constraints #desc: '#valueUnderTest'"() {
        final String field = 'createdBy'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                                            | valid | errorCode
        'null '            | null                                                      | false | 'nullable'
        'too long'         | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE)     | true  | null
    }

    void "test display value constraints #desc: '#valueUnderTest'"() {
        final String field = 'displayValue'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc               | valueUnderTest                                               | valid | errorCode
        'null '            | null                                                         | false | 'nullable'
        'too long'         | createString(DownTimeScheduler.DISPLAY_VALUE_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(DownTimeScheduler.DISPLAY_VALUE_MAX_SIZE)     | true  | null
    }

    void "test downTime constraints #desc downTime: '#valueUnderTest'"() {
        final String field = 'downTime'

        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            domainInstance == domainInstance.save(flush: true)
        }

        where:
        desc        | valueUnderTest | valid | errorCode
        'null'      | null           | false | 'nullable'
        'down time' | new Date(200)  | true  | null
    }
}
