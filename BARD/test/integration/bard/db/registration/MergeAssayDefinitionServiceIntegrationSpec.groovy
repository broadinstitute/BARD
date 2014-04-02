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
        final List<Experiment> updatedExperiment = mergeAssayDefinitionService.findEntityByIdType(id, IdType.AID)
        then:
        assert updatedExperiment
        assert experiment.id == updatedExperiment.first().id
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

    void "test move Experiments from Assay"() {
        given: "Three assays with experiments and a target assay"
        Assay sourceAssay1 = Assay.build(assayStatus: Status.APPROVED)
        Assay sourceAssay4 = Assay.build(assayStatus: Status.PROVISIONAL)
        Experiment experimentA = Experiment.build(assay: sourceAssay1)
        Experiment experimentE = Experiment.build(assay: sourceAssay4)
        Assay sourceAssay2 = Assay.build(assayStatus: Status.APPROVED)
        Experiment experimentB = Experiment.build(assay: sourceAssay2)
        Assay sourceAssay3 = Assay.build(assayStatus: Status.APPROVED)
        Experiment experimentC = Experiment.build(assay: sourceAssay3)
        Experiment experimentD = Experiment.build(assay: sourceAssay3)
        List<Experiment> experiments = [experimentA, experimentB, experimentC, experimentE]
        Assay targetAssay = Assay.build(assayStatus: Status.APPROVED)

        when: "We move 3 experiments from 3 assays to the target assay"
        Assay newTargetAssay = mergeAssayDefinitionService.moveExperimentsFromAssay(targetAssay, experiments)

        then: "We expect source assay 1 to have status RETIRED and 0 experiments after experiment A was moved to target assay"
        assert sourceAssay1.assayStatus == Status.RETIRED
        assert sourceAssay1.experiments.size() == 0
        and: "We expect source assay 4 to have status RETIRED and 0 experiments after experiment A was moved to target assay"
        assert sourceAssay4.assayStatus == Status.RETIRED
        assert sourceAssay4.experiments.size() == 0
        and: "We expect source assay 2 to have status RETIRED and 0 experiments after experiment B was moved to target assay"
        assert sourceAssay2.assayStatus == Status.RETIRED
        assert sourceAssay2.experiments.size() == 0
        and: "We expect source assay 3 to still have its original status of APPROVED since it has 1 experiment after experiment C was moved to target assay"
        assert sourceAssay3.assayStatus == Status.APPROVED
        assert sourceAssay3.experiments.size() == 1
        and: "We expect the target assay to have 3 new experiments"
        newTargetAssay.experiments.size() == 4
    }
}