package bard.db.context.item

import bard.db.ContextItemService
import bard.db.ContextService
import bard.db.context.item.BasicContextItemCommand
import bard.db.context.item.ContextItemController
import bard.db.enums.ContextType
import bard.db.project.Project
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import bard.db.project.ProjectService
import bard.db.registration.AssayContext
import bard.db.registration.AssayContextItem
import bard.db.registration.EditingHelper
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.PermissionEvaluator
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ContextItemController)
@Build([ProjectContext, ProjectContextItem, AssayContext, AssayContextItem])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class ContextItemControllerUnitSpec extends Specification {

    def setup() {
        controller.metaClass.mixin(EditingHelper)
        controller.contextService = Mock(ContextService)
        controller.permissionEvaluator = Mock(PermissionEvaluator)
        controller.springSecurityService = Mock(SpringSecurityService)
    }

    def cleanup() {
    }
   // contextItemService.updateAssayContextItem((Assay) owner, this)
    void 'test update - Project Context Item -Success'() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        ProjectContext projectContext = ProjectContext.build()
        ProjectContextItem contextItem = ProjectContextItem.build(context: projectContext)

        BasicContextItemCommand basicContextItemCommand =
            new BasicContextItemCommand(context: projectContext, contextItem: contextItem, contextId: projectContext.id,
                    contextItemId: contextItem.id, attributeElementId: contextItem.attributeElement.id)
        basicContextItemCommand.contextItemService = Mock(ContextItemService)
        when:
        controller.update(basicContextItemCommand)
        then:
        assert view == "/contextItem/edit"
        assert model.instance
   }
    void 'test create - Project Context'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        ProjectContext projectContext = ProjectContext.build()
        String contextClass = "ProjectContext"
        when:
        controller.create(projectContext.id, contextClass, projectContext.owner.id)
        then:
        assert response.status == HttpServletResponse.SC_OK
    }

    void 'test create - Assay Context'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        AssayContext assayContext = AssayContext.build()
        String contextClass = "AssayContext"
        when:
        controller.create(assayContext.id, contextClass, assayContext.owner.id)
        then:
        assert response.status == HttpServletResponse.SC_OK
    }

    void 'test save - ProjectContextItem Success'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        ProjectContext projectContext = ProjectContext.build()
        BasicContextItemCommand basicContextItemCommand = new BasicContextItemCommand(context: projectContext, contextId: projectContext.id)
        basicContextItemCommand.contextItemService = Mock(ContextItemService)

        when:
        controller.save(basicContextItemCommand)
        then:
        assert response.status == HttpServletResponse.SC_OK
        assert view == "/contextItem/create"

    }

    void 'test save - ProjectContextItem Unauthorized error'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return false
        }
        ProjectContext projectContext = ProjectContext.build()
        BasicContextItemCommand basicContextItemCommand = new BasicContextItemCommand(context: projectContext, contextId: projectContext.id)
        basicContextItemCommand.contextItemService = Mock(ContextItemService)

        when:
        controller.save(basicContextItemCommand)
        then:
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"

    }

    void 'test edit - ProjectContextItem Success'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        ProjectContext projectContext = ProjectContext.build()
        ProjectContextItem contextItem = ProjectContextItem.build(context: projectContext)

        BasicContextItemCommand basicContextItemCommand =
            new BasicContextItemCommand(context: projectContext, contextItem: contextItem, contextId: projectContext.id,
                    contextItemId: contextItem.id, attributeElementId: contextItem.attributeElement.id)
        basicContextItemCommand.contextItemService = Mock(ContextItemService)

        when:
        controller.save(basicContextItemCommand)
        then:
        basicContextItemCommand.contextItemService.createProjectContextItem(_, _) >> { return true }
        assert response.status == HttpServletResponse.SC_OK
        assert view == "/contextItem/edit"

    }

    void 'test edit - ProjectContextItem - UnAuthorized'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return false
        }
        ProjectContext projectContext = ProjectContext.build()
        ProjectContextItem contextItem = ProjectContextItem.build(context: projectContext)

        BasicContextItemCommand basicContextItemCommand =
            new BasicContextItemCommand(context: projectContext, contextItem: contextItem, contextId: projectContext.id,
                    contextItemId: contextItem.id, attributeElementId: contextItem.attributeElement.id)
        basicContextItemCommand.contextItemService = Mock(ContextItemService)

        when:
        controller.save(basicContextItemCommand)
        then:
        basicContextItemCommand.contextItemService.createProjectContextItem(_, _) >> { return true }
        assert response.status == HttpServletResponse.SC_FORBIDDEN
        assert response.text == "editing.forbidden.message"

    }


    void 'test save - AssayContextItem Success'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        AssayContext assayContext = AssayContext.build()
        BasicContextItemCommand basicContextItemCommand = new BasicContextItemCommand(context: assayContext, contextId: assayContext.id)
        basicContextItemCommand.contextItemService = Mock(ContextItemService)

        when:
        controller.save(basicContextItemCommand)
        then:
        assert response.status == HttpServletResponse.SC_OK
        assert view == "/contextItem/create"

    }

    void 'test edit - AssayContextItem Success'() {
        given:
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        AssayContext assayContext = AssayContext.build()
        AssayContextItem contextItem = AssayContextItem.build(context: assayContext)

        BasicContextItemCommand basicContextItemCommand =
            new BasicContextItemCommand(context: assayContext,
                    contextItem: contextItem, contextId: assayContext.id, contextItemId: contextItem.id,
                    contextClass: 'AssayContext',
                    attributeElementId: contextItem.attributeElement.id)
        basicContextItemCommand.contextItemService = Mock(ContextItemService)

        when:
        controller.save(basicContextItemCommand)
        then:
        basicContextItemCommand.contextItemService.createAssayContextItem(_, _) >> { return true }

        assert response.status == HttpServletResponse.SC_OK
        assert view == "/contextItem/edit"

    }

    void "test delete"() {
        given:
        ProjectContext projectContext = ProjectContext.build(contextType: ContextType.UNCLASSIFIED)
        ProjectContextItem contextItem = ProjectContextItem.build(context:projectContext)

        BasicContextItemCommand basicContextItemCommand = new BasicContextItemCommand(
                contextOwnerId: projectContext.owner.id,
                context:projectContext,
                contextId: projectContext.id,
                contextItem: contextItem,
                contextItemId: contextItem.id)
        basicContextItemCommand.contextItemService=Mock(ContextItemService)
        when:
        controller.delete(basicContextItemCommand)
        then:
        assert response.redirectedUrl == "/project/editContext/${projectContext.owner.id}?groupBySection=Unclassified"
    }
}