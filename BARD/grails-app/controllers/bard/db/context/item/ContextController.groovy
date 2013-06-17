package bard.db.context.item

import bard.db.ContextService
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner

class ContextController {
    ContextService contextService

    def createCard(String contextClass, Long ownerId, String cardName, String cardSection) {
        if (ownerId == null) {
            throw new RuntimeException("bad instance")
        }

        AbstractContextOwner owner = BasicContextItemCommand.getContextOwnerClass(contextClass).findById(ownerId)
        contextService.createContext(owner, cardName, cardSection)

        render(template: "/context/list", model: [contextOwner: owner, contexts: owner.groupContexts(), subTemplate: 'edit'])
    }

    def deleteEmptyCard(String contextClass, Long contextId) {
        AbstractContext context = BasicContextItemCommand.getContextClass(contextClass).findById(contextId)
        AbstractContextOwner owner = context.owner
        contextService.deleteContext(context)

        render(template: "/context/list", model: [contextOwner: owner, contexts: owner.groupContexts(), subTemplate: 'edit'])
    }

//    def updateCardTitle(Long src_assay_context_item_id, Long target_assay_context_id) {
//        AssayContextItem sourceAssayContextItem = AssayContextItem.findById(src_assay_context_item_id)
//        AssayContext targetAssayContext = AssayContext.findById(target_assay_context_id)
//        contextService.updateContextName(targetAssayContext, sourceAssayContextItem)
//        render(template: "/context/list", model: [contextOwner: targetAssayContext, contexts: targetAssayContext.groupContexts(), subTemplate: 'edit'])
//    }

}
