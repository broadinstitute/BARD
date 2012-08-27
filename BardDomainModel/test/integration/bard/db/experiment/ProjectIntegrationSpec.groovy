package bard.db.experiment

import bard.db.registration.ExternalReference
import grails.plugin.spock.IntegrationSpec
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.Before
import spock.lang.Ignore

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectIntegrationSpec extends IntegrationSpec {

    Project domainInstance
    SessionFactory sessionFactory
    Session session

    @Before
    void doSetup() {
        domainInstance = Project.buildWithoutSave()
        session = sessionFactory.currentSession
    }

    void "test projectContextItem cascade save'"() {


        when:
        ProjectContextItem projectContextItem = ProjectContextItem.buildWithoutSave()
        projectContextItem.attributeElement.save()
        domainInstance.addToProjectContextItems(projectContextItem)

        then:
        domainInstance == domainInstance.save(flush: true)
        def id = domainInstance.id

        when:
        session.clear()
        domainInstance = null
        domainInstance = Project.get(id)

        then:
        domainInstance.projectContextItems.size() == 1
    }

    @Ignore("temporarily ignore need to look into ORA-02290: check constraint (DDURKIN.CK_PROJECT_EXPERIMENT_NULLS) violated")
    void "test externalReferences '"() {

        when:
        domainInstance.addToExternalReferences(ExternalReference.build())

        then:
        domainInstance == domainInstance.save(flush: true)
        def id = domainInstance.id

        when:
        session.clear()
        domainInstance = null
        domainInstance = Project.get(id)

        then:
        domainInstance.externalReferences.size() == 1
    }

    void "test projectSteps cascade save'"() {

        when:
        domainInstance.addToProjectSteps(ProjectStep.buildWithoutSave())

        then:
        domainInstance == domainInstance.save(flush: true)
        def id = domainInstance.id

        when:
        session.clear()
        domainInstance = null
        domainInstance = Project.get(id)

        then:
        domainInstance.projectSteps.size() == 1
    }
}
