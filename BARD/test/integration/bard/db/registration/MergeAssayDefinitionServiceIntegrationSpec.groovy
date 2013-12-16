package bard.db.registration

import bard.db.audit.BardContextUtils
import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.people.Person
import bard.db.people.PersonRole
import bard.db.people.Role
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
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

    void "test filterOut Experiments Not Owned By Me"() {
        given:
        Role role1 = Role.build(authority: "ROLE_TEAM_MU", displayName: "Manchester United").save(flush: true)
        Role role2 = Role.build(authority: "ROLE_TEAM_MC", displayName: "Manchester City").save(flush: true)
        Person person1 = Person.build(userName: "ryanGiggs").save(flush: true)
        PersonRole.build(person: person1, role: role1).save(flush: true)
        final List<Long> experimentIds = []


        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) {
                Experiment experiment = Experiment.build(ownerRole: role1).save(flush: true)
                experimentIds.add(experiment.id)
            } else {
                Experiment experiment = Experiment.build(ownerRole: role2).save(flush: true)
                experimentIds.add(experiment.id)
            }
        }
        SpringSecurityUtils.reauthenticate(person1.userName, null)
        when:

        List<Long> ids = mergeAssayDefinitionService.filterOutExperimentsNotOwnedByMe(experimentIds)
        then:
        assert ids.size() == 5


    }

    void "test find Assay By ADID no exceptions"() {
        given:
        final Assay assay = Assay.build(assayName: 'assay1', capPermissionService: null)
        when:
        final Assay updatedAssay = mergeAssayDefinitionService.findEntityByIdType(assay.id, IdType.ADID)
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
        final Experiment updatedExperiment = mergeAssayDefinitionService.findEntityByIdType(id, IdType.AID)
        then:
        assert updatedExperiment
        assert experiment.id == updatedExperiment.id
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
        assert assayTwo.assayStatus == Status.RETIRED
        and: "and the merged Assay should have no experiments associated to it"
        assert assayTwo.experiments.size() == 0
    }
}