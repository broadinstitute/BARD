package bard.db.registration

import bard.db.audit.BardContextUtils
import bard.db.enums.AssayType
import bard.db.enums.Status
import grails.plugin.spock.IntegrationSpec
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class AssayDefinitionServiceIntegrationSpec extends IntegrationSpec {

    AssayDefinitionService assayDefinitionService
    SessionFactory sessionFactory
    SpringSecurityService springSecurityService

    @Before
    void setup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'test')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    }
    void "test update designed By"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName20', designedBy: "BARD")
        final String newDesignedBy = "CAP"
        when:
        final Assay updatedAssay = assayDefinitionService.updateDesignedBy(assay.id, newDesignedBy)
        then:
        assert newDesignedBy == updatedAssay.designedBy
    }

    void "test update assay name"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName20', assayStatus: Status.DRAFT)
        final String newAssayName = "New Assay Name"
        when:
        final Assay updatedAssay = assayDefinitionService.updateAssayName(assay.id, newAssayName)
        then:
        assert newAssayName == updatedAssay.assayName
    }

    void "test update assay type"() {
        given:
        final Assay assay = Assay.build(assayName: 'assayName10', assayType: AssayType.PANEL_GROUP)
        when:
        final Assay updatedAssay = assayDefinitionService.updateAssayType(assay.id, AssayType.TEMPLATE)
        then:
        assert AssayType.TEMPLATE == updatedAssay.assayType
    }
}