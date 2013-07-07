package bard.db.dictionary

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
 */
@Unroll
class ElementControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = baseUrl +  "element/"

    @Shared
    Map elementData
    @Shared
    List<Long> elementIdList = []

    def setupSpec() {
        String reauthenticateWithUser = CURATOR_USERNAME
        List<Long> currentIdList = elementIdList
        elementData = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            final String element1Label = "YYY"
            final String element2Label = "ZZZ"
            final String element3Label = "WWW"
            final String element4Label = "GGG"

            Element element1 = Element.findByLabel(element1Label)
            if (!element1) {
                element1 = Element.build(label: element1Label).save(flush: true)
            }

            Element element2 = Element.findByLabel(element2Label)
            if (!element2) {
                element2 = Element.build(label: element2Label).save(flush: true)
            }

            Element element3 = Element.findByLabel(element3Label)
            if (!element3) {
                element3 = Element.build(label: element3Label).save(flush: true)
            }

            Element element4 = Element.findByLabel(element4Label)
            if (!element4) {
                element4 = Element.build(label: element4Label).save(flush: true)
            }

            currentIdList.add(element1.id)
            currentIdList.add(element2.id)
            currentIdList.add(element3.id)
            currentIdList.add(element4.id)

            ElementHierarchy eh0a = ElementHierarchy.findByChildElementAndParentElement(element2, element1)
            if (eh0a == null) {
                eh0a = new ElementHierarchy(parentElement: element1, childElement: element2,
                        relationshipType: "subClassOf", dateCreated: new Date())
                eh0a.save(flush: true)

                element1.parentHierarchies.add(eh0a)
                element2.childHierarchies.add(eh0a)
            }


            ElementHierarchy eh1a = ElementHierarchy.findByChildElementAndParentElement(element3, element2)
            if (eh1a == null) {
                eh1a = new ElementHierarchy(parentElement: element2, childElement: element3,
                        relationshipType: "subClassOf", dateCreated: new Date())
                eh1a.save(flush: true)

                element2.parentHierarchies.add(eh1a)
                element3.childHierarchies.add(eh1a)
            }

            ElementHierarchy eh2a = ElementHierarchy.findByChildElementAndParentElement(element4, eh1a.childElement)
            if (eh2a == null) {
                eh2a = new ElementHierarchy(parentElement: eh1a.childElement, childElement: element4,
                        relationshipType: "subClassOf", dateCreated: new Date())
                eh2a.save(flush: true)

                eh1a.childElement.parentHierarchies.add(eh2a)
                element4.childHierarchies.add(eh2a)
            }
            return [element1Id: element1.id, element2Id: element2.id, element3Id:element3.id, element4Id: element4.id]
        })

        elementIdList.add(elementData.element1Id)
        elementIdList.add(elementData.element2Id)
        elementIdList.add(elementData.element3Id)
        elementIdList.add(elementData.element4Id)
    }


    def 'test edit #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "edit", team, teamPassword)

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

    }

    def 'test edit #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "edit", team, teamPassword)

        when:
        final Response response = client.get()
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc      | team             | teamPassword     | expectedHttpResponse
        "CURATOR" | CURATOR_USERNAME | CURATOR_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"   | ADMIN_USERNAME   | ADMIN_PASSWORD   | HttpServletResponse.SC_OK
    }

    def 'test update #desc - forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)

        when:
        client.post() {
            urlenc elementHierarchyIdList: "3", elementId: "2", newPathString: "newPathString"
        }
        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User B"   | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FORBIDDEN

    }


    def 'test update #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "update", team, teamPassword)
        String newPathString = "a/b/c"
        when:
        final Response response = client.post() {
            urlenc elementHierarchyIdList: "0", elementId: "0", newPathString: newPathString
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc      | team             | teamPassword     | expectedHttpResponse
        "CURATOR" | CURATOR_USERNAME | CURATOR_PASSWORD | HttpServletResponse.SC_OK
        "ADMIN"   | ADMIN_USERNAME   | ADMIN_PASSWORD   | HttpServletResponse.SC_OK
    }

    def 'test list #desc'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "list", team, teamPassword)

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
    // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [CURATOR_USERNAME])
        for (Long elementId : elementIdList) {
            sql.execute("DELETE FROM ELEMENT_HIERARCHY WHERE PARENT_ELEMENT_ID=${elementId} OR CHILD_ELEMENT_ID=${elementId}")
            sql.execute("DELETE FROM ELEMENT WHERE ELEMENT_ID=${elementId}")
        }
    }

}
