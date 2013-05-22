package bard.context.item

import bard.db.context.item.BasicContextItemCommand
import bard.db.context.item.ContextItemController
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.project.ProjectService
import grails.buildtestdata.mixin.Build
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ContextItemController)
@Build([ProjectContext,ProjectContextItem])
class ContextItemControllerUnitSpec extends Specification {

    def setup() {

    }

    def cleanup() {
    }

    void "test delete"() {
        given:
        ProjectContext projectContext = ProjectContext.build()
        ProjectContextItem contextItem = ProjectContextItem.build(context:projectContext)

        BasicContextItemCommand basicContextItemCommand = new BasicContextItemCommand(context:projectContext,contextItem: contextItem,contextId: projectContext.id,contextItemId: contextItem.id)
        basicContextItemCommand.projectService=Mock(ProjectService)
        when:
        controller.delete(basicContextItemCommand)
        then:
        assert response.redirectedUrl.startsWith("/project/editContext#")
    }
}