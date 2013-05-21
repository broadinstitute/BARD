package bard.auth

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.security.core.GrantedAuthority
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Build([Person, Role, PersonRole])
@Mock([Person, Role, PersonRole])
@TestFor(BardAuthorizationProviderService)
@Unroll
class BardAuthorizationProviderServiceSpec extends Specification {


    void "test getRolesFromDatabase Role: #desc"() {
        given:
        Person person = Person.build()
        Role role = Role.build(authority: roleName)
        PersonRole.build(person: person, role: role)

        when:
        final List<GrantedAuthority> rolesFromDatabase = service.getRolesFromDatabase(person.userName)
        then:
        assert rolesFromDatabase
        assert rolesFromDatabase.size() == 1
        assert rolesFromDatabase.get(0).authority == expectedRoleName
        where:
        desc                            | roleName  | expectedRoleName
        "Has a role that exists in CAP" | "CURATOR" | "ROLE_CURATOR"
    }

    void "test getRolesFromDatabase Role: Fail"() {
        given:
        Person person = Person.build()
        Role role = Role.build(authority: roleName)
        PersonRole.build(person: person, role: role)

        when:
        final List<GrantedAuthority> rolesFromDatabase = service.getRolesFromDatabase(person.userName)
        then:
        assert rolesFromDatabase.isEmpty()

        where:
        desc                                    | roleName
        "Has a role that does not exist in CAP" | "SOME CURATOR"
    }
}
