package bard.db.people

import bard.db.BardIntegrationSpec
import org.junit.Before
import spock.lang.Unroll

/**
 *
 */
@Unroll
class PersonRoleConstraintIntegrationSpec extends BardIntegrationSpec {
    PersonRole domainInstance

    @Before
    void doSetup() {

    }

    void "test create and associate person"() {
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person = Person.build()
        when:
        PersonRole personRole = PersonRole.create(person, role, modifiedBy)
        then:
        assert personRole
        assert personRole.person == person
        assert personRole.role == role
        assert personRole.modifiedBy == modifiedBy
    }

    void "test get"() {
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person = Person.build()
        PersonRole.create(person, role, modifiedBy)
        when:
        PersonRole personRole = PersonRole.get(person.id, role.id)
        then:
        assert personRole
        assert personRole.person == person
        assert personRole.role == role
        assert personRole.modifiedBy == modifiedBy

    }

    void "test remove"() {
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person = Person.build()
        PersonRole.create(person, role, modifiedBy)
        when:
        boolean personRole = PersonRole.remove(person, role, true)
        then:
        assert personRole
    }
    void "test removeAll by Person"(){
        given:
        String modifiedBy = "me"
        Role role = Role.build(authority: "authority")
        Person person1 = Person.build()
        Person person2 = Person.build()
        PersonRole.create(person1, role, modifiedBy)
        PersonRole.create(person2, role, modifiedBy)
        when:
        PersonRole.removeAll(person1)
        then:
        assert !PersonRole.get(person1.id, role.id)
        assert PersonRole.get(person2.id,role.id)
    }
    void "test removeAll by Role"(){
        given:
        String modifiedBy = "me"
        Role role1 = Role.build(authority: "authority1")
        Role role2 = Role.build(authority: "authority2")
        Person person1 = Person.build()
        Person person2 = Person.build()
        PersonRole.create(person1, role1, modifiedBy)
        PersonRole.create(person2, role2, modifiedBy)
        when:
        PersonRole.removeAll(role1)
        then:
        assert !PersonRole.get(person1.id, role1.id)
        assert PersonRole.get(person2.id,role2.id)
    }

//    void "test modifiedBy constraints #desc name: '#valueUnderTest'"() {
//        given:
//        domainInstance = PersonRole.buildWithoutSave()
//        final String field = 'modifiedBy'
//
//        when: 'a value is set for the field under test'
//        domainInstance[(field)] = valueUnderTest
//        domainInstance.validate()
//
//        then: 'verify valid or invalid for expected reason'
//        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)
//
//        and: 'verify domain can be persisted to the db'
//        if (valid) {
//            domainInstance == domainInstance.save(flush: true)
//        }
//
//        where:
//        desc               | valueUnderTest                         | valid | errorCode
//        'null '            | null                                   | true  | null
//        'blank value'      | ''                                     | false | 'blank'
//        'blank value'      | '  '                                   | false | 'blank'
//        'too long'         | createString(MODIFIED_BY_MAX_SIZE + 1) | false | 'maxSize.exceeded'
//        'exactly at limit' | createString(MODIFIED_BY_MAX_SIZE)     | true  | null
//    }
}
