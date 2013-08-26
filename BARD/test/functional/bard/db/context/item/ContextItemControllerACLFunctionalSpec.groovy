package bard.db.context.item

import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
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
class ContextItemControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = getBaseUrl() + "contextItem/"

    @Shared
    Map contextItemData

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




        contextItemData = (Map) remote.exec({
            //Build assay as TEAM_A
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)

            final String elementLabel = "My Cool Label"
            final Element element = Element.findByLabel(elementLabel)
            if (!element) {
                element = Element.build(label: elementLabel).save(flush: true)
            }

            long attributeElementId = element.id

            ProjectContextItem projectContextItem = ProjectContextItem.build(attributeElement: element)
            AssayContextItem assayContextItem = AssayContextItem.build(attributeElement: element)

            Assay assay = Assay.build(assayName: "Assay Name10").save(flush: true)
            AssayContext assayContext = AssayContext.build(assay: assay, contextName: "alpha").save(flush: true)

            Project project = Project.build(name: "Proj Name10").save(flush: true)
            ProjectContext projectContext = ProjectContext.build(project: project, contextName: "projectAlpha").save(flush: true)


            return [
                    assayId: assay.id,
                    projectId: project.id,
                    assayContextId: assayContext.id,
                    projectContextId: projectContext.id,
                    attributeElementId: attributeElementId,
                    assayContextItemId: assayContextItem.id,
                    projectContextItemId: projectContextItem.id
            ]
        })
        assayIdList.add(contextItemData.assayId)
        projectIdList.add(contextItemData.projectId)


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
            sql.execute("DELETE FROM PROJECT_CONTEXT WHERE PROJECT_ID=${projectId}")
            sql.execute("DELETE FROM PROJECT WHERE PROJECT_ID=${projectId}")
        }
    }

    def 'test index #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        client.get()
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_NOT_FOUND
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_NOT_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_NOT_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_NOT_FOUND
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_NOT_FOUND
    }

    def 'test edit Assay Context Item #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "edit", team, teamPassword)
        String contextClass = "AssayContext"
        Long contextItemId = contextItemData.assayContextItemId
        when:
        def response = client.post() {
            urlenc contextClass: contextClass, contextItemId: contextItemId
        }
        then:
        response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test edit Project Context Item #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "edit", team, teamPassword)
        String contextClass = "ProjectContext"
        Long contextItemId = contextItemData.projectContextItemId
        when:
        def response = client.post() {
            urlenc contextClass: contextClass, contextItemId: contextItemId
        }
        then:
        response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def "test create Assay Context Item #desc"() {
        given:
        Long contextId = contextItemData.assayContextId
        String contextClass = "AssayContext"
        Long contextOwnerId = contextItemData.assayId
        RESTClient client = getRestClient(controllerUrl, "create", team, teamPassword)
        when:
        def response = client.post() {
            urlenc contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    Map createAssayContextItem() {

        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            final String elementLabel = "My Cool Label"
            final Element element = Element.findByLabel(elementLabel)
            if (!element) {
                element = Element.build(label: elementLabel).save(flush: true)
            }

            long attributeElementId = element.id


            Assay assay = Assay.build().save(flush: true)

            AssayContext context = AssayContext.build(contextType: ContextType.BIOLOGY, assay: assay, contextName: "alpha2" + System.currentTimeMillis()).save(flush: true)

            AssayContextItem assayContextItem = AssayContextItem.build(assayContext: context, attributeElement: element).save(flush: true)

            return [contextType: context.contextType, version: assayContextItem.version, assayId: assay.id,
                    assayContextItemId: assayContextItem.id, attributeElementId: attributeElementId, contextId: context.id]
        })

        assayIdList.add(m.assayId)
        return m

    }

    Map createProjectContextItem() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME
        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            final String elementLabel = "My Cool Label"
            final Element element = Element.findByLabel(elementLabel)
            if (!element) {
                element = Element.build(label: elementLabel).save(flush: true)
            }

            long attributeElementId = element.id


            Project project = Project.build().save(flush: true)

            ProjectContext context = ProjectContext.build(contextType: ContextType.BIOLOGY, project: project, contextName: "alpha22" + System.currentTimeMillis()).save(flush: true)

            ProjectContextItem projectContextItem = ProjectContextItem.build(context: context, attributeElement: element).save(flush: true)

            return [contextType: context.contextType, version: projectContextItem.version, projectId: project.id,
                    projectContextItemId: projectContextItem.id, attributeElementId: attributeElementId, contextId: context.id]
        })

        projectIdList.add(m.projectId)
        return m;

    }

    def "test update Project Context Item #desc"() {

        given:
        final Map currentItem = createProjectContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "ProjectContext"
        Long contextOwnerId = currentItem.projectId
        Long contextItemId = currentItem.projectContextItemId
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)
        when:
        def response = client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }


    def "test update Project Context Item #desc forbidden"() {
        given:
        final Map currentItem = createProjectContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "ProjectContext"
        Long contextOwnerId = currentItem.assayId
        Long contextItemId = currentItem.projectContextItemId
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)
        when:
        response = client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }

        then:
        //Returns a 500 in this test because we are not doing any redirects. In the real app though, you would be forwarded to a 403 page
        //because the redirect that is mapped in the URLMappings file occurs
        //Perhaps we should catch and handle the exception on the page like we do for other controllers

        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    def "test update Assay Context Item #desc"() {

        given:
        final Map currentItem = createAssayContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "AssayContext"
        Long contextOwnerId = currentItem.assayId
        Long contextItemId = currentItem.assayContextItemId
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)
        when:
        def response = client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }


    def "test update Assay Context Item #desc forbidden"() {
        given:
        final Map currentItem = createAssayContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "AssayContext"
        Long contextOwnerId = currentItem.assayId
        Long contextItemId = currentItem.assayContextItemId
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)
        when:
        response = client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }

        then:
        //Returns a 500 in this test because we are not doing any redirects. In the real app though, you would be forwarded to a 403 page
        //because the redirect that is mapped in the URLMappings file occurs
        //Perhaps we should catch and handle the exception on the page like we do for other controllers

        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    def "test delete Project Context Item #desc"() {

        given:
        final Map currentItem = createProjectContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "ProjectContext"
        Long contextOwnerId = currentItem.projectId
        Long contextItemId = currentItem.projectContextItemId
        RESTClient client = getRestClient(controllerUrl, "delete", team, teamPassword)
        when:
        def response = client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }


    def "test delete Project Context Item #desc forbidden"() {
        given:
        final Map currentItem = createProjectContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "ProjectContext"
        Long contextOwnerId = currentItem.assayId
        Long contextItemId = currentItem.projectContextItemId
        RESTClient client = getRestClient(controllerUrl, "delete", team, teamPassword)
        when:
        client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }

        then:
        //Returns a 500 in this test because we are not doing any redirects. In the real app though, you would be forwarded to a 403 page
        //because the redirect that is mapped in the URLMappings file occurs
        //Perhaps we should catch and handle the exception on the page like we do for other controllers

        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    def "test delete Assay Context Item #desc"() {

        given:
        final Map currentItem = createAssayContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "AssayContext"
        Long contextOwnerId = currentItem.assayId
        Long contextItemId = currentItem.assayContextItemId
        RESTClient client = getRestClient(controllerUrl, "delete", team, teamPassword)
        when:
        def response = client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }


    def "test delete Assay Context Item #desc forbidden"() {
        given:
        final Map currentItem = createAssayContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "AssayContext"
        Long contextOwnerId = currentItem.assayId
        Long contextItemId = currentItem.assayContextItemId
        RESTClient client = getRestClient(controllerUrl, "delete", team, teamPassword)
        when:
        client.post() {
            urlenc attributeElementId: currentItem.attributeElementId, contextItemId: contextItemId, contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId, version: currentItem.version
        }

        then:
        //Returns a 500 in this test because we are not doing any redirects. In the real app though, you would be forwarded to a 403 page
        //because the redirect that is mapped in the URLMappings file occurs
        //Perhaps we should catch and handle the exception on the page like we do for other controllers

        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    def "test update Preferred Assay Context  Name #desc forbidden"() {
        given:
        final Map currentItem = createAssayContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "AssayContext"
        RESTClient client = getRestClient(controllerUrl, "updatePreferredName", team, teamPassword)
        when:
        client.post() {
            urlenc name: contextClass, pk: contextId, value: "My New Context Name"

        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }


    def "test update Preferred Project Context  Name #desc"() {

        given:
        final Map currentItem = createProjectContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "ProjectContext"
        RESTClient client = getRestClient(controllerUrl, "updatePreferredName", team, teamPassword)
        when:
        def response = client.post() {
            urlenc name: contextClass, pk: contextId, value: "My New Context Name"
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def "test update Preferred Project Context  Name #desc forbidden"() {
        given:
        final Map currentItem = createProjectContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "ProjectContext"
        RESTClient client = getRestClient(controllerUrl, "updatePreferredName", team, teamPassword)
        when:
        client.post() {
            urlenc name: contextClass, pk: contextId, value: "My New Context Name"
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def "test update Preferred Assay Context  Name #desc"() {

        given:
        final Map currentItem = createAssayContextItem()
        Long contextId = currentItem.contextId
        String contextClass = "AssayContext"
        RESTClient client = getRestClient(controllerUrl, "updatePreferredName", team, teamPassword)
        when:
        def response = client.post() {


            urlenc name: contextClass, pk: contextId, value: "My New Context Name"
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }




    def "test create Assay Context Item #desc forbidden"() {
        given:
        Long contextId = contextItemData.assayContextId
        String contextClass = "AssayContext"
        Long contextOwnerId = contextItemData.assayId
        RESTClient client = getRestClient(controllerUrl, "create", team, teamPassword)
        when:

        client.post() {
            urlenc contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def "test create Project Context Item #desc"() {
        given:
        Long contextId = contextItemData.projectContextId
        String contextClass = "ProjectContext"
        Long contextOwnerId = contextItemData.projectId
        RESTClient client = getRestClient(controllerUrl, "create", team, teamPassword)
        when:
        def response = client.post() {
            urlenc contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def "test create Project Context Item #desc forbidden"() {
        given:
        Long contextId = contextItemData.projectContextId
        String contextClass = "ProjectContext"
        Long contextOwnerId = contextItemData.projectId
        RESTClient client = getRestClient(controllerUrl, "create", team, teamPassword)
        when:

        client.post() {
            urlenc contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def "test save Project Context Item #desc forbidden"() {
        given:
        Long contextId = contextItemData.projectContextId
        String contextClass = "ProjectContext"
        Long contextOwnerId = contextItemData.projectId
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:

        client.post() {
            urlenc contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def "test save Assay Context Item #desc forbidden"() {
        given:
        Long contextId = contextItemData.assayContextId
        String contextClass = "AssayContext"
        Long contextOwnerId = contextItemData.assayId
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:

        client.post() {
            urlenc contextClass: contextClass, contextOwnerId: contextOwnerId, contextId: contextId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def "test save Assay Context Item #desc"() {
        given:
        Long contextId = contextItemData.assayContextId
        Long attributeElementId = contextItemData.attributeElementId
        def valueNum = 0.1234567

        String contextClass = "AssayContext"

        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:

        Response response = client.post() {
            urlenc contextClass: contextClass, contextId: contextId, valueNum: valueNum, attributeElementId: attributeElementId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def "test save Project Context Item #desc"() {
        given:
        Long contextId = contextItemData.projectContextId
        Long attributeElementId = contextItemData.attributeElementId
        def valueNum = 0.1234567

        String contextClass = "ProjectContext"
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:

        Response response = client.post() {
            urlenc contextClass: contextClass, contextId: contextId, valueNum: valueNum, attributeElementId: attributeElementId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }
}
