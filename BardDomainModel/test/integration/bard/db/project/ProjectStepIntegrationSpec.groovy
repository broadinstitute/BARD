package bard.db.project

import bard.db.BardIntegrationSpec
import bard.db.dictionary.Element
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectStepIntegrationSpec extends BardIntegrationSpec {

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
