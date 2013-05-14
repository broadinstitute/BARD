package bard.db.context.item

import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
import grails.plugins.springsecurity.Secured

@Secured(['isAuthenticated()'])
class ContextItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {}

    def create(Long contextId, String contextClass, Long contextOwnerId) {

        BasicContextItemCommand command = new BasicContextItemCommand(contextId: contextId, contextClass: contextClass, contextOwnerId: contextOwnerId)
        command.context = command.attemptFindById(ProjectContext, contextId)
        [instance: command]
    }

    def save(BasicContextItemCommand contextItemCommand) {
        if (contextItemCommand.createNewContextItem()) {
            render(view: "edit", model: [instance: contextItemCommand, reviewNewItem:true])
        } else {
            render(view: "create", model: [instance: contextItemCommand])
        }
    }

    def edit(BasicContextItemCommand contextItemCommand) {
        ProjectContextItem contextItem = contextItemCommand.attemptFindById(ProjectContextItem, contextItemCommand.contextItemId)
        if (!contextItem) {
            render(view: "edit", model: [instance: contextItemCommand])
        } else {
            render(view: "edit", model: [instance: new BasicContextItemCommand(contextItem)])
        }
    }

    def update(BasicContextItemCommand contextItemCommand) {
        if (!contextItemCommand.update()) {
            render(view: "edit", model: [instance: contextItemCommand])
        } else {
            render(view: "edit", model: [instance: contextItemCommand, reviewNewItem:true])
        }

    }

    def delete(BasicContextItemCommand basicContextItemCommand) {

        assert basicContextItemCommand.delete()

        redirect(controller: basicContextItemCommand.ownerController, action: "editContext", id: basicContextItemCommand.contextOwnerId, fragment: "card-${basicContextItemCommand.contextId}")
    }
}
