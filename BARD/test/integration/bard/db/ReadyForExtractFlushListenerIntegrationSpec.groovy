package bard.db

import bard.db.audit.BardContextUtils
import bard.db.dictionary.Element
import bard.db.dictionary.ElementStatus as ELS
import bard.db.enums.Status
import bard.db.enums.Status as ST
import bard.db.experiment.Experiment
import bard.db.experiment.ExperimentContext
import bard.db.experiment.ExperimentContextItem
import bard.db.project.Project
import bard.db.project.ProjectSingleExperiment
import bard.db.registration.Assay
import grails.plugin.spock.IntegrationSpec
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.hibernate.SessionFactory
import spock.lang.Unroll

import static bard.db.enums.ReadyForExtraction.*

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 6/11/13
 * Time: 2:33 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class ReadyForExtractFlushListenerIntegrationSpec extends IntegrationSpec {
    SessionFactory sessionFactory

    void "test ready flag gets set"() {
        // all of the various paths to find owners are tested in unit tests.  This is just to test that the spring config
        // and database update is working by doing a single example.
        setup:
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        Experiment experiment = Experiment.build(readyForExtraction: NOT_READY, experimentStatus: ST.APPROVED)
        ExperimentContext context = ExperimentContext.build(experiment: experiment)
        experiment.disableUpdateReadyForExtraction = true

        when:
        sessionFactory.currentSession.flush()

        then:
        experiment.readyForExtraction == NOT_READY

        when:
        experiment.disableUpdateReadyForExtraction = false
        ExperimentContextItem item = ExperimentContextItem.build(experimentContext: context)
        sessionFactory.currentSession.flush()

        then:
        experiment.readyForExtraction == READY
    }

    void "test ready flag gets set #desc #statusToTest.id"() {
        given: 'an owning entity with readyForExtraction: NOT_READY'
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        def ownerEntity = ownerClosure.call()

        when: 'when flush'
        sessionFactory.currentSession.flush()

        then: 'verify readyForExtraction due to status'
        ownerEntity.readyForExtraction == initialRFE
//        ownerEntity."${statusField}".getId() == 'Draft'

        when: 'modification but status Draft'
        ownerEntity."${fieldToModify}" += ' a change '
        sessionFactory.currentSession.flush()

        then: 'still NOT_READY'
        ownerEntity.readyForExtraction == NOT_READY

        when: 'status is not Draft'
        ownerEntity."${statusField}" = statusToTest
        ownerEntity."${fieldToModify}" += ' a change '
        sessionFactory.currentSession.flush()

        then: 'the expect'
        ownerEntity.readyForExtraction == READY

        when: 'disableUpdateReadyForExtraction == true'
        ownerEntity.disableUpdateReadyForExtraction = true
        ownerEntity.readyForExtraction = NOT_READY
        sessionFactory.currentSession.flush()

        then: 'readyForExtraction will remain NOT_READY'
        ownerEntity.readyForExtraction == NOT_READY

        where:
        desc                      | ownerClosure           | initialRFE | statusField        | statusToTest | fieldToModify
        'Assay with status '      | { Assay.build() }      | NOT_READY  | 'assayStatus'      | ST.APPROVED  | 'assayName'
        'Assay with status '      | { Assay.build() }      | NOT_READY  | 'assayStatus'      | ST.RETIRED   | 'assayName'
        'Experiment with status ' | { Experiment.build() } | NOT_READY  | 'experimentStatus' | ST.APPROVED | 'description'
        'Experiment with status ' | { Experiment.build() } | NOT_READY  | 'experimentStatus' | ST.RETIRED  | 'description'
        'Project with status '    | { Project.build() }    | NOT_READY  | 'projectStatus'    | ST.APPROVED  | 'description'
        'Project with status '    | { Project.build() }    | NOT_READY  | 'projectStatus'    | ST.RETIRED   | 'description'
    }

    void "test ready flag for project experiments gets set #desc #statusToTest.id"() {
        given: 'an owning entity with readyForExtraction: NOT_READY'
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        Project project = Project.build(projectStatus: Status.APPROVED, readyForExtraction: COMPLETE)
        Experiment experiment = Experiment.build()
        ProjectSingleExperiment.build(project: project, experiment: experiment)

        when: 'when flush'
        sessionFactory.currentSession.flush()

        then: 'verify readyForExtraction due to status'
        experiment.readyForExtraction == initialRFE

        when: 'modification but status Draft'
        experiment."${fieldToModify}" += ' a change '
        sessionFactory.currentSession.flush()

        then: 'still NOT_READY'
        experiment.readyForExtraction == NOT_READY


        when: 'status is not Draft'
        experiment."${statusField}" = statusToTest
        experiment."${fieldToModify}" += ' a change '
        sessionFactory.currentSession.flush()

        then: 'the expect'
        experiment.readyForExtraction == READY
        project.readyForExtraction == READY



        where:
        desc                      | initialRFE | statusField        | statusToTest | fieldToModify
        'Experiment with status ' | NOT_READY  | 'experimentStatus' | ST.APPROVED | 'description'
        'Experiment with status ' | NOT_READY  | 'experimentStatus' | ST.RETIRED  | 'description'
    }

    void "test Element ready flag #desc"() {
        given: 'an owning entity with readyForExtraction: NOT_READY'
        BardContextUtils.setBardContextUsername(sessionFactory.currentSession, 'integrationTestUser')
        SpringSecurityUtils.reauthenticate('integrationTestUser', null)
        Element ownerEntity = Element.build()

        when: 'when flush'
        sessionFactory.currentSession.flush()

        then: 'verify initial readyForExtraction and status'
        ownerEntity.readyForExtraction == READY
        ownerEntity.elementStatus == ELS.Pending

        when: 'we have a spcific status and a modification'
        ownerEntity.elementStatus = statusToTest
        ownerEntity.description += ' a change '
        sessionFactory.currentSession.flush()

        then: 'the expect readyForExtraction == READY'
        ownerEntity.readyForExtraction == READY

        when: 'disableUpdateReadyForExtraction == true'
        ownerEntity.disableUpdateReadyForExtraction = true
        ownerEntity.readyForExtraction = NOT_READY
        sessionFactory.currentSession.flush()

        then: 'readyForExtraction will remain NOT_READY'
        ownerEntity.readyForExtraction == NOT_READY

        where:
        desc                   | statusToTest
        'Element with status ' | ELS.Pending
        'Element with status ' | ELS.Published
        'Element with status ' | ELS.Deprecated
        'Element with status ' | ELS.Retired
    }
}
