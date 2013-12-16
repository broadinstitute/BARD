package bard.db.registration

import bard.db.enums.Status
import bard.db.people.Role
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
class PanelControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = getBaseUrl() + "panel/"

    @Shared
    Map panelAssayData
    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished
    @Shared
    List<Long> panelIdList = []
    @Shared
    List<String> panelNames = []

    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        panelAssayData = (Map) remote.exec({
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

            Assay assay = Assay.build(assayName: "Assay Name10", ownerRole: role, assayStatus:Status.APPROVED).save(flush: true)
            Panel panel = Panel.build(name: "Panel Name", description: "Panel Name", ownerRole: role).save(flush: true)

            return [assayId: assay.id, panelId: panel.id, panelName: panel.name, roleId: role.authority, otherRoleId: otherRole.authority]
        })
        assayIdList.add(panelAssayData.assayId)
        panelIdList.add(panelAssayData.panelId)


    }     // run before the first feature method
    def cleanupSpec() {
        //if (panelAssayData?.id) {
        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])
        for (Long assayId : assayIdList) {
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${assayId}")
        }
        for (Long panelId : panelIdList) {
            sql.execute("DELETE FROM PANEL WHERE PANEL_ID=${panelId}")
        }
        for (String panelName : panelNames) {
            sql.execute("DELETE FROM PANEL WHERE NAME='" + panelName + "'")
        }
        // }
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


    def 'test show #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show/${panelAssayData.panelId}", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.text.contains(TEAM_A_1_USERNAME)

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
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        panelNames.add(panelName)
        when:
        def response = client.post() {
            urlenc name: panelName, description: "Some Description", ownerRole: panelAssayData.roleId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | panelName  | team              | teamPassword      | expectedHttpResponse
        "User A_1" | "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND

    }

    def 'test save, selected role not in users role list #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        panelNames.add(panelName)
        when:
        Response response = client.post() {
            urlenc name: panelName, description: "Some Description", ownerRole: panelAssayData.roleId
        }
        then:

        assert response.statusCode == expectedHttpResponse

        where:
        desc      | panelName | team             | teamPassword     | expectedHttpResponse
        "CURATOR" | "CURATOR" | CURATOR_USERNAME | CURATOR_PASSWORD | HttpServletResponse.SC_OK
    }

    def 'test edit owner admin #desc'() {
        Long pk = panelAssayData.panelId
        String newRole = "ROLE_TEAM_B"
        Long version = getCurrentPanelProperties().version
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
        Long pk = panelAssayData.panelId
        String newRole = "ROLE_TEAM_B"
        Long version = getCurrentPanelProperties().version
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

    def 'test addAssayToPanel #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "addAssayToPanel", team, teamPassword)
        long assayId = panelAssayData.assayId
        long panelId = panelAssayData.panelId
        when:
        def response = client.post() {
            urlenc assayIds: assayId, id: panelId
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

    def 'test add assay #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "addAssay", team, teamPassword)
        long assayId = panelAssayData.assayId
        long panelId = panelAssayData.panelId
        when:
        def response = client.post() {
            urlenc assayIds: assayId, id: panelId
        }

        then:
        //all of these redirect
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }

    def 'test add assay #forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "addAssay", team, teamPassword)
        long assayId = panelAssayData.assayId
        long panelId = panelAssayData.panelId
        when:
        response = client.post() {
            urlenc assayIds: assayId, id: panelId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_NOT_FOUND
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_NOT_FOUND
    }

    def 'test add assays #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "addAssays", team, teamPassword)
        long assayId = panelAssayData.assayId
        long panelId = panelAssayData.panelId
        when:
        def response = client.post() {
            urlenc assayIds: assayId, id: panelId
        }

        then:
        //all of these redirect
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }

    def 'test add assays forbidden #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "addAssays", team, teamPassword)
        long assayId = panelAssayData.assayId
        long panelId = panelAssayData.panelId
        when:
        def response = client.post() {
            urlenc assayIds: assayId, id: panelId
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test remove Assays  #desc'() {
        given:

        RESTClient client = getRestClient(controllerUrl, "removeAssays", team, teamPassword)
        long id = panelAssayData.panelId
        long assayId = panelAssayData.assayId
        when:
        def response = client.post() {
            urlenc assayIds: assayId, id: id
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }


    def 'test remove Assays #forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "removeAssays", team, teamPassword)
        long id = panelAssayData.panelId
        long assayId = panelAssayData.assayId
        when:
        def response = client.post() {
            urlenc assayIds: assayId, id: id
        }

        then:
        response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }

    def 'test remove Assay  #desc'() {
        given:

        RESTClient client = getRestClient(controllerUrl, "removeAssay", team, teamPassword)
        long id = panelAssayData.panelId
        long assayId = panelAssayData.assayId
        when:
        def response = client.post() {
            urlenc assayIds: assayId, id: id
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }


    def 'test remove Assay #forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "removeAssay", team, teamPassword)
        long id = panelAssayData.panelId
        long assayId = panelAssayData.assayId
        when:
        client.post() {
            urlenc assayIds: assayId, id: id
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_NOT_FOUND
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_NOT_FOUND
    }



    def 'test edit Panel Name #desc'() {
        given:
        long id = panelAssayData.panelId
        Map currentDataMap = getCurrentPanelProperties()
        RESTClient client = getRestClient(controllerUrl, "editPanelName", team, teamPassword)


        Long version = currentDataMap.version
        String oldPanelName = currentDataMap.panelName
        String newPanelName = "${oldPanelName}_1"
        panelNames.add(newPanelName)
        when:

        def response = client.post() {
            urlenc version: version, pk: id, value: newPanelName
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test edit Panel Name #desc - Forbidden'() {
        given:
        long id = panelAssayData.panelId
        Map currentDataMap = getCurrentPanelProperties()
        RESTClient client = getRestClient(controllerUrl, "editPanelName", team, teamPassword)


        Long version = currentDataMap.version
        String oldPanelName = currentDataMap.panelName
        String newPanelName = "${oldPanelName}_1"
        when:

        client.post() {
            urlenc version: version, pk: id, value: newPanelName
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test edit Panel Description #desc'() {
        given:
        long id = panelAssayData.panelId
        Map currentDataMap = getCurrentPanelProperties()
        RESTClient client = getRestClient(controllerUrl, "editDescription", team, teamPassword)


        Long version = currentDataMap.version
        String oldPanelDescription = currentDataMap.panelName
        String newPanelDescription = "${oldPanelDescription}_1"
        when:

        def response = client.post() {
            urlenc version: version, pk: id, value: newPanelDescription
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test edit Panel Description #desc - Forbidden'() {
        given:
        long id = panelAssayData.panelId
        Map currentDataMap = getCurrentPanelProperties()
        RESTClient client = getRestClient(controllerUrl, "editDescription", team, teamPassword)


        Long version = currentDataMap.version
        String oldPanelDescription = currentDataMap.panelName
        String newPanelDescription = "${oldPanelDescription}_1"
        when:

        client.post() {
            urlenc version: version, pk: id, value: newPanelDescription
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test delete Panel - unauthorized #desc'() {
        given:
        long id = panelAssayData.panelId
        RESTClient client = getRestClient(controllerUrl, "deletePanel", team, teamPassword)


        when:
        client.post() {
            urlenc id: id
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }


    def 'test delete Panel #desc'() {
        given:
        String reAuthenticateWith = TEAM_A_1_USERNAME
        List<Long> deleteAssayIdList = assayIdList
        long panelId = (Long) remote.exec({
            SpringSecurityUtils.reauthenticate(reAuthenticateWith, null)

            Role role = Role.findByAuthority(authority)
            Panel panel = Panel.build(name: "name", ownerRole: role)
            panel.save(flush: true)

            Assay assay = Assay.build(assayName: "name", ownerRole: role)
            assay.save(flush: true)
            deleteAssayIdList.add(assay.id)

            PanelAssay panelAssay = new PanelAssay(panel: panel, assay: assay)
            panelAssay.save(flush: true)

            return panel.id
        })
        RESTClient client = getRestClient(controllerUrl, "deletePanel", team, teamPassword)
        when:
        def response = client.post() {
            urlenc id: panelId
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc                  | team              | teamPassword      | authority     | expectedHttpResponse
        "User A_1 Can delete" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | 'ROLE_TEAM_A' | HttpServletResponse.SC_FOUND
        "User A_2 Can delete" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | 'ROLE_TEAM_A' | HttpServletResponse.SC_FOUND
    }

    private Map getCurrentPanelProperties() {
        long id = panelAssayData.panelId
        Map currentDataMap = (Map) remote.exec({
            Panel panel = Panel.findById(id)
            return [panelName: panel.name, version: panel.version]
        })
        return currentDataMap
    }

}
