package bard.db.registration

import bard.db.audit.BardContextUtils
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import grails.plugin.spock.IntegrationSpec
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import org.junit.Before
import org.springframework.security.core.context.SecurityContextHolder
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

    void "test get assays for user groups no results"() {
        when:
        List<Assay> assays = assayDefinitionService.getAssaysByGroup()

        then:
        assert assays.size() == 0

    }

    void "test get assays for user groups with results"() {
        final List<Assay> builtAssays = []

        given:
        SecurityContextHolder.clearContext();

        if (username) {
            springSecurityService.reauthenticate(username)
            numberOfAssays.times { builtAssays.add(Assay.build()) }
        }

        when:
        List<Assay> foundAssays = assayDefinitionService.getAssaysByGroup()

        then:
        assert foundAssays.size() == numberOfAssays
        assert foundAssays.containsAll(builtAssays)

        where:
        desc                               | numberOfAssays | username
        'no user no assays'                | 0              | null
        'authenticated user with 0 assays' | 0              | 'integrationTestUser'
        'authenticated user with 1 assay'  | 1              | 'integrationTestUser'
        'authenticated user with 2 assays' | 2              | 'integrationTestUser'
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
        final Assay assay = Assay.build(assayName: 'assayName20', assayStatus: AssayStatus.DRAFT)
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

//    void "test save new Assay"() {
//        given:
//        final Assay assay = Assay.build(assayName: 'assayName40', assayType: AssayType.PANEL_GROUP)
//        when:
//        final Assay updatedAssay = assayDefinitionService.saveNewAssay(assay)
//        then:
//        assert AssayType.PANEL_GROUP == updatedAssay.assayType
//    }
}