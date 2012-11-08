package bard.db.experiment

import bard.db.registration.ExternalReference
import grails.plugin.spock.IntegrationSpec
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
    ProjectContext projectContext
    ProjectContextItem projectContextItem

    ProjectStep projectStep
    StepContext stepContext
    StepContextItem stepContextItem

    ExternalReference externalReference

    @Before
    void doSetup() {
        domainInstance = Project.buildWithoutSave()
    }

    public initializeProjectStep() {
        projectStep = ProjectStep.buildWithoutSave()
        domainInstance.addToProjectSteps(projectStep)
    }

    void initializeProjectContextItem() {
        projectContextItem = ProjectContextItem.buildWithoutSave()
        projectContext.addToProjectContextItems(projectContextItem)
        projectContextItem.attributeElement.save()
    }

    void initializeExternalReference() {
        externalReference = ExternalReference.buildWithoutSave()
        domainInstance.addToExternalReferences(externalReference)
    }

    void initializeProjectContext() {
        projectContext = ProjectContext.buildWithoutSave()
        domainInstance.addToProjectContexts(projectContext)
        projectContext
    }

    void initializeStepContext() {
        stepContext = StepContext.buildWithoutSave()
        projectStep.addToStepContexts(stepContext)
    }

    void initializeStepContextItem() {
        stepContextItem = StepContextItem.buildWithoutSave()
        stepContext.addToStepContextItems(stepContextItem)
        stepContextItem.attributeElement.save()
    }

    void "test projectContext cascade save"() {
        given:
        initializeProjectContext()
        assert projectContext.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectContext.id != null
    }

    void "test projectContextItems cascade save"() {
        given:
        initializeProjectContext()
        initializeProjectContextItem()
        assert projectContextItem.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectContextItem.id != null
    }

    void "test projectSteps cascade save"() {
        given:
        initializeProjectStep()
        assert projectStep.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectStep.id != null
    }

    void "test stepContexts cascade save"() {
        given:
        initializeProjectStep()
        initializeStepContext()
        assert stepContext.id == null
        when:
        domainInstance.save(flush: true)
        then:
        stepContext.id != null
    }

    void "test stepContextItems cascade save"() {
        given:
        initializeProjectStep()
        initializeStepContext()
        initializeStepContextItem()
        assert stepContextItem.id == null
        when:
        domainInstance.save(flush: true)
        then:
        stepContextItem.id != null
    }

    void "test externalReferences cascade save"() {
        given:
        initializeExternalReference()
        assert externalReference.id == null
        when:
        domainInstance.save()
        then:
        externalReference.id != null
    }

    void "test externalReferences cascade delete"() {
        given:
        initializeExternalReference()
        domainInstance.save()
        assert externalReference.id != null
        flushAndClear()

        when:
        domainInstance.refresh().delete()

        then:
        ExternalReference.findById(externalReference.id) == null
    }

    void flushAndClear() {
        Project.withSession {session ->
            session.flush()
            session.clear()
        }
    }
}