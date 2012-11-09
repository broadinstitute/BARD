package bard.db.experiment

import bard.db.dictionary.Element
import grails.plugin.spock.IntegrationSpec
import org.junit.Before
import bard.db.project.ProjectStep
import bard.db.project.StepContext
import bard.db.project.StepContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectStepIntegrationSpec extends IntegrationSpec {

    ProjectStep domainInstance
    StepContext stepContext
    StepContextItem stepContextItem

    @Before
    void doSetup() {
        domainInstance = ProjectStep.buildWithoutSave()

        stepContext = StepContext.buildWithoutSave()
        domainInstance.addToStepContexts(stepContext)

        stepContextItem = StepContextItem.buildWithoutSave(attributeElement: Element.build())
        stepContext.addToStepContextItems(stepContextItem)
    }

    void "test stepContext cascade save'"() {
        when:
        domainInstance == domainInstance.save(flush: true)

        then:
        domainInstance.id != null
        stepContext.id != null
    }

    void "test stepContextItem cascade save'"() {
        when:
        domainInstance == domainInstance.save(flush: true)

        then:
        domainInstance.id != null
        stepContextItem.id != null
    }

}
