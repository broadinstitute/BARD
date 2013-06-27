package bard.db.context.item

import bard.db.ContextService
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.registration.Assay
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletResponse

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
                contextService.createAssayContext((Assay) owningContext, cardName, cardSection)
            }
            if (owningContext instanceof Project) {
                contextService.createProjectContext((Project) owningContext, cardName, cardSection)
            }
        } catch (AccessDeniedException ae) {
            render(status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null)
            return
        }
        render(template: "/context/list", model: [contextOwner: owningContext, contexts: owningContext.groupContexts(), subTemplate: 'edit'])
    }

    def deleteEmptyCard(String contextClass, Long contextId) {
        AbstractContext context = BasicContextItemCommand.getContextClass(contextClass).findById(contextId)
        AbstractContextOwner owningContext = context.owner
        try {
            if (owningContext instanceof Assay) {
                contextService.deleteAssayContext((Assay) owningContext, context)
            }
            if (owningContext instanceof Project) {
                contextService.deleteProjectContext((Project) owningContext, context)
            }
        } catch (AccessDeniedException ae) {
            render(status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null)
            return
        }
        render(template: "/context/list", model: [contextOwner: owningContext, contexts: owningContext.groupContexts(), subTemplate: 'edit'])
    }

}
