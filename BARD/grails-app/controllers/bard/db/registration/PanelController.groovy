package bard.db.registration

import acl.CapPermissionService
import bard.db.command.BardCommand
import bard.db.project.InlineEditableCommand
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.Validateable
import groovy.transform.InheritConstructors
import org.springframework.security.access.AccessDeniedException

@Mixin(EditingHelper)
@Secured(['isAuthenticated()'])
class PanelController {

    static allowedMethods = [associateAssay: "POST", disassociateAssay: "POST"]
    SpringSecurityService springSecurityService
    def permissionEvaluator
    CapPermissionService capPermissionService

    PanelService panelService



    def deletePanel() {
        Panel panel = Panel.get(params.id)

        if (!canEdit(permissionEvaluator, springSecurityService, panel)) {
            render accessDeniedErrorMessage()
            return
        }
        this.panelService.deletePanel(panel.id)
        flash.message = "Panel deleted successfully"
        redirect(action: "list")
    }

    def addAssayToPanel(AssociatePanelCommand associatePanelCommand) {
        return [associatePanelCommand: associatePanelCommand]
    }

    def addAssays(AssociatePanelCommand associatePanelCommand) {

        if (!associatePanelCommand.id) {
            [associatePanelCommand: new AssociatePanelCommand()]
        }
        if (!request.post) {
            return [associatePanelCommand: associatePanelCommand]
        }
        if (associatePanelCommand.hasErrors()) {
            return [associatePanelCommand: associatePanelCommand]
        }

        this.panelService.associateAssays(associatePanelCommand.id, associatePanelCommand.assays)

        redirect(controller: "panel", action: "show", id: associatePanelCommand.id)


    }

    def addAssay(AssociatePanelCommand associatePanelCommand) {
        if (!associatePanelCommand.id) {
            [associatePanelCommand: new AssociatePanelCommand()]
        }
        if (!request.post) {
            return [associatePanelCommand: associatePanelCommand]
        }
        if (associatePanelCommand.hasErrors()) {
            return [associatePanelCommand: associatePanelCommand]
        }
        this.panelService.associateAssay(associatePanelCommand.assays.get(0), associatePanelCommand.id)
        redirect(controller: "panel", action: "show", id: associatePanelCommand.id)

    }

    def removeAssays(AssociatePanelCommand associatePanelCommand) {

        if (!associatePanelCommand.id) {
            [associatePanelCommand: new AssociatePanelCommand()]
        }
        if (!request.post) {
            return [associatePanelCommand: associatePanelCommand]
        }
        if (associatePanelCommand.hasErrors()) {
            return [associatePanelCommand: associatePanelCommand]
        }

        this.panelService.disassociateAssays(associatePanelCommand.id, associatePanelCommand.assays)
        redirect(controller: "panel", action: "show", id: associatePanelCommand.id)

    }

    def removeAssay(AssociatePanelCommand associatePanelCommand) {
        if (!associatePanelCommand.id) {
            [associatePanelCommand: new AssociatePanelCommand()]
        }
        if (associatePanelCommand.hasErrors()) {
            return [associatePanelCommand: associatePanelCommand]
        }
        final Assay assay = associatePanelCommand.assays.get(0)
        this.panelService.disassociateAssay(assay, associatePanelCommand.id)
        redirect(controller: "panel", action: "show", id: associatePanelCommand.id)
    }


    def list() {
        render(view: "findByName", params: params, model: [panels: Panel.findAll()])
    }

    def editDescription(InlineEditableCommand inlineEditableCommand) {
        try {
            Panel panel = Panel.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(panel.version, Panel.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Panel.PANEL_DESCRIPTION_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            panel = panelService.updatePanelDescription(inputValue, inlineEditableCommand.pk)
            if (panel?.hasErrors()) {
                throw new Exception("Error while editing panel Description")
            }
            generateAndRenderJSONResponse(panel.version, panel.modifiedBy, "", panel.lastUpdated, panel.description)
        }
        catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        }
        catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editPanelName(InlineEditableCommand inlineEditableCommand) {
        try {
            Panel panel = Panel.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(panel.version, Panel.class)
            if (message) {
                conflictMessage(message)
                return
            }

            final String inputValue = inlineEditableCommand.value.trim()
            String maxSizeMessage = validateInputSize(Panel.PANEL_NAME_MAX_SIZE, inputValue.length())
            if (maxSizeMessage) {
                editExceedsLimitErrorMessage(maxSizeMessage)
                return
            }
            panel = panelService.updatePanelName(inputValue, inlineEditableCommand.pk)
            if (panel?.hasErrors()) {
                throw new Exception("Error while editing panel Name")
            }
            generateAndRenderJSONResponse(panel.version, panel.modifiedBy, "", panel.lastUpdated, panel.name)
        }
        catch (AccessDeniedException ade) {
            log.error(ade)
            render accessDeniedErrorMessage()
        }
        catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }



    def index() {
        redirect(action: "list")
    }

    def save(PanelCommand panelCommand) {
        if (!panelCommand.validate()) {
            render(view: "create", model: [panelCommand: panelCommand])
            return
        }
        final Panel panel = panelCommand.createNewPanel()
        if (panel) {
            redirect(action: "show", id: panel.id)
            return
        }
        render(view: "create", model: [panelCommand: panelCommand])
    }

    def create() {
        return [panelCommand: new PanelCommand()]
    }



    def show() {
        Panel panelInstance = Panel.get(params.id)

        if (!panelInstance) {
            def messageStr = message(code: 'default.not.found.message', args: [message(code: 'panel.label', default: 'Panel'), params.id])
            return [message: messageStr]
        }

        boolean editable = canEdit(permissionEvaluator, springSecurityService, panelInstance)
        String owner = capPermissionService.getOwner(panelInstance)
        return [panelInstance: panelInstance, panelOwner: owner, editable: editable ? 'canedit' : 'cannotedit']
    }




    def getNames() {
        def results
        if (params?.term)
            results = Panel.findAllByNameIlike("%${params.term}%", [sort: "name", order: "asc"])
        else
            results = Panel.list()

        render(contentType: "text/json") {
            if (results) {
                for (a in results) {
                    element a.name
                }
            } else
                element ""
        }
    }

    def findById() {
        if (params.id && params.id.isLong()) {
            Panel panelInstance = Panel.findById(params.id.toLong())
            if (panelInstance?.id)
                redirect(action: "show", id: panelInstance.id)
            else
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'panel.label', default: 'Panel'), params.panelId])
        }
    }

    def findByName() {
        if (params.name) {
            def panels = Panel.findAllByNameIlike("%${params.name}%")
            if (panels?.size() > 1) {
                if (params.sort == null) {
                    params.sort = "id"
                }
                panels.sort {
                    a, b ->
                        if (params.order == 'desc') {
                            b."${params.sort}" <=> a."${params.sort}"
                        } else {
                            a."${params.sort}" <=> b."${params.sort}"
                        }
                }
                render(view: "findByName", params: params, model: [panels: panels])
            } else if (panels?.size() == 1) {
                redirect(action: "show", id: panels.get(0).id)
            } else {
                flash.message = message(code: 'default.not.found.property.message', args: [message(code: 'panel.label', default: 'Panel'), "name", params.name])
            }
        }
    }
}
@InheritConstructors
@Validateable
class AssociatePanelCommand extends BardCommand {
    EditingHelper editingHelper = new EditingHelper()
    Long id
    String assayIds
    def PermissionEvaluator
    SpringSecurityService springSecurityService

    AssociatePanelCommand() {}

    List<Assay> getAssays() {
        List<Assay> sourceAssays = []
        for (Long id : getAssayIdsAsLong()) {
            sourceAssays.add(Assay.findById(id))
        }
        return sourceAssays
    }

    List<Long> getAssayIdsAsLong() {
        return MergeAssayDefinitionService.convertStringToIdList(assayIds)
    }

    Panel getPanel() {
        return Panel.get(id)
    }

    static constraints = {
        id(nullable: false, validator: { value, command, err ->
            Panel panel = Panel.get(value.toLong())
            if (panel) {
                if (!command.editingHelper.canEdit(command.permissionEvaluator, command.springSecurityService, panel)) {
                    err.rejectValue('id', "message.code", "You do not have the privileges to add Assays to this Panel PLID:${value}");
                }
            } else {
                err.rejectValue("id", "message.code", "Panel with ID:${value} cannot be found");
            }

        })
        assayIds blank: false, nullable: false, validator: { value, command, err ->
            if (value) {
                final List<Long> assayIds = command.getAssayIdsAsLong()
                for (Long assayId : assayIds) {
                    Assay assay = Assay.get(assayId)
                    if (assay) {
                        if (!command.editingHelper.canEdit(command.permissionEvaluator, command.springSecurityService, assay)) {
                            err.rejectValue("assayIds", "message.code", "You do not have the privileges to add ADID:${value} to this Panel");
                        }
                    } else {
                        err.rejectValue("assayIds", "message.code", "ADID:${value} not found");
                    }
                }
            }
        }
    }
}
@InheritConstructors
@Validateable
class PanelCommand extends BardCommand {

    String name
    String description
    SpringSecurityService springSecurityService


    static constraints = {
        importFrom(Panel, exclude: ['readyForExtraction', 'lastUpdated', 'dateCreated'])
    }

    PanelCommand() {}



    Panel createNewPanel() {
        Panel panelToReturn = null
        if (validate()) {
            Panel tempPanel = new Panel()
            copyFromCmdToDomain(tempPanel)
            if (attemptSave(tempPanel)) {
                panelToReturn = tempPanel
            }
        }
        return panelToReturn
    }


    void copyFromCmdToDomain(Panel panel) {

        panel.modifiedBy = springSecurityService.principal?.username
        panel.name = this.name
        panel.description = this.description
    }
}
