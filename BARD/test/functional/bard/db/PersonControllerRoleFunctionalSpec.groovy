package bard.db

import bard.db.people.Person
import bard.db.registration.BardControllerFunctionalSpec
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
class PersonControllerRoleFunctionalSpec extends BardControllerFunctionalSpec {
    static final String baseUrl = remote { ctx.grailsApplication.config.tests.server.url } + "person/"
    @Shared
    Map personId

    def setupSpec() {
        String reauthenticateWithUser = ADMIN_USERNAME
        createTeamsInDatabase(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_ROLE, reauthenticateWithUser)

        personId = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Person person = Person.findByUserName(reauthenticateWithUser)
            return [id: person.id]
        })
    }

    def 'test edit #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(baseUrl, "edit/${personId.id}", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc           | team              | teamPassword      | expectedHttpResponse
        "User A_1"     | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User Curator" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test edit #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "edit/${personId.id}", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_OK
    }

    def 'test list #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(baseUrl, "list", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc           | team              | teamPassword      | expectedHttpResponse
        "User A_1"     | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User Curator" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test list #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "list", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_OK
    }

//    def 'test update #desc - forbidden'() {
//        given:
//        RESTClient client = getRestClient(baseUrl, "update", team, teamPassword)
//
//        when:
//        client.post() {
//            urlenc elementHierarchyIdList: "3", elementId: "2", newPathString: "newPathString"
//        }
//        then:
//        def ex = thrown(RESTClientException)
//        assert ex.response.statusCode == expectedHttpResponse
//        where:
//        desc       | team              | teamPassword      | expectedHttpResponse
//        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
//        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
//        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FORBIDDEN
//
//    }
//
//
//    def 'test update #desc'() {
//        given:
//        RESTClient client = getRestClient(baseUrl, "update", team, teamPassword)
//        String newPathString = "a/b/c"
//        when:
//        final Response response = client.post() {
//            urlenc elementHierarchyIdList: "0", elementId: "0", newPathString: newPathString
//        }
//        then:
//        assert response.statusCode == expectedHttpResponse
//        where:
//        desc      | team             | teamPassword     | expectedHttpResponse
//        "CURATOR" | CURATOR_USERNAME | CURATOR_PASSWORD | HttpServletResponse.SC_OK
//        "ADMIN"   | ADMIN_USERNAME   | ADMIN_PASSWORD   | HttpServletResponse.SC_OK
//    }

    // run before the first feature method
    def cleanupSpec() {

//        Sql sql = Sql.newInstance(dburl, dbusername,
//                dbpassword, driverClassName)
//        sql.call("{call bard_context.set_username(?)}", [CURATOR_USERNAME])
//        for (Long elementId : elementIdList) {
//            sql.execute("DELETE FROM ELEMENT_HIERARCHY WHERE PARENT_ELEMENT_ID=${elementId} OR CHILD_ELEMENT_ID=${elementId}")
//            sql.execute("DELETE FROM ELEMENT WHERE ELEMENT_ID=${elementId}")
//        }
    }

}
