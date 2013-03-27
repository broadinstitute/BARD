package bard.db.context.item

import bard.db.model.AbstractContextItem
import bard.db.project.ProjectController
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class ContextItemController {

    def index() {}

    def create(Long contextId, String contextClass) {
        [instance: new BasicContextItemCommand(contextId: contextId, contextClass: contextClass)]
    }

    def save(BasicContextItemCommand contextItemCommand) {
        AbstractContextItem contextItem= contextItemCommand.createNewContextItem()
        if (contextItem) {
            redirect(controller: 'project', action: "editContext", id: contextItem.context.owner.id, fragment: "card-${contextItem.context.id}")
        } else {
            render(view: "create", model: [instance: contextItemCommand])
        }
    }
}
