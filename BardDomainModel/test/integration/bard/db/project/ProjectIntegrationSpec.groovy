package bard.db.project

import bard.db.BardIntegrationSpec
import bard.db.registration.ExternalReference
import org.junit.Before
import org.springframework.dao.DataIntegrityViolationException

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectIntegrationSpec extends BardIntegrationSpec {

    Project domainInstance
    ProjectContext projectContext
    ProjectContextItem contextItem

    ProjectExperiment projectExperiment
    ProjectExperimentContext projectExperimentContext
    ProjectExperimentContextItem projectExperimentContextItem

    ExternalReference externalReference

    @Before
    void doSetup() {
        domainInstance = Project.buildWithoutSave()
    }

    public initializeProjectExperiment() {
        projectExperiment = ProjectExperiment.buildWithoutSave()
        projectExperiment.experiment.save()
        domainInstance.addToProjectExperiments(projectExperiment)
    }

    void initializeProjectContextItem() {
        contextItem = ProjectContextItem.buildWithoutSave()
        projectContext.addToContextItems(contextItem)
        contextItem.attributeElement.save()
    }

    void initializeExternalReference() {
        externalReference = ExternalReference.buildWithoutSave()
        domainInstance.addToExternalReferences(externalReference)
    }

    void initializeProjectContext() {
        projectContext = ProjectContext.buildWithoutSave()
        domainInstance.addToContexts(projectContext)
        projectContext
    }

    void initializeProjectExperimentContext() {
        projectExperimentContext = ProjectExperimentContext.buildWithoutSave()
        projectExperiment.addToProjectExperimentContexts(projectExperimentContext)
    }

    void initializeProjectExperimentContextItem() {
        projectExperimentContextItem = ProjectExperimentContextItem.buildWithoutSave()
        projectExperimentContext.addToContextItems(projectExperimentContextItem)
        projectExperimentContextItem.attributeElement.save()
    }

    void "test context cascade save"() {
        given:
        initializeProjectContext()
        assert projectContext.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectContext.id != null
    }

    void "test contextItems cascade save"() {
        given:
        initializeProjectContext()
        initializeProjectContextItem()
        assert contextItem.id == null
        when:
        domainInstance.save(flush: true)
        then:
        contextItem.id != null
    }

    void "test projectExperiments cascade save"() {
        given:
        initializeProjectExperiment()
        assert projectExperiment.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectExperiment.id != null
    }

    void "test stepContexts cascade save"() {
        given:
        initializeProjectExperiment()
        initializeProjectExperimentContext()
        assert projectExperimentContext.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectExperimentContext.id != null
    }

    void "test stepContextItems cascade save"() {
        given:
        initializeProjectExperiment()
        initializeProjectExperimentContext()
        initializeProjectExperimentContextItem()
        assert projectExperimentContextItem.id == null
        when:
        domainInstance.save(flush: true)
        then:
        projectExperimentContextItem.id != null
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
        flushAndClear()

        then: 'externalReference should be deleted to to belongsTo cascade delete'
        notThrown(DataIntegrityViolationException)
        ExternalReference.findById(externalReference.id) == null
    }

    void flushAndClear() {
        Project.withSession { session ->
            session.flush()
            session.clear()
        }
    }
}