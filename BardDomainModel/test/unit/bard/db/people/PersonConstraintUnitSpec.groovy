package bard.db.people

import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import org.junit.Before
import spock.lang.Specification
import spock.lang.Unroll

import static bard.db.people.Person.NAME_MAX_SIZE
import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Build([Person, PersonRole, Role])
@Mock([Person, PersonRole, Role])
@Unroll
class PersonConstraintUnitSpec extends Specification {
    Person domainInstance

    @Before
    void doSetup() {
        domainInstance = Person.buildWithoutSave()
    }

    void 'test isAdmin #desc'() {
        given:
        Person person = Person.build()
        Role role = Role.build(authority: authority)
        PersonRole.build(role: role, person: person)
        when:
        boolean isAdmin = person.isAdmin()
        then:
        assert isAdmin == adminProperty
        where:
        desc                     | authority                 | adminProperty
        "Not Bard Administrator" | "Authority"               | false
        "Bard Administrator"     | "ROLE_BARD_ADMINISTRATOR" | true
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
        desc               | valueUnderTest                  | valid | errorCode
        'null '            | null                            | false | 'nullable'
        'blank value'      | ''                              | false | 'blank'
        'blank value'      | '  '                            | false | 'blank'
        'too long'         | createString(NAME_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'exactly at limit' | createString(NAME_MAX_SIZE)     | true  | null
    }
}
