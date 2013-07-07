package bard.db.registration

import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Shared
import spock.lang.Unroll
import wslite.json.JSONArray
import wslite.rest.RESTClient
import wslite.rest.Response

import javax.servlet.http.HttpServletResponse

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 7/2/13
 * Time: 9:16 PM
 * To change this template use File | Settings | File Templates.
 *
 *  TEAM_A_1 and TEAM_A_2 belong to the same group
 *
 *  TEAM_B_1 belong to a different group
 *
 *  TEAM_A_1 also has ROLE_CURATOR
 *
 *
 *
 */
@Unroll
class AssayJSonControllerFunctionalSpec extends BardControllerFunctionalSpec {

    static final String controllerUrl = baseUrl +  "assayJSon/"
    @Shared
    Map assayData

    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME
        assayData = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Assay assay1 = Assay.build(assayName: "Assay Name101", assayShortName: "Assay Short Name").save(flush: true)
            Assay assay2 = Assay.build(assayName: "Assay Name102", assayShortName: "Assay Short Name").save(flush: true)
            Assay assay3 = Assay.build(assayName: "Assay Name103", assayShortName: "Assay Short Name").save(flush: true)
            return [shortName: assay1.assayShortName, ids: [assay1.id, assay2.id, assay3.id]]
        })
    }     // run before the first feature method
    def cleanupSpec() {

        def sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])
        for (Long id : assayData.ids) {
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${id}")
        }

    }

    def 'test index #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FOUND
    }

    def 'test getNames #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "", team, teamPassword)

        when:
        final Response response = client.get(path: '/getNames', query: [term: assayData.shortName, include_entities: true])
        then:
        assert response.statusCode == expectedHttpResponse
        assert response.contentType == "application/json"
        assert response.json instanceof JSONArray
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }
}
