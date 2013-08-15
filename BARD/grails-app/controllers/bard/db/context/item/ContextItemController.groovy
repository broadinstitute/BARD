package bard.db.context.item

import bard.db.ContextService
import bard.db.command.BardCommand
import bard.db.experiment.Experiment
import bard.db.model.AbstractContext
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractContextOwner
import bard.db.project.InlineEditableCommand
import bard.db.project.Project
import bard.db.registration.Assay
import bard.db.registration.EditingHelper
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.springframework.security.access.AccessDeniedException

import javax.servlet.http.HttpServletResponse

@Mixin(EditingHelper)
@Secured(['isAuthenticated()'])
class ContextItemController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", updatePreferredName: "POST"]
    ContextService contextService
    def permissionEvaluator
    SpringSecurityService springSecurityService

    def index() {}

    def create(Long contextId, String contextClass, Long contextOwnerId) {
        BasicContextItemCommand command = new BasicContextItemCommand(contextId: contextId, contextClass: contextClass, contextOwnerId: contextOwnerId)

        command.context = command.attemptFindContext()
        if (command.context) {
            boolean canCreateOrEdit = canEdit(permissionEvaluator, springSecurityService, command?.context?.getOwner())
            if (!canCreateOrEdit) {
                render accessDeniedErrorMessage()
                return
            }
        }
        [instance: command]
    }

    def save(BasicContextItemCommand contextItemCommand) {
        def context = contextItemCommand.attemptFindById(contextItemCommand.getContextClass(contextItemCommand.contextClass), contextItemCommand.contextId)
        if (context) {
            boolean canCreateOrEdit = canEdit(permissionEvaluator, springSecurityService, context.getOwner())
            if (!canCreateOrEdit) {
                render accessDeniedErrorMessage()
                return
            }
        }

        if (contextItemCommand.createNewContextItem()) {
            render(view: "edit", model: [instance: contextItemCommand, reviewNewItem: true])
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
        contextItemCommand.context = contextItemCommand.attemptFindContext()

        if (!contextItemCommand.update()) {
            render(view: "edit", model: [instance: contextItemCommand])
        } else {
            render(view: "edit", model: [instance: contextItemCommand, reviewNewItem: true])
        }
    }

    def delete(BasicContextItemCommand basicContextItemCommand) {

        basicContextItemCommand.delete()

        redirect(controller: basicContextItemCommand.ownerController, action: "editContext",
                params: [id:basicContextItemCommand.contextOwnerId,groupBySection: basicContextItemCommand.context?.getSectionKey()])
    }

    def updatePreferredName(InlineEditableCommand inlineEditableCommand) {
     attemptUpdate {
         InlineUpdateCommand command = new InlineUpdateCommand(inlineEditableCommand)
            AbstractContext context = BasicContextItemCommand.getContextClass(command.contextClass).findById(command.id)
            AbstractContextOwner owningContext = context.owner
            if (owningContext instanceof Assay) {
                final String name = contextService.updatePreferredAssayContextName(owningContext.id, context, command.value)
                render(status: HttpServletResponse.SC_OK, text: name, contentType: 'text/plain', template: null)
                return
            }
            if (owningContext instanceof Experiment) {
                String name= contextService.updatePreferredExperimentContextName(owningContext.id, context, command.value)
                render(status: HttpServletResponse.SC_OK, text: name, contentType: 'text/plain', template: null)
                return
            }
            if (owningContext instanceof Project) {
                String name= contextService.updatePreferredProjectContextName(owningContext.id, context, command.value)
                render(status: HttpServletResponse.SC_OK, text: name, contentType: 'text/plain', template: null)
                return
            }

           render(status: HttpServletResponse.SC_BAD_REQUEST, text: context.preferredName, contentType: 'text/plain', template: null)
        }
    }


    private Object attemptUpdate(Closure body) {
        try {
            def newValue = body.call()

            JSON jsonResponse = [data: newValue] as JSON
            render status: HttpServletResponse.SC_OK, text: jsonResponse, contentType: 'text/json', template: null
        }
        catch (AccessDeniedException ae) {
            log.error("Access denied", ae)
            render(status: HttpServletResponse.SC_FORBIDDEN, text: message(code: 'editing.forbidden.message'), contentType: 'text/plain', template: null)

        }
        catch (Exception ee) {
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

    InlineUpdateCommand(){}
    InlineUpdateCommand(InlineEditableCommand inlineEditableCommand){
        id = inlineEditableCommand.pk
        contextClass=inlineEditableCommand.name
        value = inlineEditableCommand.value
    }
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

