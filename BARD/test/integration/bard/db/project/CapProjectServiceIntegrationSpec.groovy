package bard.db.project

import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.enums.ExpectedValueType
import bard.db.enums.ProjectStatus
import bard.db.enums.ValueType
import bard.db.people.Role
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.IgnoreRest

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/9/13
 * Time: 11:29 AM
 * To change this template use File | Settings | File Templates.
 */
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

    @IgnoreRest
    void "test find Approved warehouse Probe Projects"() {
        given:
        Element probeElement = Element.findByBardURI(bardURI)
        if (!probeElement) {
            probeElement = Element.build(expectedValueType: ExpectedValueType.EXTERNAL_ONTOLOGY, bardURI: bardURI)
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
        desc                     | projectStatus          | ncgcWarehouseId | bardURI                       | expectedNumberOfProbes
        "Valid Probe Element"    | ProjectStatus.APPROVED | 1               | ProjectService.BARD_PROBE_URI | 1
        "Status is not Approved" | ProjectStatus.DRAFT    | 1               | ProjectService.BARD_PROBE_URI | 0
        "No NCGC Ware House Id"  | ProjectStatus.APPROVED | null            | ProjectService.BARD_PROBE_URI | 0
        "Invalid PROBE URI"      | ProjectStatus.APPROVED | 1               | "Some uri"                    | 0
    }
}
