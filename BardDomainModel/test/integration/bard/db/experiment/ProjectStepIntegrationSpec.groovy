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
class ProjectStepIntegrationSpec extends IntegrationSpec {

    ProjectStep domainInstance
    SessionFactory sessionFactory
    Session session

    @Before
    void doSetup() {
        domainInstance = ProjectStep.buildWithoutSave()
        session = sessionFactory.currentSession
    }

    void "test stepContextItem cascade save'"() {


        when:
        StepContextItem stepContextItem = StepContextItem.buildWithoutSave()
        stepContextItem.attributeElement.save()
        domainInstance.addToStepContextItems(stepContextItem)

        then:
        domainInstance == domainInstance.save(flush: true)
        def id = domainInstance.id

        when:
        session.clear()
        domainInstance = null
        domainInstance = ProjectStep.get(id)

        then:
        domainInstance.stepContextItems.size() == 1
    }

}
