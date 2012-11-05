package bard.db.experiment

import bard.db.dictionary.Element
import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import grails.plugin.spock.IntegrationSpec
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.junit.Before

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


    Project project
    ProjectContext projectContext
    ProjectContextItem projectContextItem
    ProjectStep projectStep
    ExternalReference externalReference

    @Before
    void doSetup() {
        domainInstance = Project.buildWithoutSave()
        session = sessionFactory.currentSession
        project = Project.buildWithoutSave()

        projectContext = ProjectContext.buildWithoutSave()
        project.addToProjectContexts(projectContext)
        projectContextItem = ProjectContextItem.buildWithoutSave(attributeElement: Element.build())
        projectContext.addToProjectContextItems(projectContextItem)

        projectStep = ProjectStep.buildWithoutSave()
        project.addToProjectSteps(projectStep)

        externalReference = ExternalReference.buildWithoutSave(externalSystem: ExternalSystem.build())
        project.addToExternalReferences(externalReference)
    }

    void "test projectContext cascade save"() {

        when:
        project.save(flush: true)

        then:
        project.id != null
        projectContext.id != null

    }

    void "test projectContextItems cascade save"() {

        when:
        project.save(flush: true)

        then:
        project.id != null
        projectContextItem.id != null

    }

    void "test externalReferences cascade save"() {

        when:
        project.save(flush: true)

        then:
        project.id != null
        externalReference.id != null

    }

    void "test projectSteps cascade save"() {

        when:
        project.save(flush: true)

        then:
        project.id != null
        projectStep.id != null
    }
}