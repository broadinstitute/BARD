package bard.db.people

import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.people.Person.MODIFIED_BY_MAX_SIZE
import static bard.db.people.Person.NAME_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
  */
@Build([Role])
@Unroll
class RoleConstraintUnitSpec extends Specification {
    Role domainInstance

    @Before
    void doSetup() {
        domainInstance = Role.buildWithoutSave()
    }

    void "test authority constraints #desc name: '#valueUnderTest'"() {
        final String field = 'authority'

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
        desc               | valueUnderTest                  | valid | errorCode
        'null '            | null                            | false | 'nullable'
        'blank value'      | ''                              | false | 'blank'
        'blank value'      | '  '                            | false | 'blank'
        'too long'         | createString(NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(NAME_MAX_SIZE)     | true  | null
    }

    void "test modifiedBy constraints #desc name: '#valueUnderTest'"() {
        final String field = 'modifiedBy'

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
        desc               | valueUnderTest                         | valid | errorCode
        'null '            | null                                   | true  | null
        'blank value'      | ''                                     | false | 'blank'
        'blank value'      | '  '                                   | false | 'blank'
        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
    }

    void "test role constraints #desc display name: '#valueUnderTest'"() {
        given:
        final String field = 'displayName'

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
        desc               | valueUnderTest                         | valid | errorCode
        'null '            | null                                   | true  | null
        'blank value'      | ''                                     | false | 'blank'
        'blank value'      | '  '                                   | false | 'blank'
        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
    }
}
