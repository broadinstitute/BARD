package bard.db.project

import bard.db.dictionary.Element
import bard.db.enums.ProjectGroupType
import bard.db.enums.ProjectStatus
import bard.db.experiment.Experiment
import bard.db.registration.BardControllerFunctionalSpec
import groovy.sql.Sql
import org.apache.commons.lang3.StringUtils
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Shared
import spock.lang.Unroll
import wslite.json.JSONObject
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
class ProjectControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String baseUrl = remote { ctx.grailsApplication.config.tests.server.url } + "project/"

    @Shared
    Map projectData
    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished

    @Shared
    List<Long> projectIdList = []   //we keep ids of all projects here so we can delete after all the tests have finished


    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        createTeamsInDatabase(TEAM_A_1_USERNAME, TEAM_A_1_EMAIL, TEAM_A_1_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(TEAM_A_2_USERNAME, TEAM_A_2_EMAIL, TEAM_A_2_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(TEAM_B_1_USERNAME, TEAM_B_1_EMAIL, TEAM_B_1_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(ADMIN_USERNAME, ADMIN_EMAIL, ADMIN_ROLE, reauthenticateWithUser)

        createTeamsInDatabase(CURATOR_USERNAME, CURATOR_EMAIL, CURATOR_ROLE, reauthenticateWithUser)




        projectData = (Map) remote.exec({
            //Build assay as TEAM_A
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Project project = Project.build(name: "Some Name").save(flush: true)

            //create assay context
            return [id: project.id, name: project.name]
        })
        projectIdList.add(projectData.id)

    }     // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])

        for (Long assayId : assayIdList) {

            sql.eachRow('select ASSAY_CONTEXT_ID from ASSAY_CONTEXT WHERE ASSAY_ID=:currentAssayId', [currentAssayId: assayId]) { row ->
                sql.execute("DELETE FROM ASSAY_CONTEXT_ITEM WHERE ASSAY_CONTEXT_ID=${row.ASSAY_CONTEXT_ID}")
            }

            sql.execute("DELETE FROM ASSAY_CONTEXT WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM MEASURE WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${assayId}")
        }
        for (Long projectId : projectIdList) {
            sql.eachRow('select PROJECT_CONTEXT_ID from PROJECT_CONTEXT WHERE PROJECT_ID=:currentProjectId', [currentProjectId: projectId]) { row ->
                sql.execute("DELETE FROM PROJECT_CONTEXT_ITEM WHERE PROJECT_CONTEXT_ID=${row.PROJECT_CONTEXT_ID}")
            }
            sql.eachRow('select PROJECT_EXPERIMENT_ID from PROJECT_EXPERIMENT WHERE PROJECT_ID=:currentProjectId', [currentProjectId: projectId]) { row ->
                sql.execute("DELETE FROM PROJECT_STEP WHERE NEXT_PROJECT_EXPERIMENT_ID=${row.PROJECT_EXPERIMENT_ID} OR PREV_PROJECT_EXPERIMENT_ID=${row.PROJECT_EXPERIMENT_ID}")
            }
            sql.execute("DELETE FROM PROJECT_EXPERIMENT WHERE PROJECT_ID=${projectId}")
            sql.execute("DELETE FROM PROJECT_EXPERIMENT WHERE PROJECT_ID=${projectId}")
            sql.execute("DELETE FROM PROJECT_CONTEXT WHERE PROJECT_ID=${projectId}")
            sql.execute("DELETE FROM PROJECT WHERE PROJECT_ID=${projectId}")
        }
    }

    def 'test create #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "create", team, teamPassword)

        when:
        final Response response = client.get()
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


    def 'test save #desc'() {
        given:

        String name = "My Project Name_" + team
        String description = "Some Description"
        String status = ProjectStatus.DRAFT.id
        String groupType = ProjectGroupType.PROJECT.id
        RESTClient client = getRestClient(baseUrl, "save", team, teamPassword)
        when:
        Response response = client.post() {
            urlenc name: name, description: description, status: status, groupType: groupType
        }
        then:
        assert response.statusCode == expectedHttpResponse
        JSONObject array = response.json
        String url = array.get("url")
        String projectId = StringUtils.substringAfterLast(url, "/").trim()
        projectIdList.add(new Long(projectId))
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test edit Project Status #desc'() {
        given:
        Long pk = projectData.id
        String newStatus = ProjectStatus.APPROVED.id
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(baseUrl, "editProjectStatus", team, teamPassword)
        when:
        Response response = client.post() {
            urlenc pk: pk, version: version, value: newStatus
        }
        then:
        assert response.statusCode == expectedHttpResponse
        JSONObject jsonObject = response.json
        assert jsonObject.get("modifiedBy")
        assert jsonObject.get("data") == newStatus
        assert jsonObject.get("lastUpdated")
        assert jsonObject.get("version") != null

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test edit Project Status #desc - Forbidden'() {
        given:
        Long pk = projectData.id
        String value = ProjectStatus.APPROVED.id
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(baseUrl, "editProjectStatus", team, teamPassword)
        when:
        client.post() {
            urlenc pk: pk, version: version, value: value
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test edit Project Name #desc'() {
        given:
        Long pk = projectData.id
        String newName = ProjectStatus.APPROVED.id + team
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(baseUrl, "editProjectName", team, teamPassword)
        when:
        Response response = client.post() {
            urlenc pk: pk, version: version, value: newName
        }
        then:
        assert response.statusCode == expectedHttpResponse
        JSONObject jsonObject = response.json
        assert jsonObject.get("modifiedBy")
        assert jsonObject.get("data") == newName
        assert jsonObject.get("lastUpdated")
        assert jsonObject.get("version") != null

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test edit Project Name #desc - Forbidden'() {
        given:
        Long pk = projectData.id
        String newName = ProjectStatus.APPROVED.id + team
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(baseUrl, "editProjectName", team, teamPassword)
        when:
        client.post() {
            urlenc pk: pk, version: version, value: newName
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }


    def 'test edit Project Description #desc'() {
        given:
        Long pk = projectData.id
        String newDescription = ProjectStatus.APPROVED.id + team
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(baseUrl, "editDescription", team, teamPassword)
        when:
        Response response = client.post() {
            urlenc pk: pk, version: version, value: newDescription
        }
        then:
        assert response.statusCode == expectedHttpResponse
        JSONObject jsonObject = response.json
        assert jsonObject.get("modifiedBy")
        assert jsonObject.get("data") == newDescription
        assert jsonObject.get("lastUpdated")
        assert jsonObject.get("version") != null

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test edit Project Description #desc - Forbidden'() {
        given:
        Long pk = projectData.id
        String newDescription = ProjectStatus.APPROVED.id + team
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(baseUrl, "editDescription", team, teamPassword)
        when:
        client.post() {
            urlenc pk: pk, version: version, value: newDescription
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test projectNames #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "getProjectNames", team, teamPassword)
        String projectName = projectData.name
        when:

        final Response response = client.post() {
            urlenc term: "${projectName}"
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

    def 'test projectStatus #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "projectStatus", team, teamPassword)

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

    def 'test projectStages #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "projectStages", team, teamPassword)

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

    def 'test groupType #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "groupType", team, teamPassword)

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
        RESTClient client = getRestClient(baseUrl, "show/${projectData.id}", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.text.contains("Edit Contexts") == buttonExist
        assert response.text.contains("Add New Publication") == buttonExist


        where:
        desc       | team              | teamPassword      | expectedHttpResponse      | buttonExist
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK | true
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK | false
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK | true
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK | true
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK | false
    }

    def 'test edit #desc'() {
        given:
        RESTClient client = getRestClient(baseUrl, "edit", team, teamPassword)
        Long projectId = projectData.id
        when:
        def response = client.post() {
            urlenc id: projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse


        where:
        desc       | team              | teamPassword      | expectedHttpResponse      | buttonExist
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK | true
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK | false
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK | true
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK | true
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK | false

    }

    def 'test findByName #desc'() {
        given:
        Map currentDataMap = getCurrentProjectProperties()
        RESTClient client = getRestClient(baseUrl, "findByName", team, teamPassword)
        String name = currentDataMap.name
        when:

        final Response response = client.post() {
            urlenc projectName: "${name}"
        }

        then:
        //Response depends on the number of projects with that name.
        // Earlier tests might have used the same name.
        // So to make the test not fail in a non-deterministic manner we check for both a redirect and an OK
        assert response.statusCode == HttpServletResponse.SC_OK || response.statusCode == HttpServletResponse.SC_FOUND

        where:
        desc       | team              | teamPassword
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD
    }


    def 'test findById #desc'() {
        given:

        RESTClient client = getRestClient(baseUrl, "findById/${projectData.id}", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.text.contains(team)

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test reload project steps #desc'() {
        given:
        Map m = buildProjectExperiments()
        RESTClient client = getRestClient(baseUrl, "reloadProjectSteps", team, teamPassword)

        when:
        def response = client.post() {
            urlenc projectId: m.projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.text.contains("Link Experiments") == buttonExist

        where:
        desc       | team              | teamPassword      | expectedHttpResponse      | buttonExist
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK | true
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK | false
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK | true
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK | true
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK | false

    }


    def 'test update Project Stage #desc'() {
        given:
        Map m = buildProjectExperiments()
        RESTClient client = getRestClient(baseUrl, "updateProjectStage", team, teamPassword)
        String value = "secondary assay"
        Long projectExperimentId = m.peFromId
        when:
        def response = client.post() {
            urlenc pk: projectExperimentId, value: value
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK

    }


    def 'test remove Experiment From Project #desc'() {
        given:
        Map m = buildProjectExperiments()
        RESTClient client = getRestClient(baseUrl, "removeExperimentFromProject", team, teamPassword)
        when:
        def response = client.post() {
            urlenc experimentId: m.eFromId, projectId: m.projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK

    }


    def 'test remove Experiment From Project #desc forbidden'() {
        given:
        Map m = buildProjectExperiments()
        RESTClient client = getRestClient(baseUrl, "removeExperimentFromProject", team, teamPassword)
        when:
        client.post() {
            urlenc experimentId: m.eFromId, projectId: m.projectId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }


    def 'test link Experiment #desc forbidden'() {
        given:
        Map m = buildExperimentsToLink()

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(baseUrl, "linkExperiment", team, teamPassword)
        when:
        client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test link Experiment #desc'() {
        given:
        Map m = buildExperimentsToLink()

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(baseUrl, "linkExperiment", team, teamPassword)
        when:
        def response = client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK

    }

    def 'test remove Edge From Project #desc'() {
        given:
        Map m = buildProjectExperiments()

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(baseUrl, "removeEdgeFromProject", team, teamPassword)
        when:
        def response = client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK

    }

    def 'test remove Edge From Project #desc forbidden'() {
        given:
        Map m = buildProjectExperiments()

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(baseUrl, "removeEdgeFromProject", team, teamPassword)
        when:
        client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test update Project Stage #desc forbidden'() {
        given:
        Map m = buildProjectExperiments()
        RESTClient client = getRestClient(baseUrl, "updateProjectStage", team, teamPassword)
        String value = "secondary assay"
        Long projectExperimentId = m.peFromId
        when:
        client.post() {
            urlenc pk: projectExperimentId, value: value
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test associate Experiments To Project #desc'() {

        given:
        String selectedExperiments = createUnAssociatedExperiment()
        RESTClient client = getRestClient(baseUrl, "associateExperimentsToProject", team, teamPassword)

        when:
        def response = client.post() {
            urlenc projectId: projectData.id, 'selectedExperiments[]': selectedExperiments
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK

    }

    def 'test associate Experiments To Project #desc forbidden'() {

        given:
        String selectedExperiments = createUnAssociatedExperiment()
        RESTClient client = getRestClient(baseUrl, "associateExperimentsToProject", team, teamPassword)
        when:
        client.post() {
            urlenc projectId: projectData.id, 'selectedExperiments[]': selectedExperiments
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }


    def 'test show edit summary #desc'() {
        given:
        Long id = projectData.id
        RESTClient client = getRestClient(baseUrl, "showEditSummary", team, teamPassword)
        when:
        Response response = client.post() {
            urlenc instanceId: id
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

    def 'test edit context #desc'() {
        given:
        Long id = projectData.id
        String groupBySection = "Some Section"
        RESTClient client = getRestClient(baseUrl, "editContext", team, teamPassword)
        when:
        Response response = client.post() {
            urlenc id: id, groupBySection: groupBySection
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

    def 'test edit Summary #desc'() {

        given:
        RESTClient client = getRestClient(baseUrl, "editSummary", team, teamPassword)

        when:
        def response = client.post() {
            urlenc instanceId: projectData.id, projectName: "My New Name", description: "My Description", projectStatus: ProjectStatus.DRAFT
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK

    }

    def 'test edit Summary #desc forbidden'() {

        given:
        RESTClient client = getRestClient(baseUrl, "editSummary", team, teamPassword)
        when:
        client.post() {
            urlenc instanceId: projectData.id, projectName: "My New Name", description: "My Description", projectStatus: ProjectStatus.DRAFT
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }
    // def editSummary(Long instanceId, String projectName, String description, String projectStatus) {

    String createUnAssociatedExperiment() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        return (String) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Experiment experiment = Experiment.build(experimentName: reauthenticateWithUser + System.currentTimeMillis()).save(flush: true)
            return experiment.id + "-" + experiment.experimentName
        })

    }

    Map buildExperimentsToLink() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Project project = Project.build().save(flush: true)

            final String elementFromLabel = "primary assay"
            final String elementToLabel = "secondary assay"

            Element fromElement = Element.findByLabel(elementFromLabel)
            if (!fromElement) {
                fromElement = Element.build(label: elementFromLabel).save(flush: true)
            }

            Element toElement = Element.findByLabel(elementToLabel)
            if (!toElement) {
                toElement = Element.build(label: elementToLabel).save(flush: true)
            }
            // StageTree stageTree1 = StageTree.build(element: fromElement).save(flush: true)
            //  StageTree stageTree2 = StageTree.build(element: toElement).save(flush: true)

            Experiment experimentFrom = Experiment.build().save(flush: true)
            Experiment experimentTo = Experiment.build().save(flush: true)

            ProjectExperiment projectExperimentFrom = ProjectExperiment.build(project: project, experiment: experimentFrom, stage: fromElement).save(flush: true)
            ProjectExperiment projectExperimentTo = ProjectExperiment.build(project: project, experiment: experimentTo, stage: toElement).save(flush: true)


            return [peFromId: projectExperimentFrom.id, peToId: projectExperimentTo.id, eFromId: experimentFrom.id, eToId: experimentTo.id, projectId: project.id]
        })
        projectIdList.add(m.projectId)
        return m

    }

    Map buildProjectExperiments() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Project project = Project.build().save(flush: true)

            final String elementFromLabel = "primary assay"
            final String elementToLabel = "secondary assay"

            Element fromElement = Element.findByLabel(elementFromLabel)
            if (!fromElement) {
                fromElement = Element.build(label: elementFromLabel).save(flush: true)
            }

            Element toElement = Element.findByLabel(elementToLabel)
            if (!toElement) {
                toElement = Element.build(label: elementToLabel).save(flush: true)
            }
            // StageTree stageTree1 = StageTree.build(element: fromElement).save(flush: true)
            //  StageTree stageTree2 = StageTree.build(element: toElement).save(flush: true)

            Experiment experimentFrom = Experiment.build().save(flush: true)
            Experiment experimentTo = Experiment.build().save(flush: true)

            ProjectExperiment projectExperimentFrom = ProjectExperiment.build(project: project, experiment: experimentFrom, stage: fromElement).save(flush: true)
            ProjectExperiment projectExperimentTo = ProjectExperiment.build(project: project, experiment: experimentTo, stage: toElement).save(flush: true)

            ProjectStep projectStep = ProjectStep.build(previousProjectExperiment: projectExperimentFrom, nextProjectExperiment: projectExperimentTo).save(flush: true)
            projectExperimentFrom.addToFollowingProjectSteps(projectStep)
            projectExperimentTo.addToPrecedingProjectSteps(projectStep)

            projectExperimentFrom.save(flush: true)
            projectExperimentTo.save(flush: true)

            return [peFromId: projectExperimentFrom.id, peToId: projectExperimentTo.id, eFromId: experimentFrom.id, eToId:
                    experimentTo.id, projectId: project.id, fromElementId: fromElement.id, toElementId: toElement.id]
        })
        projectIdList.add(m.projectId)
        return m

    }

    private Map getCurrentProjectProperties() {
        long id = projectData.id
        Map currentDataMap = (Map) remote.exec({
            Project project = Project.findById(id)
            return [name: project.name, version: project.version]
        })
        return currentDataMap
    }

}
