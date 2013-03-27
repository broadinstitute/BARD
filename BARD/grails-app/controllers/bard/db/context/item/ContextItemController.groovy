package bard.db.context.item

import bard.db.model.AbstractContextItem
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class ContextItemController {

    def index() {}

    def create(Long contextId, String contextClass, Long contextOwnerId) {
        [instance: new BasicContextItemCommand(contextId: contextId, contextClass: contextClass, contextOwnerId: contextOwnerId)]
    }

    def save(BasicContextItemCommand contextItemCommand) {
        AbstractContextItem contextItem = contextItemCommand.createNewContextItem()
        if (contextItem) {
            redirect(controller: 'project', action: "editContext", id: contextItem.context.owner.id, fragment: "card-${contextItem.context.id}")
        } else {
            render(view: "create", model: [instance: contextItemCommand])
        }
    }

}
