package bard.db.people

import org.junit.*
import spock.lang.Specification
import grails.buildtestdata.mixin.Build
import spock.lang.Unroll
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString
import static bard.db.people.Person.*

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Build([Person])
@Unroll
class PersonConstraintUnitSpec extends Specification {
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
