package bard.db.registration

import acl.CapPermissionService
import bard.db.command.BardCommand
import bard.db.people.Role
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

    def myPanels() {
        List<Panel> panels = capPermissionService.findAllObjectsForRoles(Panel)
        Set<Panel> uniquePanels = new HashSet<Panel>(panels)
        [panels: uniquePanels]
    }

    def deletePanel() {
        Panel panel = Panel.get(params.id)

        if (!canEdit(permissionEvaluator, springSecurityService, panel)) {
            render accessDeniedErrorMessage()
            return
        }
        this.panelService.deletePanel(panel.id)
        flash.message = "Panel deleted successfully"
        redirect(action: "myPanels")
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
    def editOwnerRole(InlineEditableCommand inlineEditableCommand) {
        try {
            final Role ownerRole = Role.findByDisplayName(inlineEditableCommand.value)?:Role.findByAuthority(inlineEditableCommand.value)
            if (!ownerRole) {
                editBadUserInputErrorMessage("Could not find a registered team with name ${inlineEditableCommand.value}")
                return
            }
            Panel panel = Panel.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(panel.version, Panel.class)
            if (message) {
                conflictMessage(message)
                return
            }
            if (!BardCommand.isRoleInUsersRoleList(ownerRole)) {
                editBadUserInputErrorMessage("You do not have the permission to select team: ${inlineEditableCommand.value}")
                return
            }
            panel = panelService.updatePanelOwnerRole(inlineEditableCommand.pk,ownerRole)
            generateAndRenderJSONResponse(panel.version, panel.modifiedBy, "", panel.lastUpdated, panel.ownerRole.displayName)

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
        redirect(action: "myPanels")
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
    Role ownerRole

    static constraints = {
        importFrom(Panel, exclude: ['ownerRole', 'readyForExtraction', 'lastUpdated', 'dateCreated'])
        ownerRole(nullable: false, validator: { value, command, err ->
            /*We make it required in the command object even though it is optional in the domain.
         We will make it required in the domain as soon as we are done back populating the data*/
            //validate that the selected role is in the roles associated with the user
            if (!BardCommand.isRoleInUsersRoleList(value)) {
                err.rejectValue('ownerRole', "message.code", "You do not have the privileges to create Panels for this team : ${value.displayName}");
            }
        })
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
        panel.ownerRole = this.ownerRole
    }
}
