package bard.db.registration

import bard.db.enums.ExperimentStatus
import bard.db.experiment.Experiment
import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import bard.db.project.ExperimentController
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
class ExperimentControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = baseUrl+ "experiment/"

    @Shared
    Map experimentData

    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished

    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        createTeamsInDatabase(TEAM_A_1_USERNAME, TEAM_A_1_EMAIL, TEAM_A_1_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(TEAM_A_2_USERNAME, TEAM_A_2_EMAIL, TEAM_A_2_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(TEAM_B_1_USERNAME, TEAM_B_1_EMAIL, TEAM_B_1_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(CURATOR_USERNAME, CURATOR_EMAIL, CURATOR_ROLE, reauthenticateWithUser)




        experimentData = (Map) remote.exec({
            //Build assay as TEAM_A
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Assay assay = Assay.build(assayName: "Assay Name10").save(flush: true)
            Experiment experiment = Experiment.build(assay: assay).save(flush: true)
            return [id: experiment.id, experimentName: experiment.experimentName, assayName: assay.assayName, assayId: assay.id]
        })
        assayIdList.add(experimentData.assayId)


    }     // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])
        if (experimentData?.id) {
            sql.execute("DELETE FROM EXPRMT_CONTEXT WHERE EXPERIMENT_ID=${experimentData.id}")
            sql.execute("DELETE FROM EXPRMT_MEASURE WHERE EXPERIMENT_ID=${experimentData.id}")
            sql.execute("DELETE FROM EXPERIMENT WHERE EXPERIMENT_ID=${experimentData.id}")
        }

        for (Long assayId : assayIdList) {

            sql.eachRow('select ASSAY_CONTEXT_ID from ASSAY_CONTEXT WHERE ASSAY_ID=:currentAssayId', [currentAssayId: assayId]) { row ->
                sql.execute("DELETE FROM ASSAY_CONTEXT_ITEM WHERE ASSAY_CONTEXT_ID=${row.ASSAY_CONTEXT_ID}")
            }

            sql.execute("DELETE FROM ASSAY_CONTEXT WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM MEASURE WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${assayId}")
        }


    }

    def 'test create #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "create", team, teamPassword)
        Long assayId = experimentData.assayId
        when:
        def response = client.post() {
            urlenc assayId: assayId
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test edit #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "edit", team, teamPassword)
        Long id = experimentData.id
        when:
        def response = client.post() {
            urlenc id: id
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test experimentStatus #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "experimentStatus", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.json


        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test show #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show", team, teamPassword)
        Long id = experimentData.id
        when:
        def response = client.post() {
            urlenc id: id
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }


    def 'test editHoldUntilDate #desc'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editHoldUntilDate", team, teamPassword)

        Long version = currentDataMap.version
        String newholdUntilDate = ExperimentController.inlineDateFormater.format(new Date())
        when:
        def response = client.post() {
            urlenc version: version, pk: id, value: newholdUntilDate
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test editHoldUntilDate #desc forbidden'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editHoldUntilDate", team, teamPassword)

        Long version = currentDataMap.version
        String newholdUntilDate = ExperimentController.inlineDateFormater.format(new Date())
        when:

        client.post() {
            urlenc version: version, pk: id, value: newholdUntilDate
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test editRunFromDate #desc'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editRunFromDate", team, teamPassword)

        Long version = currentDataMap.version
        String newRunFromDate = ExperimentController.inlineDateFormater.format(new Date())
        when:
        def response = client.post() {
            urlenc version: version, pk: id, value: newRunFromDate
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test editRunFromDate #desc forbidden'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editRunFromDate", team, teamPassword)

        Long version = currentDataMap.version
        String newRunFromDate = ExperimentController.inlineDateFormater.format(new Date())
        when:

        client.post() {
            urlenc version: version, pk: id, value: newRunFromDate
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }


    def 'test editRunToDate #desc'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editRunToDate", team, teamPassword)

        Long version = currentDataMap.version
        String newRunToDate = ExperimentController.inlineDateFormater.format(new Date())
        when:
        def response = client.post() {
            urlenc version: version, pk: id, value: newRunToDate
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test editRunToDate #desc forbidden'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editRunToDate", team, teamPassword)

        Long version = currentDataMap.version
        String newRunToDate = ExperimentController.inlineDateFormater.format(new Date())
        when:

        client.post() {
            urlenc version: version, pk: id, value: newRunToDate
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test edit Description #desc'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editDescription", team, teamPassword)

        Long version = currentDataMap.version
        String newDescription = "Some Description"
        when:
        def response = client.post() {
            urlenc version: version, pk: id, value: newDescription
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test edit Description #desc forbidden'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editDescription", team, teamPassword)

        Long version = currentDataMap.version
        String newDescription = "Some Description"
        when:

        client.post() {
            urlenc version: version, pk: id, value: newDescription
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test edit Experiment Name #desc'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editExperimentName", team, teamPassword)

        Long version = currentDataMap.version
        String newExperimentName = "Some Name"
        when:
        def response = client.post() {
            urlenc version: version, pk: id, value: newExperimentName
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test edit experiment Name #desc forbidden'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editExperimentName", team, teamPassword)

        Long version = currentDataMap.version
        String newExperimentName = "Some Name"
        when:

        client.post() {
            urlenc version: version, pk: id, value: newExperimentName
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test editExperimentStatus #desc'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editExperimentStatus", team, teamPassword)

        Long version = currentDataMap.version
        String oldExperimentStatus = currentDataMap.experimentStatus
        String newExperimentStatus = null
        for (ExperimentStatus experimentStatus : ExperimentStatus.values()) {
            if (oldExperimentStatus != experimentStatus.id) {
                newExperimentStatus = experimentStatus.id
                break;
            }
        }
        when:

        def response = client.post() {
            urlenc version: version, pk: id, value: newExperimentStatus
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test editExperimentStatus - unauthorized #desc'() {
        given:
        long id = experimentData.id
        Map currentDataMap = getCurrentExperimentProperties()
        RESTClient client = getRestClient(controllerUrl, "editExperimentStatus", team, teamPassword)

        Long version = currentDataMap.version
        String oldExperimentStatus = currentDataMap.experimentStatus
        String newExperimentStatus = null
        for (ExperimentStatus experimentStatus : ExperimentStatus.values()) {
            if (oldExperimentStatus != experimentStatus.id) {
                newExperimentStatus = experimentStatus.id
                break;
            }
        }
        when:
        client.post() {
            urlenc version: version, pk: id, value: newExperimentStatus
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test save #desc'() {
        given:
        long assayId = experimentData.assayId
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)

        when:

        def response = client.post() {
            urlenc assayId: assayId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test save - unauthorized #desc'() {
        given:
        long assayId = experimentData.assayId
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:
        client.post() {
            urlenc assayId: assayId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test update #desc'() {
        given:
        long id = experimentData.id
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)

        when:

        def response = client.post() {
            urlenc id: id, experimentTree: "[]"
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }


    def 'test update - unauthorized #desc'() {
        given:
        long id = experimentData.id
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)
        when:
        client.post() {
            urlenc id: id, experimentTree: "[]"
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    private Map getCurrentExperimentProperties() {
        long id = (Long) experimentData.id
        Map currentDataMap = (Map) remote.exec({
            Experiment experiment = Experiment.findById(id)
            return [experimentName: experiment.experimentName, version: experiment.version, experimentStatus: experiment.experimentStatus.id]
        })
        return currentDataMap
    }
}
