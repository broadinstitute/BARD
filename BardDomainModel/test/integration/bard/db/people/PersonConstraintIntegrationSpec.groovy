package bard.db.people

import bard.db.BardIntegrationSpec
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import org.junit.Before

import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import static bard.db.people.Person.NAME_MAX_SIZE
import static test.TestUtils.createString
import static bard.db.people.Person.NAME_MAX_SIZE

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/11/13
 * Time: 3:10 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class PersonConstraintIntegrationSpec extends BardIntegrationSpec {
    Person domainInstance

    @Before
    void doSetup() {
        domainInstance = Person.buildWithoutSave()
    }

    void "test name constraints #desc name: '#valueUnderTest'"() {
        final String field = 'userName'

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
        desc               | valueUnderTest                             | valid | errorCode
        'null '            | null                                       | false | 'nullable'
        'blank value'      | ''                                         | false | 'blank'
        'blank value'      | '  '                                       | false | 'blank'
        'too long'         | createString(NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(NAME_MAX_SIZE)     | true  | null
    }
}
