package bard.db.registration

import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.ContextType
import bard.db.enums.HierarchyType
import bard.db.experiment.ExperimentMeasure
import bard.db.people.Role
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Shared
import spock.lang.Unroll
import wslite.json.JSONArray
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
class AssayDefintionControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = getBaseUrl() + "assayDefinition/"

    @Shared
    Map assayData
    @Shared
    List<Long> assayIdList = []  //we keep ids of all assays here so we can delete after all the tests have finished


    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME



        assayData = (Map) remote.exec({
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
            String childLabel = "child"
            String parentLabel = "parent"
            Element childElement = Element.findByLabel(childLabel)
            if (!childElement) {
                childElement = Element.build(label: childLabel).save(flush: true)
            }
            ExperimentMeasure childMeasure = ExperimentMeasure.findByResultType(childElement)
            if (!childMeasure) {
                childMeasure = ExperimentMeasure.build(resultType: childElement).save(flush: true)
            }

            Element parentElement = Element.findByLabel(parentLabel)
            if (!parentElement) {
                parentElement = Element.build(label: parentLabel).save(flush: true)
            }
            ExperimentMeasure parentMeasure = ExperimentMeasure.findByResultType(parentElement)
            if (!parentMeasure) {
                parentMeasure = ExperimentMeasure.build(resultType: parentElement).save(flush: true)

                childMeasure.parentChildRelationship = HierarchyType.SUPPORTED_BY
                parentMeasure.addToChildMeasures(childMeasure)
                childMeasure.save(flush: true)
            }

            Assay assay = Assay.build(assayName: "Assay Name10", ownerRole: role).save(flush: true)
            AssayContext context = AssayContext.build(assay: assay, contextName: "alpha").save(flush: true)

            //create assay context
            return [id: assay.id, assayName: assay.assayName, assayContextId: context.id,
                    measureId: childMeasure.id, parentMeasureId: parentMeasure.id, roleId: role.id, otherRoleId: otherRole.id]
        })
        assayIdList.add(assayData.id)


    }     // run before the first feature method
    def cleanupSpec() {
        if (assayData?.id) {
            Sql sql = Sql.newInstance(dburl, dbusername,
                    dbpassword, driverClassName)
            sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])

            sql.execute("DELETE FROM ASSAY_CONTEXT WHERE ASSAY_ID=${assayData.id}")
            //sql.execute("DELETE FROM MEASURE WHERE ASSAY_ID=${assayData.id}")
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${assayData.id}")
        }
    }

    def 'test edit owner admin #desc'() {
        Long pk = assayData.id
        String newRole = "ROLE_TEAM_B"
        Long version = getCurrentAssayProperties().version
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
        Long pk = assayData.id
        String newRole = "ROLE_TEAM_B"
        Long version = getCurrentAssayProperties().version
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

    def 'test save #desc'() {
        given:

        String name = "My Assay Name_" + team
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:


        Response response = client.post() {
            urlenc assayName: name, ownerRole: assayData.roleId
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
        String name = "My Assay Name_" + team
        RESTClient client = getRestClient(controllerUrl, "save", team, teamPassword)
        when:
        client.post() {
            urlenc assayName: name, ownerRole: assayData.roleId
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc      | team              | teamPassword      | expectedHttpResponse
        "User B"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_NOT_FOUND
        "CURATOR" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_NOT_FOUND
    }

    def 'test index #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "index", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc                 | team              | teamPassword      | expectedHttpResponse
        "Not Logged in User" | null              | null              | HttpServletResponse.SC_FOUND
        "User A_1"           | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User B"             | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2"           | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"              | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
        "CURATOR"            | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FOUND
    }

    def 'test reloadCardHolder #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "", team, teamPassword)

        when:
        final Response response = client.get(path: '/reloadCardHolder', query: [assayId: assayData.id, include_entities: true])
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

    def 'test reloadCardHolder unauthorized #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "", team, teamPassword)

        when:
        client.get(path: '/reloadCardHolder', query: [assayId: assayData.id, include_entities: true])
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse


        where:
        desc                 | team | teamPassword | expectedHttpResponse
        "Not Logged in User" | null | null         | HttpServletResponse.SC_UNAUTHORIZED
    }

    def 'test show not logged in #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show/${assayData.id}", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                 | team | teamPassword | expectedHttpResponse
        "Not Logged in User" | null | null         | HttpServletResponse.SC_OK

    }

    def 'test show #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "show/${assayData.id}", team, teamPassword)

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


    /**
     * Forwards user to the edit Context page
     * @return
     */
    def 'test editContext #desc'() {
        given:
        long assayId = assayData.id
        String groupBySection = ContextType.BIOLOGY.id
        RESTClient client = getRestClient(controllerUrl, "editContext", team, teamPassword)

        when:
        def response = client.post() {
            urlenc id: assayId, groupBySection: groupBySection
        }

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.text.contains("Edit Assay (${assayId}) Contexts")

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK

    }
    /**
     * Forwards user to the edit Context page
     * @return
     */
    def 'test editContext  not logged in #desc'() {
        given:
        long assayId = assayData.id
        String groupBySection = ContextType.BIOLOGY.id
        RESTClient client = getRestClient(controllerUrl, "editContext", team, teamPassword)

        when:
        def response = client.post() {
            urlenc id: assayId, groupBySection: groupBySection
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                 | team | teamPassword | expectedHttpResponse
        "Not Logged in User" | null | null         | HttpServletResponse.SC_UNAUTHORIZED
    }


    def 'test cloneAssay #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "cloneAssay/${assayData.id}", team, teamPassword)
        when:
        final Response response = client.get()

        then:
        // they all redirect -- see https://www.pivotaltracker.com/story/show/54059075
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
        "CURATOR"  | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FOUND

    }

    def 'test cloneAssay not logged in  #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "cloneAssay/${assayData.id}", team, teamPassword)
        when:
        client.get()

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc                 | team | teamPassword | expectedHttpResponse
        "Not Logged in User" | null | null         | HttpServletResponse.SC_UNAUTHORIZED
    }


    def 'test assayTypes #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "assayTypes", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.json instanceof JSONArray


        where:
        desc                 | team              | teamPassword      | expectedHttpResponse
        "Not Logged in User" | null              | null              | HttpServletResponse.SC_OK
        "User A_1"           | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"             | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2"           | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"              | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"            | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK

    }

    def 'test assayStatus #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "assayStatus", team, teamPassword)

        when:
        final Response response = client.get()

        then:
        assert response.statusCode == expectedHttpResponse
        assert response.json


        where:
        desc                 | team              | teamPassword      | expectedHttpResponse
        "Not Logged in User" | null              | null              | HttpServletResponse.SC_OK
        "User A_1"           | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User B"             | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2"           | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"              | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
        "CURATOR"            | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_OK
    }





    def 'test editDesignedBy #desc'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()
        RESTClient client = getRestClient(controllerUrl, "editDesignedBy", team, teamPassword)

        Long version = currentDataMap.version
        String oldDesignedBy = currentDataMap.designedBy
        String newDesignedBy = oldDesignedBy ? "${oldDesignedBy}_1" : "team1"
        when:

        def response = client.post() {
            urlenc version: version, pk: id, value: newDesignedBy
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK

    }



    def 'test editDesignedBy #desc forbidden'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()
        RESTClient client = getRestClient(controllerUrl, "editDesignedBy", team, teamPassword)

        Long version = currentDataMap.version
        String oldDesignedBy = currentDataMap.designedBy
        String newDesignedBy = oldDesignedBy ? "${oldDesignedBy}_1" : "team1"
        when:

        client.post() {
            urlenc version: version, pk: id, value: newDesignedBy
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN

    }

    def 'test editAssayName #desc'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()
        RESTClient client = getRestClient(controllerUrl, "editAssayName", team, teamPassword)


        Long version = currentDataMap.version
        String oldAssayName = currentDataMap.assayName
        String newAssayName = "${oldAssayName}_1"
        when:

        def response = client.post() {
            urlenc version: version, pk: id, value: newAssayName
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test editAssayName #desc - Forbidden'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()
        RESTClient client = getRestClient(controllerUrl, "editAssayName", team, teamPassword)


        Long version = currentDataMap.version
        String oldAssayName = currentDataMap.assayName
        String newAssayName = "${oldAssayName}_1"
        when:

        response = client.post() {
            urlenc version: version, pk: id, value: newAssayName
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test editAssayStatus #desc'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()
        RESTClient client = getRestClient(controllerUrl, "editAssayStatus", team, teamPassword)

        Long version = currentDataMap.version
        String oldAssayStatus = currentDataMap.assayStatus
        String newAssayStatus = null
        for (AssayStatus assayStatus : AssayStatus.values()) {
            if (oldAssayStatus != assayStatus.id) {
                newAssayStatus = assayStatus.id
                break;
            }
        }
        when:

        def response = client.post() {
            urlenc version: version, pk: id, value: newAssayStatus
        }

        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    def 'test editAssayStatus - unauthorized #desc'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()
        RESTClient client = getRestClient(controllerUrl, "editAssayStatus", team, teamPassword)
        Long version = currentDataMap.version
        String oldAssayStatus = currentDataMap.assayStatus
        String newAssayStatus = null
        for (AssayStatus assayStatus : AssayStatus.values()) {
            if (oldAssayStatus != assayStatus.id) {
                newAssayStatus = assayStatus.id
                break;
            }
        }
        when:
        client.post() {
            urlenc version: version, pk: id, value: newAssayStatus
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test editAssayType - unauthorized #desc'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()

        RESTClient client = getRestClient(controllerUrl, "editAssayType", team, teamPassword)

        Long version = currentDataMap.version
        String oldAssayType = currentDataMap.assayType
        String newAssayType = null
        for (AssayType assayType : AssayType.values()) {
            if (oldAssayType != assayType.id) {
                newAssayType = assayType.id
                break;
            }
        }
        when:
        client.post() {
            urlenc version: version, pk: id, value: newAssayType
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test editAssayType #desc'() {
        given:
        long id = assayData.id
        Map currentDataMap = getCurrentAssayProperties()
        RESTClient client = getRestClient(controllerUrl, "editAssayType", team, teamPassword)

        Long version = currentDataMap.version
        String oldAssayType = currentDataMap.assayType
        String newAssayType = null
        for (AssayType assayType : AssayType.values()) {
            if (oldAssayType != assayType.id) {
                newAssayType = assayType.id
                break;
            }
        }
        when:

        def response = client.post() {
            urlenc version: version, pk: id, value: newAssayType
        }

        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }


    def 'test updateCardName - unauthorized #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "updateCardName", team, teamPassword)
        long assayContextId = assayData.assayContextId

        when:
        client.post() {
            urlenc edit_card_name: "My New Card Name", contextId: assayContextId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test updateCardName #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "updateCardName", team, teamPassword)
        long assayContextId = assayData.assayContextId

        when:
        def response = client.post() {
            urlenc edit_card_name: "My New Card Name", contextId: assayContextId
        }

        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc                | team              | teamPassword      | expectedHttpResponse
        "User A_1 Can Edit" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_OK
        "User A_2 Can Edit" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN Can Edit"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_OK
    }

    private Map getCurrentAssayProperties() {
        long id = assayData.id
        Map currentDataMap = (Map) remote.exec({
            Assay assay = Assay.findById(id)
            return [assayName: assay.assayName, version: assay.version, assayType: assay.assayType.id, assayStatus: assay.assayStatus.id, designedBy: assay.designedBy]
        })
        return currentDataMap
    }

}
