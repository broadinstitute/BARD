package bard.db.registration

import bard.db.experiment.Experiment
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
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
class SplitAssayDefintionControllerFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = baseUrl + "splitAssayDefinition/"

    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished
    @Shared
    Map experimentData = [:]

    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        createTeamsInDatabase(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_ROLE, reauthenticateWithUser)



        Map assayData = (Map) remote.exec({
            //Create two assays
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)

            Assay assay1 = Assay.build(assayName: "Assay Name10").save(flush: true)
            String experimentsAlias = "experiment1"
            final Experiment experiment1 = Experiment.build(experimentName: experimentsAlias, assay: assay1, capPermissionService: null).save(flush: true)

            Assay assay2 = Assay.build(assayName: "Assay Name10").save(flush: true)
            experimentsAlias = "experiment2"
            final Experiment experiment2 = Experiment.build(experimentName: experimentsAlias, assay: assay2, capPermissionService: null).save(flush: true)

            return [assayId1: assay1.id, assayId2: assay2.id, experimentId1: experiment1.id, experimentId2: experiment2.id]
        })
        experimentData.put("experimentId1", assayData.experimentId1)
        experimentData.put("experimentId2", assayData.experimentId2)
        assayIdList.add(assayData.assayId1)
        assayIdList.add(assayData.assayId2)


    }     // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])

        sql.execute("DELETE FROM EXTERNAL_REFERENCE WHERE EXPERIMENT_ID=${experimentData.experimentId1}")
        sql.execute("DELETE FROM EXTERNAL_REFERENCE WHERE EXPERIMENT_ID=${experimentData.experimentId2}")
        sql.execute("DELETE FROM EXPRMT_MEASURE WHERE EXPERIMENT_ID=${experimentData.experimentId1}")
        sql.execute("DELETE FROM EXPRMT_MEASURE WHERE EXPERIMENT_ID=${experimentData.experimentId2}")
        sql.execute("DELETE FROM EXPRMT_CONTEXT WHERE EXPERIMENT_ID=${experimentData.experimentId1}")
        sql.execute("DELETE FROM EXPRMT_CONTEXT WHERE EXPERIMENT_ID=${experimentData.experimentId2}")
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

    def 'test show #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_OK

    }

    def 'test show - forbidden  #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show", team, teamPassword)

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


    def 'test selectExperimentsToMove - exceptions #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "selectExperimentsToMove", team, teamPassword)

        when:
        client.post() {
            urlenc assayId: assayId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                 | team           | teamPassword   | expectedHttpResponse               | assayId
        "No Assay Id"        | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_BAD_REQUEST | -1
        "Assay Id not found" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_BAD_REQUEST | null
    }

    def 'test selectExperimentsToMove - forbidden  #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "selectExperimentsToMove", team, teamPassword)

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

    def 'test selectExperimentsToMove #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "selectExperimentsToMove", team, teamPassword)

        when:
        def response = client.post() {
            urlenc assayId: assayId
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc          | team           | teamPassword   | expectedHttpResponse      | assayId
        "Valid Assay" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_OK | assayIdList.get(0)

    }


    def 'test splitExperiments #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "splitExperiments", team, teamPassword)

        when:
        def response = client.post() {
            urlenc "assay.id": assayId, experimentIds: experimentData.experimentId1
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc          | team           | teamPassword   | expectedHttpResponse      | assayId
        "Valid Assay" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_OK | assayIdList.get(0)

    }

    def 'test splitExperiments - forbidden  #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "splitExperiments", team, teamPassword)

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
