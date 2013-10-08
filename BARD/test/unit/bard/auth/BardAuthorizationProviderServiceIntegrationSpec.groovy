package bard.auth

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import com.atlassian.crowd.integration.rest.entity.UserEntity
import com.atlassian.crowd.service.client.CrowdClient
import grails.buildtestdata.mixin.Build
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.GrantedAuthorityImpl
import spock.lang.Specification
import spock.lang.Unroll
import util.BardUser

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@Build([Person, Role, PersonRole])
@Mock([Person, Role, PersonRole])
@TestFor(BardAuthorizationProviderService)
@Unroll
class BardAuthorizationProviderServiceIntegrationSpec extends Specification {

    void "test addRolesFromDatabase Role: #desc"() {
        given:
        Person person = Person.build(userName: roleName)
        Role role = Role.build(authority: roleName)
        PersonRole.build(person: person, role: role)
        BardUser bardUser = new BardUser(username: person.userName)
        when:
        service.addRolesFromDatabase(bardUser)
        then:
        assert bardUser.authorities
        assert bardUser.authorities.size() == 1
        assert bardUser.authorities.iterator().next().authority == expectedRoleName
        where:
        desc                                                                           | roleName       | expectedRoleName
        "Has a role that exists in CAP"                                                | "ROLE_CURATOR" | "ROLE_CURATOR"
        "Has a role that exists in CAP, but it does not start with the prefix 'ROLE_'" | "CURATOR"      | "CURATOR"
    }

    void "test Person #desc"() {
        given:
        Person person = Person.build()
        BardUser bardUser = new BardUser(username: person.userName)
        when:
        service.addRolesFromDatabase(bardUser)
        then:
        assert bardUser.authorities.isEmpty()
        where:
        desc           | roleName  | expectedRoleName
        "has no roles" | "CURATOR" | "ROLE_CURATOR"
    }

    void "test addRolesFromDatabase Person #desc"() {
        given:
        String userName = "some username"
        BardUser bardUser = new BardUser(username: userName)
        when:
        service.addRolesFromDatabase(bardUser)
        then:
        assert bardUser.authorities.isEmpty()
        where:
        desc                         | roleName  | expectedRoleName
        "Does not exist in database" | "CURATOR" | "ROLE_CURATOR"
    }

    void "test authenticate with failure"() {
        given:
        Authentication authentication = Mock(Authentication)
        use(MockedTestCategoryException) {
            service.addRolesFromDatabase(new BardUser(username: "userName"))
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
           service.addRolesFromDatabase(new BardUser(username:"userName"))
        }
        when:
        BardUser bardUser = service.findByUserName("userName")
        then:
        crowdClient.getUser(_) >> { new UserEntity("name", "firstName", "lastName", "displayName", "emailAddress", null, true) }
        assert bardUser
    }

    void "test authenticate with success"() {
        given:
        Authentication authentication = Mock(Authentication)
        CrowdClient crowdClient = Mock(CrowdClient)
        service.crowdClient = crowdClient
        use(MockedTestCategory) {
            service.addRolesFromDatabase(new BardUser(username: "userName"))
        }
        when:
        def results = service.authenticate(authentication)
        then:

        crowdClient.authenticateUser(_, _) >> { new UserEntity("name", "firstName", "lastName", "displayName", "emailAddress", null, true) }
        assert results
    }
}
class MockedTestCategory {

    static void addRolesFromDatabase(BardUser bardUser) {
        bardUser.authorities = [new GrantedAuthorityImpl("ROLE_TEST")]
    }

}
class MockedTestCategoryException {

    static void addRolesFromDatabase(BardUser userName) {
        throw new Exception("")
    }

}