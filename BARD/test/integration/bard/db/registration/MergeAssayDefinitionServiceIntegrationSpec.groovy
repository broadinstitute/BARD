package bard.db.registration

import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.grails.plugins.springsecurity.service.acl.AclUtilService
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.IgnoreRest
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/21/12
 * Time: 9:18 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class MergeAssayDefinitionServiceIntegrationSpec extends IntegrationSpec {

    MergeAssayDefinitionService mergeAssayDefinitionService
    SessionFactory sessionFactory

    @Before
    void setup() {
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'test')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
    }

    void "test find Assay By ADID no exceptions"() {
        given:
        final Assay assay = Assay.build(assayName: 'assay1', capPermissionService: null)
        when:
        final Assay updatedAssay = mergeAssayDefinitionService.findAssayByAssayIdType(assay.id, AssayIdType.ADID)
        then:
        assert assay.id == updatedAssay.id
    }

    void "test find Assay By AID"() {
        given:
        Long id = 1
        final Assay assay = Assay.build(assayName: 'assay1', capPermissionService: null)
        final String experimentsAlias = "experiment"
        final Experiment experiment = Experiment.build(experimentName: experimentsAlias, assay: assay, capPermissionService: null)
        ExternalReference.build(extAssayRef: "aid=${id}", experiment: experiment)
        when:
        final Assay updatedAssay = mergeAssayDefinitionService.findAssayByAssayIdType(id, AssayIdType.AID)
        then:
        assert updatedAssay
        assert assay.id == updatedAssay.id
    }


    void "test merge All Assays"() {
        given: "Two assays with two different experiments"
        final Assay assayOne = Assay.build()
        Experiment.build(experimentName: "experiments1", assay: assayOne, capPermissionService: null)

        final Assay assayTwo = Assay.build()
        Experiment.build(experimentName: "experiments2", assay: assayTwo, capPermissionService: null)


        when: "We merge the assays"
        Assay mergedAssay = mergeAssayDefinitionService.mergeAllAssays(assayOne, [assayTwo])

        then: "We expect that target assay should have one more experiment than it did before the merge"
        assert mergedAssay.experiments.size() == 2
        and: "that the merged Assay should be in a retired state"
        assert assayTwo.assayStatus == AssayStatus.RETIRED
        and: "and the merged Assay should have no experiments associated to it"
        assert assayTwo.experiments.size() == 0
    }
}