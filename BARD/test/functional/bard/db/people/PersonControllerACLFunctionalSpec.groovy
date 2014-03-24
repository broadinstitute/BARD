package bard.db.people

import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import bard.db.registration.BardControllerFunctionalSpec
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Shared
import spock.lang.Unroll
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 7/2/13
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class PersonControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = getBaseUrl() + "person/"
    @Shared
    Map personId
    @Shared
    List<String> personUserNames = []

    def setupSpec() {
        String reauthenticateWithUser = BardControllerFunctionalSpec.ADMIN_USERNAME
        String adminRole = BardControllerFunctionalSpec.ADMIN_ROLE
        createPersonInDatabase(BardControllerFunctionalSpec.ADMIN_USERNAME, BardControllerFunctionalSpec.ADMIN_EMAIL, BardControllerFunctionalSpec.ADMIN_ROLE, reauthenticateWithUser)

        personId = (Map) BardControllerFunctionalSpec.remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Person person = Person.findByUserName(reauthenticateWithUser)
            Role role = Role.findByAuthority(adminRole)
            return [id: person.id, roleId: role.id]
        })
    }

    static void createPersonInDatabase(String teamuserName, String teamEmail, String teamRole, String reAuthenticateWith) {
        assert teamuserName != null

        BardControllerFunctionalSpec.remote.exec({
            SpringSecurityUtils.reauthenticate(reAuthenticateWith, null)
            Person person = Person.findByUserName(teamuserName)
            Role role = Role.findByAuthority(teamRole)
            if (!role) {
                role = Role.build(authority: teamRole, displayName: teamRole).save(flush: true)
            }
            if (!person) {
                person = Person.build(userName: teamuserName, emailAddress: teamEmail, fullName: 'fullName',
                        dateCreated: new Date()).save(flush: true)
            }
            PersonRole personRole = PersonRole.findByPersonAndRole(person, role)
            if (!personRole) {
                PersonRole.build(role: role, person: person).save(flush: true)
            }
            return true
        })
    }

    def cleanupSpec() {

        Sql sql = Sql.newInstance(BardControllerFunctionalSpec.dburl, BardControllerFunctionalSpec.dbusername,
                BardControllerFunctionalSpec.dbpassword, BardControllerFunctionalSpec.driverClassName)
        sql.call("{call bard_context.set_username(?)}", [BardControllerFunctionalSpec.ADMIN_USERNAME])

        for (String username : personUserNames) {
            sql.eachRow('select PERSON_ID from PERSON WHERE USERNAME=:CURRENT_USER_NAME', [CURRENT_USER_NAME: username]) { row ->
                sql.execute("DELETE FROM PERSON_ROLE WHERE PERSON_ID=${row.PERSON_ID}")
            }
            sql.execute("DELETE FROM PERSON WHERE USERNAME=${username}")
        }
    }

    def 'test save #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        String username = "Some_userName_${System.currentTimeMillis()}"
        String email = "abc@email.com"
        String displayName = "Some Display Name"
        Long roleId = personId.roleId
        when:
        client.post() {
            urlenc username: username, email: email, displayName: displayName
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc           | team              | teamPassword      | expectedHttpResponse
        "User A_1"     | BardControllerFunctionalSpec.TEAM_A_1_USERNAME | BardControllerFunctionalSpec.TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User Curator" | BardControllerFunctionalSpec.CURATOR_USERNAME  | BardControllerFunctionalSpec.CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }


    def 'test update #desc - forbidden'() {
        given:
        Map m = getCurrentPersonProperties()
        String username = m.usename
        String email = m.email
        String displayName = m.displayName
        Long version = m.version
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)

        when:
        client.post() {
            urlenc username: username, email: email, displayName: displayName, version: version
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc           | team              | teamPassword      | expectedHttpResponse
        "User A_1"     | BardControllerFunctionalSpec.TEAM_A_1_USERNAME | BardControllerFunctionalSpec.TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User Curator" | BardControllerFunctionalSpec.CURATOR_USERNAME  | BardControllerFunctionalSpec.CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test update #desc'() {
        given:
        Map m = getCurrentPersonProperties()
        String username = m.username
        String email = m.email
        String displayName = m.displayName
        Long version = m.version
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)

        when:
        final Response response = client.post() {
            urlenc username: username, email: email, displayName: displayName, version: version
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | BardControllerFunctionalSpec.ADMIN_USERNAME | BardControllerFunctionalSpec.ADMIN_PASSWORD | HttpServletResponse.SC_FOUND
    }

    def 'test save #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        String username = "Some_userName_${System.currentTimeMillis()}"
        String email = "${username}@gmail.com"
        String displayName = "Some Display Name"
        Long roleId = personId.roleId
        personUserNames.add(username)
        when:
        final Response response = client.post() {
            urlenc username: username, email: email, displayName: displayName
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | BardControllerFunctionalSpec.ADMIN_USERNAME | BardControllerFunctionalSpec.ADMIN_PASSWORD | HttpServletResponse.SC_FOUND
    }

    def 'test edit #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "edit/${personId.id}", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc           | team              | teamPassword      | expectedHttpResponse
        "User A_1"     | BardControllerFunctionalSpec.TEAM_A_1_USERNAME | BardControllerFunctionalSpec.TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User Curator" | BardControllerFunctionalSpec.CURATOR_USERNAME  | BardControllerFunctionalSpec.CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }


    def 'test edit #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "edit/${personId.id}", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | BardControllerFunctionalSpec.ADMIN_USERNAME | BardControllerFunctionalSpec.ADMIN_PASSWORD | HttpServletResponse.SC_OK
    }

    def 'test index #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc           | team              | teamPassword      | expectedHttpResponse
        "User A_1"     | BardControllerFunctionalSpec.TEAM_A_1_USERNAME | BardControllerFunctionalSpec.TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User Curator" | BardControllerFunctionalSpec.CURATOR_USERNAME  | BardControllerFunctionalSpec.CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test index #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | BardControllerFunctionalSpec.ADMIN_USERNAME | BardControllerFunctionalSpec.ADMIN_PASSWORD | HttpServletResponse.SC_FOUND
    }

    def 'test list #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "list", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc           | team              | teamPassword      | expectedHttpResponse
        "User A_1"     | BardControllerFunctionalSpec.TEAM_A_1_USERNAME | BardControllerFunctionalSpec.TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User Curator" | BardControllerFunctionalSpec.CURATOR_USERNAME  | BardControllerFunctionalSpec.CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test list #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "list", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | BardControllerFunctionalSpec.ADMIN_USERNAME | BardControllerFunctionalSpec.ADMIN_PASSWORD | HttpServletResponse.SC_OK
    }

    private Map getCurrentPersonProperties() {
        long id = personId.id
        Map currentDataMap = (Map) BardControllerFunctionalSpec.remote.exec({
            Person person = Person.findById(id)
            return [username: person.userName, version: person.version, email: person.emailAddress, displayName: person.fullName]
        })
        return currentDataMap
    }

}