package bard.db.context.item

import bard.db.ContextService
import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextOwner
import bard.db.project.Project
import bard.db.registration.Assay

class ContextController {
    ContextService contextService

    def createCard(String contextClass, Long ownerId, String cardName, String cardSection) {
        if (ownerId == null) {
            throw new RuntimeException("bad instance")
        }

        AbstractContextOwner owningContext = BasicContextItemCommand.getContextOwnerClass(contextClass).findById(ownerId)
        if (owningContext instanceof Assay) {
            contextService.createAssayContext((Assay) owningContext, cardName, cardSection)
        }
        if (owningContext instanceof Experiment) {
            contextService.createExperimentContext((Experiment) owningContext, cardName, cardSection)
        }
        if (owningContext instanceof Project) {
            contextService.createProjectContext((Project) owningContext, cardName, cardSection)
        }

        render(template: "/context/list", model: [contextOwner: owningContext, contexts: owner.groupContexts(), subTemplate: 'edit'])


    }

    def deleteEmptyCard(String contextClass, Long contextId) {
        AbstractContext context = BasicContextItemCommand.getContextClass(contextClass).findById(contextId)
        AbstractContextOwner owningContext = context.owner
        if (owningContext instanceof Assay) {
            contextService.deleteAssayContext((Assay) owningContext, context)
        }
        if (owningContext instanceof Experiment) {
            contextService.deleteExperimentContext((Experiment) owningContext, context)
        }
        if (owningContext instanceof Project) {
            contextService.deleteProjectContext((Project) owningContext, context)
        }
        render(template: "/context/list", model: [contextOwner: owningContext, contexts: owningContext.groupContexts(), subTemplate: 'edit'])
    }

}
