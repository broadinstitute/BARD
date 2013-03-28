package bard.db.context.item

import bard.db.model.AbstractContextItem
import bard.db.project.ProjectContext
import bard.db.project.ProjectContextItem
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

    def edit(BasicContextItemCommand contextItemCommand){
        ProjectContextItem contextItem = contextItemCommand.attemptFindById(ProjectContextItem, contextItemCommand.contextItemId)
        if (!contextItem){
            render(view: "edit", model: [instance: contextItemCommand])
        }
        else{
            render(view: "edit", model: [instance:new BasicContextItemCommand(contextItem)])
        }
    }

    def delete(BasicContextItemCommand basicContextItemCommand) {

        assert basicContextItemCommand.delete()

        redirect(controller: basicContextItemCommand.ownerController, action: "editContext", id: basicContextItemCommand.contextOwnerId, fragment: "card-${basicContextItemCommand.contextId}")
    }
}
