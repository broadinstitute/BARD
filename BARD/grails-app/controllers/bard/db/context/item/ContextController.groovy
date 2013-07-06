package bard.db.context.item

import bard.db.ContextService
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.project.ProjectController
import bard.db.registration.Assay
import bard.db.registration.AssayDefinitionController
import grails.plugins.springsecurity.Secured
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletResponse
@Secured(['isAuthenticated()'])
class ContextController {
    ContextService contextService

    def createCard(String contextClass, Long ownerId, String cardName, String cardSection) {
        if (ownerId == null) {
            render(status: HttpServletResponse.SC_BAD_REQUEST, text: "OwnerId is required", contentType: 'text/plain', template: null)
            return

        }
        AbstractContextOwner owningContext = BasicContextItemCommand.getContextOwnerClass(contextClass).findById(ownerId)
        try {
            if (owningContext instanceof Assay) {
                contextService.createAssayContext(owningContext.id, owningContext, cardName, cardSection)
            }
            if (owningContext instanceof Project) {
                contextService.createProjectContext(owningContext.id, owningContext,cardName, cardSection)
            }
        } catch (AccessDeniedException ae) {
            render(status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null)
            return
        }
        render(template: "/context/list", model: [contextOwner: owningContext, contexts: owningContext.groupBySection(cardSection), subTemplate: 'edit'])
    }

    def deleteEmptyCard(String contextClass, Long contextId, String section) {
        AbstractContext context = BasicContextItemCommand.getContextClass(contextClass).findById(contextId)
        AbstractContextOwner owningContext = context.owner
        try {
            if (owningContext instanceof Assay) {
                contextService.deleteAssayContext(owningContext.id,owningContext,context)
            }
            if (owningContext instanceof Project) {
                contextService.deleteProjectContext(owningContext.id,owningContext, context)
            }
        } catch (AccessDeniedException ae) {
            render(status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null)
            return
        }
        String controller = contextClass == 'AssayContext' ? AssayDefinitionController.simpleName : ProjectController.simpleName
        controller = controller.replaceAll('Controller', '')
        redirect(controller: controller, action: 'editContext', params: [groupBySection: section, 'id': owningContext.id])
    }

}
