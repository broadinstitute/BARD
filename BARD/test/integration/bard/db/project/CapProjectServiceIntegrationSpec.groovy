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

package bard.db.project

import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.ExpectedValueType
import bard.db.enums.Status
import bard.db.enums.ValueType
import bard.db.people.Role
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.IgnoreRest
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/9/13
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class CapProjectServiceIntegrationSpec extends IntegrationSpec {
    ProjectService projectService
    SessionFactory sessionFactory
    Role role

    @Before
    void setup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'test')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        role = Role.findByAuthority("ROLE_BARD_ADMINISTRATOR")
        if (!role) {
            role = Role.build(authority: "ROLE_BARD_ADMINISTRATOR", displayName: 'ROLE_1')
        }
    }

    void "test find Approved warehouse Probe Projects #desc"() {
        given:
        Element probeElement = Element.findByBardURI(bardURI)
        if (!probeElement) {
            probeElement = Element.build(expectedValueType: ExpectedValueType.EXTERNAL_ONTOLOGY, bardURI: bardURI, externalURL: "externalURL")
        }
        Project project = Project.build(projectStatus: projectStatus, ncgcWarehouseId: ncgcWarehouseId, ownerRole: role)
        ProjectContext projectContext = ProjectContext.build(contextName: "Context Name", contextType: ContextType.BIOLOGY, project: project)
        ProjectContextItem.build(context: projectContext,
                attributeElement: probeElement, valueDisplay: 'someDisplay', valueType: ValueType.FREE_TEXT, extValueId: "Stuff")


        when:
        final List<Long> projects = projectService.findApprovedProbeProjects()

        then:
        assert projects.size() == expectedNumberOfProbes

        where:
        desc                                  | projectStatus      | ncgcWarehouseId | bardURI                       | expectedNumberOfProbes
        "Valid Probe Element"                 | Status.APPROVED    | 1               | ProjectService.BARD_PROBE_URI | 1
        "Valid Probe Element-Prov"            | Status.PROVISIONAL | 1               | ProjectService.BARD_PROBE_URI | 1
        "Status is not Approved"              | Status.DRAFT       | 1               | ProjectService.BARD_PROBE_URI | 0
        "No NCGC Ware House Id"               | Status.APPROVED    | null            | ProjectService.BARD_PROBE_URI | 0
        "Invalid PROBE URI"                   | Status.APPROVED    | 1               | "Some uri"                    | 0
        "No NCGC Ware House Id - Prov status" | Status.PROVISIONAL | null            | ProjectService.BARD_PROBE_URI | 0
        "Invalid PROBE URI Prov status"       | Status.PROVISIONAL | 1               | "Some uri"                    | 0

    }
}
