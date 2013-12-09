package bard.db.project

import bard.db.dictionary.Element
import bard.db.dictionary.StageTree
import bard.db.enums.ContextType
import bard.db.enums.ProjectGroupType
import bard.db.enums.ProjectStatus
import bard.db.experiment.Experiment
import bard.db.people.Role
import bard.db.registration.BardControllerFunctionalSpec
import groovy.sql.Sql
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
    static final String controllerUrl = getBaseUrl() + "project/"

    @Shared
    Map projectData
    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished

    @Shared
    List<Long> projectIdList = []   //we keep ids of all projects here so we can delete after all the tests have finished


    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        projectData = (Map) remote.exec({
            //Build assay as TEAM_A

            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Role role = Role.findByAuthority('ROLE_TEAM_A')
            if (!role) {
                role = Role.build(authority: 'ROLE_TEAM_A', displayName: 'ROLE_TEAM_A').save(flush: true)
            }
            Role otherRole = Role.findByAuthority('ROLE_TEAM_B')
            if (!otherRole) {
                otherRole = Role.build(authority: 'ROLE_TEAM_B', displayName: 'ROLE_TEAM_B').save(flush: true)
            }
            Project project = Project.build(name: "Some Name2", ownerRole: role).save(flush: true)
            Element element = Element.findByLabel('ProjectControllerACLFunctionalSpec') ?: Element.build(label: 'ProjectControllerACLFunctionalSpec')
            StageTree.build(element: element).save(flush: true)

            //create assay context
            return [id: project.id, name: project.name, roleId: role.authority, otherRoleId: otherRole.authority]
        })
        projectIdList.add(projectData.id)

    }     // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])

        sql.execute("DELETE FROM STAGE_TREE")

        for (Long assayId : assayIdList) {

            sql.eachRow('select ASSAY_CONTEXT_ID from ASSAY_CONTEXT WHERE ASSAY_ID=:currentAssayId', [currentAssayId: assayId]) { row ->
                sql.execute("DELETE FROM ASSAY_CONTEXT_ITEM WHERE ASSAY_CONTEXT_ID=${row.ASSAY_CONTEXT_ID}")
            }

            sql.execute("DELETE FROM ASSAY_CONTEXT WHERE ASSAY_ID=${assayId}")
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



    def 'test save #desc'() {
        given:

        String name = "My Project Name_" + team
        String description = "Some Description"
        String status = ProjectStatus.DRAFT.id
        String groupType = ProjectGroupType.PROJECT.id
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:


        Response response = client.post() {
            urlenc name: name, description: description, projectStatus: status, projectGroupType: groupType, ownerRole: projectData.roleId
        }
        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }


    def 'test save, selected role not in users role list #desc'() {
        given:

        String name = "My Project Name_" + team
        String description = "Some Description"
        String status = ProjectStatus.DRAFT.id
        String groupType = ProjectGroupType.PROJECT.id
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:


        client.post() {
            urlenc name: name, description: description, projectStatus: status, projectGroupType: groupType, ownerRole: projectData.roleId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    def 'test edit Project Status #desc'() {
        given:
        Long pk = projectData.id
        String newStatus = ProjectStatus.APPROVED.id
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(controllerUrl, "editProjectStatus", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "editProjectStatus", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "editProjectName", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "editProjectName", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "editDescription", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "editDescription", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "getProjectNames", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "projectStatus", team, teamPassword)

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
        RESTClient client = getRestClient(controllerUrl, "projectStages", team, teamPassword)

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
        RESTClient client = getRestClient(controllerUrl, "groupType", team, teamPassword)

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
        RESTClient client = getRestClient(controllerUrl, "show/${projectData.id}", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.text.contains("Edit Contexts") == buttonExist
        assert response.text.contains("Add New Publication") == buttonExist


        where:
        desc                 | team              | teamPassword      | expectedHttpResponse      | buttonExist
        "Not Logged in User" | null              | null              | HttpServletResponse.SC_OK | false
        "User A_1"           | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK | true
        "User B"             | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK | false
        "User A_2"           | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK | true
        "ADMIN"              | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK | true
        "CURATOR"            | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK | false
    }


    def 'test reload project steps #desc'() {
        given:
        Map m = buildProjectExperiments(authority)
        RESTClient client = getRestClient(controllerUrl, "reloadProjectSteps", team, teamPassword)

        when:
        def response = client.post() {
            urlenc projectId: m.projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.text.contains("Link Experiments") == buttonExist

        where:
        desc       | team              | teamPassword      | authority     | expectedHttpResponse      | buttonExist
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK | true
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK | false
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK | true
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | "ROLE_TEAM_A" | HttpServletResponse.SC_OK | true
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | "ROLE_TEAM_A" | HttpServletResponse.SC_OK | false

    }

    def 'test update Project Stage #desc'() {
        given:
        Map m = buildProjectExperiments(authority)
        RESTClient client = getRestClient(controllerUrl, "updateProjectStage", team, teamPassword)
        String value = "secondary assay"
        Long projectExperimentId = m.peFromId
        when:
        def response = client.post() {
            urlenc pk: projectExperimentId, value: value
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | authority     | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | "ROLE_TEAM_A" | HttpServletResponse.SC_OK

    }


    def 'test remove Experiment From Project #desc'() {
        given:
        Map m = buildProjectExperiments(authority)
        RESTClient client = getRestClient(controllerUrl, "removeExperimentFromProject", team, teamPassword)
        when:
        def response = client.post() {
            urlenc experimentId: m.eFromId, projectId: m.projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | authority     | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | "ROLE_TEAM_A" | HttpServletResponse.SC_OK

    }


    def 'test remove Experiment From Project #desc forbidden'() {
        given:
        Map m = buildProjectExperiments(authority)
        RESTClient client = getRestClient(controllerUrl, "removeExperimentFromProject", team, teamPassword)
        when:
        client.post() {
            urlenc experimentId: m.eFromId, projectId: m.projectId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | authority     | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN
    }


    def 'test link Experiment #desc forbidden'() {
        given:
        Map m = buildExperimentsToLink(authority)

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(controllerUrl, "linkExperiment", team, teamPassword)
        when:
        client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | authority     | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test link Experiment #desc'() {
        given:
        Map m = buildExperimentsToLink(authority)

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(controllerUrl, "linkExperiment", team, teamPassword)
        when:
        def response = client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | authority     | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | "ROLE_TEAM_A" | HttpServletResponse.SC_OK

    }

    def 'test remove Edge From Project #desc'() {
        given:
        Map m = buildProjectExperiments(authority)

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(controllerUrl, "removeEdgeFromProject", team, teamPassword)
        when:
        def response = client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | authority     | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | "ROLE_TEAM_A" | HttpServletResponse.SC_OK

    }

    def 'test remove Edge From Project #desc forbidden'() {
        given:
        Map m = buildProjectExperiments(authority)

        Long fromExperimentId = m.eFromId
        Long toExperimentId = m.eToId
        Long projectId = m.projectId

        RESTClient client = getRestClient(controllerUrl, "removeEdgeFromProject", team, teamPassword)
        when:
        client.post() {
            urlenc fromExperimentId: fromExperimentId, toExperimentId: toExperimentId, projectId: projectId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc      | team              | teamPassword      | authority     | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test update Project Stage #desc forbidden'() {
        given:
        Map m = buildProjectExperiments(authority)
        RESTClient client = getRestClient(controllerUrl, "updateProjectStage", team, teamPassword)
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
        desc      | team              | teamPassword      | authority     | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | "ROLE_TEAM_A" | HttpServletResponse.SC_FORBIDDEN

    }



    def 'test show edit summary #desc'() {
        given:
        Long id = projectData.id
        RESTClient client = getRestClient(controllerUrl, "showEditSummary", team, teamPassword)
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
        String groupBySection = ContextType.BIOLOGY.id
        RESTClient client = getRestClient(controllerUrl, "editContext", team, teamPassword)
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
        RESTClient client = getRestClient(controllerUrl, "editSummary", team, teamPassword)

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
        RESTClient client = getRestClient(controllerUrl, "editSummary", team, teamPassword)
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

    def 'test edit owner admin #desc'() {
        Long pk = projectData.id
        String newRole = "ROLE_TEAM_B"
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(controllerUrl, "editOwnerRole", team, teamPassword)
        when:
        Response response = client.post() {
            urlenc pk: pk, version: version, value: newRole
        }
        then:
        assert response.statusCode == expectedHttpResponse
        JSONObject jsonObject = response.json
        assert jsonObject.get("modifiedBy")
        assert jsonObject.get("data") == newRole
        assert jsonObject.get("lastUpdated")
        assert jsonObject.get("version") != null

        where:
        desc    | team           | teamPassword   | expectedHttpResponse
        "ADMIN" | ADMIN_USERNAME | ADMIN_PASSWORD | HttpServletResponse.SC_OK
    }



    def 'test edit owner role, selected role not in users role list #desc'() {
        given:
        Long pk = projectData.id
        String newRole = "ROLE_TEAM_B"
        Long version = getCurrentProjectProperties().version
        RESTClient client = getRestClient(controllerUrl, "editOwnerRole", team, teamPassword)
        when:
        client.post() {
            urlenc pk: pk, version: version, value: newRole
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_BAD_REQUEST
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_BAD_REQUEST
    }

    String createUnAssociatedExperiment() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        return (String) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)

            Experiment experiment = Experiment.build(experimentName: reauthenticateWithUser + System.currentTimeMillis()).save(flush: true)
            return experiment.id + "-" + experiment.experimentName
        })

    }

    Map buildExperimentsToLink(String authority) {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Role role = Role.findByAuthority(authority)

            Project project = Project.build(ownerRole: role).save(flush: true)

            final String elementFromLabel = "primary assay"
            final String elementToLabel = "secondary assay"

            final Element fromElement = Element.findByLabel(elementFromLabel) ?: Element.build(label: elementFromLabel).save(flush: true)
            final Element toElement = Element.findByLabel(elementToLabel) ?: Element.build(label: elementToLabel).save(flush: true)

            StageTree.findByElement(fromElement) ?: StageTree.build(element: fromElement).save(flush: true)
            StageTree.findByElement(toElement) ?: StageTree.build(element: toElement).save(flush: true)


            Experiment experimentFrom = Experiment.build(ownerRole: role).save(flush: true)
            Experiment experimentTo = Experiment.build(ownerRole: role).save(flush: true)

            final ProjectSingleExperiment projectExperimentFrom = ProjectSingleExperiment.findByProjectAndExperiment(project, experimentFrom) ?:
                ProjectSingleExperiment.build(project: project, experiment: experimentFrom, stage: fromElement).save(flush: true)

            final ProjectSingleExperiment projectExperimentTo = ProjectSingleExperiment.findByProjectAndExperiment(project, experimentTo) ?:
                ProjectSingleExperiment.build(project: project, experiment: experimentTo, stage: toElement).save(flush: true)

            return [peFromId: projectExperimentFrom.id, peToId: projectExperimentTo.id, eFromId: experimentFrom.id, eToId: experimentTo.id, projectId: project.id]
        })
        projectIdList.add(m.projectId)
        return m

    }

    Map buildProjectExperiments(String authority) {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Role role = Role.findByAuthority(authority)
            Project project = Project.build(ownerRole: role).save(flush: true)

            final String elementFromLabel = "primary assay"
            final String elementToLabel = "secondary assay"
            final Element fromElement = Element.findByLabel(elementFromLabel) ?: Element.build(label: elementFromLabel).save(flush: true)
            final Element toElement = Element.findByLabel(elementToLabel) ?: Element.build(label: elementToLabel).save(flush: true)

            StageTree.findByElement(fromElement) ?: StageTree.build(element: fromElement).save(flush: true)
            StageTree.findByElement(toElement) ?: StageTree.build(element: toElement).save(flush: true)

            Experiment experimentFrom = Experiment.build(ownerRole: role).save(flush: true)
            Experiment experimentTo = Experiment.build(ownerRole: role).save(flush: true)

            final ProjectSingleExperiment projectExperimentFrom = ProjectSingleExperiment.findByProjectAndExperiment(project, experimentFrom) ?:
                ProjectSingleExperiment.build(project: project, experiment: experimentFrom, stage: fromElement).save(flush: true)

            final ProjectSingleExperiment projectExperimentTo = ProjectSingleExperiment.findByProjectAndExperiment(project, experimentTo) ?:
                ProjectSingleExperiment.build(project: project, experiment: experimentTo, stage: toElement).save(flush: true)

            final ProjectStep projectStep = ProjectStep.findByPreviousProjectExperimentAndNextProjectExperiment(projectExperimentFrom, projectExperimentTo) ?:
                ProjectStep.build(previousProjectExperiment: projectExperimentFrom, nextProjectExperiment: projectExperimentTo).save(flush: true)

            projectExperimentFrom.save(flush: true)
            projectExperimentTo.save(flush: true)

            return [peFromId: projectExperimentFrom.id, peToId: projectExperimentTo.id, eFromId: experimentFrom.id, eToId:
                    experimentTo.id, projectId: project.id, fromElementId: fromElement.id, toElementId: toElement.id, roleId: role.id]
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
