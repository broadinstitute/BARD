package bard.auth

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import com.atlassian.crowd.integration.rest.entity.UserEntity
import com.atlassian.crowd.service.client.CrowdClient
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.broadinstitute.cbip.crowd.CbipUser
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.GrantedAuthorityImpl
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
        final List<GrantedAuthority> roles = service.getRolesFromDatabase(person.userName)
        then:
        assert roles
        assert roles.size() == 1
        assert roles.get(0).authority == expectedRoleName
        where:
        desc                                                                           | roleName       | expectedRoleName
        "Has a role that exists in CAP"                                                | "ROLE_CURATOR" | "ROLE_CURATOR"
        "Has a role that exists in CAP, but it does not start with the prefix 'ROLE_'" | "CURATOR"      | "ROLE_CURATOR"
    }

    void "test Person #desc"() {
        given:
        Person person = Person.build()
        when:
        final List<GrantedAuthority> roles = service.getRolesFromDatabase(person.userName)
        then:
        assert roles.isEmpty()
        where:
        desc           | roleName  | expectedRoleName
        "has no roles" | "CURATOR" | "ROLE_CURATOR"
    }

    void "test getRolesFromDatabase Person #desc"() {
        given:
        String userName = "some username"

        when:
        final List<GrantedAuthority> roles = service.getRolesFromDatabase(userName)
        then:
        assert roles.isEmpty()
        where:
        desc                         | roleName  | expectedRoleName
        "Does not exist in database" | "CURATOR" | "ROLE_CURATOR"
    }

    void "test authenticate with failure"() {
        given:
        Authentication authentication = Mock(Authentication)
        use(MockedTestCategoryException) {
            service.getRolesFromDatabase("userName")
        }
        when:
        service.authenticate(authentication)
        then:
        thrown(Exception)
    }

    void "test findByUserName with failure"() {
        given:
        CrowdClient crowdClient = Mock(CrowdClient)
        service.crowdClient = crowdClient

        when:
        service.findByUserName("userName")
        then:
        crowdClient.getUser(_) >> { throw new Exception("") }
        thrown(Exception)
    }

    void "test findByUserName with success"() {
        given:
        CrowdClient crowdClient = Mock(CrowdClient)
        service.crowdClient = crowdClient
        use(MockedTestCategory) {
            List list = service.getRolesFromDatabase("userName")
        }
        when:
        CbipUser cbipUser = service.findByUserName("userName")
        then:
        crowdClient.getUser(_) >> { new UserEntity("name", "firstName", "lastName", "displayName", "emailAddress", null, true) }
        assert cbipUser
    }

    void "test authenticate with success"() {
        given:
        Authentication authentication = Mock(Authentication)
        CrowdClient crowdClient = Mock(CrowdClient)
        service.crowdClient = crowdClient
        use(MockedTestCategory) {
            List list = service.getRolesFromDatabase("userName")
        }
        when:
        def results = service.authenticate(authentication)
        then:

        crowdClient.authenticateUser(_, _) >> { new UserEntity("name", "firstName", "lastName", "displayName", "emailAddress", null, true) }
        assert results
    }
}
class MockedTestCategory {

    static List getRolesFromDatabase(String userName) {
        return [new GrantedAuthorityImpl("ROLE_TEST")]
    }

}
class MockedTestCategoryException {

    static List getRolesFromDatabase(String userName) {
        throw new Exception("")
    }

}
