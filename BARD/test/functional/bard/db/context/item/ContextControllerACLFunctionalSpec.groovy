/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.context.item

import bard.db.people.Role
import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import bard.db.registration.BardControllerFunctionalSpec
import groovy.sql.Sql
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Shared
import spock.lang.Unroll
import wslite.rest.RESTClient
import wslite.rest.RESTClientException

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
class ContextControllerACLFunctionalSpec extends BardControllerFunctionalSpec {
    static final String controllerUrl = getBaseUrl() + "context/"

    @Shared
    Map contextData
    @Shared
    List projectIdList = []
    @Shared
    List assayIdList = []


    def setupSpec() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME




        contextData = (Map) remote.exec({
            //Build assay as TEAM_A
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)

            Role role = Role.findByAuthority('ROLE_TEAM_A')
            if (!role) {
                role = Role.build(authority: 'ROLE_TEAM_A', displayName: 'ROLE_TEAM_A').save(flush: true)
            }

            Assay assay = Assay.build(assayName: "Assay Name20", ownerRole:role).save(flush: true)
            Project project = Project.build(name: "Some Project Name",ownerRole:role).save(flush: true)
            //create assay context
            return [assayId: assay.id, projectId: project.id]
        })
        assayIdList.add(contextData.assayId)
        projectIdList.add(contextData.projectId)

    }     // run before the first feature method

    def 'test delete Empty Assay Context Card #desc'() {
        given:
        Map currentData = (Map) createAssayContext()
        long contextId = currentData.assayContextId

        String contextClass = 'AssayContext'
        String section = "biology"
        RESTClient client = getRestClient(controllerUrl, "deleteEmptyCard", team, teamPassword)

        when:
        def response = client.post() {
            urlenc contextClass: contextClass, section: section, contextId: contextId
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }

    def 'test delete Empty Project Context Card #desc'() {
        given:
        Map currentData = (Map) createProjectContext()
        long contextId = currentData.projectContextId

        String contextClass = 'ProjectContext'
        String section = "biology"
        RESTClient client = getRestClient(controllerUrl, "deleteEmptyCard", team, teamPassword)

        when:
        def response = client.post() {
            urlenc contextClass: contextClass, section: section, contextId: contextId
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }

    def 'test delete Empty Assay Context Card #forbidden'() {
        given:
        Map currentData = (Map) createAssayContext()
        long contextId = currentData.assayContextId

        String contextClass = 'AssayContext'
        String section = "biology"
        RESTClient client = getRestClient(controllerUrl, "deleteEmptyCard", team, teamPassword)

        when:
        response = client.post() {
            urlenc contextClass: contextClass, section: section, contextId: contextId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test delete Empty Project Context Card #forbidden'() {
        given:
        Map currentData = (Map) createProjectContext()
        long contextId = currentData.projectContextId

        String contextClass = 'ProjectContext'
        String section = "biology"
        RESTClient client = getRestClient(controllerUrl, "deleteEmptyCard", team, teamPassword)

        when:
        response = client.post() {
            urlenc contextClass: contextClass, section: section, contextId: contextId
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def 'test create Project Context Card #desc'() {
        given:
        long projectId = contextData.projectId
        String contextClass = "ProjectContext"
        String cardName = "My Card Name"
        String cardSection = "Biology"
        RESTClient client = getRestClient(controllerUrl, "createCard", team, teamPassword)

        when:
        def response = client.post() {
            urlenc contextClass: contextClass, ownerId: projectId, cardName: cardName, cardSection: cardSection
        }
        then:
        assert response.statusCode == expectedHttpResponse
        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }

    def 'test create Project Context Card #forbidden'() {
        long projectId = contextData.projectId
        String contextClass = "ProjectContext"
        String cardName = "My Card Name"
        String cardSection = "Biology"
        RESTClient client = getRestClient(controllerUrl, "createCard", team, teamPassword)

        when:
        client.post() {
            urlenc contextClass: contextClass, ownerId: projectId, cardName: cardName, cardSection: cardSection
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    def "test create Assay Context Card #desc"() {
        given:
        RESTClient client = getRestClient(controllerUrl, "createCard", team, teamPassword)
        String contextClass = "AssayContext"
        Long ownerId = contextData.assayId
        String cardName = "My Card Name"
        String cardSection = "Biology"
        when:
        def response = client.post() {
            urlenc contextClass: contextClass, ownerId: ownerId, cardName: cardName, cardSection: cardSection
        }
        then:
        assert response.statusCode == expectedHttpResponse

        where:
        desc       | team              | teamPassword      | expectedHttpResponse
        "User A_1" | TEAM_A_1_USERNAME | TEAM_A_1_PASSWORD | HttpServletResponse.SC_FOUND
        "User A_2" | TEAM_A_2_USERNAME | TEAM_A_2_PASSWORD | HttpServletResponse.SC_FOUND
        "ADMIN"    | ADMIN_USERNAME    | ADMIN_PASSWORD    | HttpServletResponse.SC_FOUND
    }

    def 'test create Assay Context Card #forbidden'() {
        given:
        RESTClient client = getRestClient(controllerUrl, "createCard", team, teamPassword)
        String contextClass = "AssayContext"
        Long ownerId = contextData.assayId
        String cardName = "My Card Name"
        String cardSection = "Biology"
        when:
        client.post() {
            urlenc contextClass: contextClass, ownerId: ownerId, cardName: cardName, cardSection: cardSection
        }

        then:
        def ex = thrown(RESTClientException)
        assert ex.response.statusCode == expectedHttpResponse

        where:
        desc                  | team              | teamPassword      | expectedHttpResponse
        "User B cannot Edit"  | TEAM_B_1_USERNAME | TEAM_B_1_PASSWORD | HttpServletResponse.SC_FORBIDDEN
        "CURATOR cannot Edit" | CURATOR_USERNAME  | CURATOR_PASSWORD  | HttpServletResponse.SC_FORBIDDEN
    }

    Map createProjectContext() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Role role = Role.findByAuthority('ROLE_TEAM_A')
            if (!role) {
                role = Role.build(authority: 'ROLE_TEAM_A', displayName: 'ROLE_TEAM_A').save(flush: true)
            }
            Project project = Project.build(name: "Some Project Name2", ownerRole: role).save(flush: true)
            ProjectContext context = ProjectContext.build(project: project, contextName: "alpha").save(flush: true)


            return [projectId: project.id, projectContextId: context.id]
        })
        projectIdList.add(m.projectId)
        return m
    }

    Map createAssayContext() {
        String reauthenticateWithUser = TEAM_A_1_USERNAME

        Map m = (Map) remote.exec({
            SpringSecurityUtils.reauthenticate(reauthenticateWithUser, null)
            Role role = Role.findByAuthority('ROLE_TEAM_A')
            if (!role) {
                role = Role.build(authority: 'ROLE_TEAM_A', displayName: 'ROLE_TEAM_A').save(flush: true)
            }
            Assay assay = Assay.build(assayName: "Assay Name202", ownerRole: role).save(flush: true)
            AssayContext context = AssayContext.build(assay: assay, contextName: "alpha").save(flush: true)

            //create assay context
            return [id: assay.id, assayContextId: context.id]
        })

        assayIdList.add(m.id)
        return m
    }

    // run before the first feature method
    def cleanupSpec() {

        Sql sql = Sql.newInstance(dburl, dbusername,
                dbpassword, driverClassName)
        sql.call("{call bard_context.set_username(?)}", [TEAM_A_1_USERNAME])

        for (Long assayId : assayIdList) {
            sql.execute("DELETE FROM ASSAY_CONTEXT WHERE ASSAY_ID=${assayId}")
            sql.execute("DELETE FROM ASSAY WHERE ASSAY_ID=${assayId}")
        }
        for (Long projectId : projectIdList) {
            sql.execute("DELETE FROM PROJECT_CONTEXT WHERE PROJECT_ID=${projectId}")
            sql.execute("DELETE FROM PROJECT WHERE PROJECT_ID=${projectId}")

        }

    }

}
