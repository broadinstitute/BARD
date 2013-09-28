package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.HierarchyType
import bard.db.experiment.Experiment
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Unroll
import wslite.rest.RESTClient
import wslite.rest.RESTClientException
import wslite.rest.Response

import javax.servlet.http.HttpServletResponse

/**
 *
 * Merging of Assays should only be done by Administrators
 *
 *
 *
 */
@Unroll
class MergeAssayDefintionControllerFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = getBaseUrl() + "mergeAssayDefinition/"

    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished

    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map assayData = (Map) remote.exec({
            //Create two assays
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)

            Assay assay1 = Assay.build(assayName: "Assay Name10").save(flush: true)

            //create assay context
            return [assayId1: assay1.id]
        })
        assayIdList.add(assayData.assayId1)

    }     // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])
        for (Long assayId : assayIdList) {
            sql.execute("DELETE FROM EXPERIMENT WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM ASSAY_CONTEXT WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM MEASURE WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${assayId}")
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
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_FOUND

    }

    def 'test index - forbidden  #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN


    }
}
