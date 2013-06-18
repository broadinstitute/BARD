package bard.db.context.item

import bard.db.command.BardCommand
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.registration.Assay
import bard.db.registration.AssayContext
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.validation.Validateable
import groovy.transform.InheritConstructors

import javax.servlet.http.HttpServletResponse

@Secured(['isAuthenticated()'])
class ContextItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", updatePreferredName: "POST"]

    def index() {}

    def create(Long contextId, String contextClass, Long contextOwnerId) {
        BasicContextItemCommand command = new BasicContextItemCommand(contextId: contextId, contextClass: contextClass, contextOwnerId: contextOwnerId)
        command.context = command.attemptFindContext()
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
        AbstractContextItem contextItem = contextItemCommand.attemptFindItem()
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

        basicContextItemCommand.delete()

        redirect(controller: basicContextItemCommand.ownerController, action: "editContext", id: basicContextItemCommand.contextOwnerId, fragment: "card-${basicContextItemCommand.contextId}")
    }

    def updatePreferredName(InlineUpdateCommand command) {
        attemptUpdate {
            AbstractContext context = BasicContextItemCommand.getContextClass(command.contextClass).findById(command.id)
            context.preferredName = command.value

            return context.preferredName
        }
    }

    def deleteContext(DeleteContextCommand command) {
        AbstractContext context = BasicContextItemCommand.getContextClass(command.contextClass).findById(command.id)
        context.getOwner().removeContext(context)
        context.delete()
    }

    private Object attemptUpdate(Closure body) {
        try {
            def newValue = body.call()

            JSON jsonResponse = [data: newValue] as JSON
            render status: HttpServletResponse.SC_OK, text: jsonResponse, contentType: 'text/json', template: null
        } catch (Exception ee) {
            log.error("update failed", ee)
            render(status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: message(code: 'editing.error.message'), contentType: 'text/plain', template: null)
        }
    }
}

@InheritConstructors
@Validateable
class InlineUpdateCommand extends BardCommand {
    Long id //primary key of the current entity
    String contextClass
    String value //the new value

    static constraints = {
        id(blank: false, nullable: false)
        contextClass(blank: false, nullable: false, inList: ["AssayContext", "ProjectContext"])
        value(blank: false, nullable: false)
    }
}

@InheritConstructors
@Validateable
class DeleteContextCommand extends BardCommand {
    Long id //primary key of the current entity
    String contextClass
    String value //the new value

    static constraints = {
        id(blank: false, nullable: false)
        contextClass(blank: false, nullable: false, inList: ["AssayContext", "ProjectContext"])
    }
}

