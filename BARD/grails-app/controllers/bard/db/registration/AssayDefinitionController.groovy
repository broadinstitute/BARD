package bard.db.registration

import bard.db.ContextService
import bard.db.dictionary.Element
import bard.db.enums.AssayStatus
import bard.db.enums.AssayType
import bard.db.enums.HierarchyType
import bard.db.project.InlineEditableCommand
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService
import grails.validation.ValidationException
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.web.json.JSONArray

import javax.servlet.http.HttpServletResponse
import java.text.DateFormat
import java.text.SimpleDateFormat

@Mixin(EditingHelper)
@Secured(['isAuthenticated()'])
class AssayDefinitionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", associateContext: "POST", disassociateContext: "POST", deleteMeasure: "POST", addMeasure: "POST"]

    AssayContextService assayContextService
    SpringSecurityService springSecurityService
    MeasureTreeService measureTreeService
    AssayDefinitionService assayDefinitionService
    ContextService contextService

    def editAssayType(InlineEditableCommand inlineEditableCommand) {
        try {
            final AssayType assayType = AssayType.byId(inlineEditableCommand.value)
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }
            assay = assayDefinitionService.updateAssayType(inlineEditableCommand.pk, assayType)
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.assayShortName, assay.lastUpdated, assay.assayType.id)
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editAssayStatus(InlineEditableCommand inlineEditableCommand) {
        try {
            final AssayStatus assayStatus = AssayStatus.byId(inlineEditableCommand.value)
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }
            assay = assayDefinitionService.updateAssayStatus(inlineEditableCommand.pk, assayStatus)
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.assayShortName, assay.lastUpdated, assay.assayStatus.id)

        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editAssayName(InlineEditableCommand inlineEditableCommand) {
        try {
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }
            assay = assayDefinitionService.updateAssayName(inlineEditableCommand.pk, inlineEditableCommand.value)
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.assayShortName, assay.lastUpdated, assay.assayName)
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def editDesignedBy(InlineEditableCommand inlineEditableCommand) {
        try {
            Assay assay = Assay.findById(inlineEditableCommand.pk)
            final String message = inlineEditableCommand.validateVersions(assay.version, Assay.class)
            if (message) {
                conflictMessage(message)
                return
            }
            assay = assayDefinitionService.updateDesignedBy(inlineEditableCommand.pk, inlineEditableCommand.value)
            generateAndRenderJSONResponse(assay.version, assay.modifiedBy, assay.assayShortName, assay.lastUpdated, assay.designedBy)
        } catch (Exception ee) {
            log.error(ee)
            editErrorMessage()
        }
    }

    def assayStatus() {
        List<String> sorted = []
        final Collection<AssayStatus> assayStatuses = AssayStatus.values()
        for (AssayStatus assayStatus : assayStatuses) {
            sorted.add(assayStatus.id)
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null

    }

    def assayTypes() {
        List<String> sorted = []
        final Collection<AssayType> assayTypes = AssayType.values()
        for (AssayType assayType : assayTypes) {
            sorted.add(assayType.id)
        }
        sorted.sort()
        final JSON json = sorted as JSON
        render text: json, contentType: 'text/json', template: null
    }

    def index() {
        redirect(action: "description", params: params)
    }

    def description() {
        [assayInstance: new Assay(params)]
    }

    def cloneAssay(Long id) {
        Assay assay = Assay.get(id)
        try {
            assay = assayDefinitionService.cloneAssayForEditing(assay, springSecurityService.principal?.username)
            assay = assayDefinitionService.recomputeAssayShortName(assay)
        } catch (ValidationException ee) {
            assay = Assay.get(id)
            flash.message = "Cannot clone assay definition with id \"${id}\" probably because of data migration issues. Please email the BARD team at bard-users@broadinstitute.org to fix this assay"

        }
        JSON measureTreeAsJson = new JSON(measureTreeService.createMeasureTree(assay, false))
        render(view: "show", model: [assayInstance: assay, measureTreeAsJson: measureTreeAsJson])
    }

    def save() {

        def assayInstance = new Assay(params)
        Assay savedAssay = assayDefinitionService.saveNewAssay(assayInstance)
        if (!savedAssay) {
            render(view: "description", model: [assayInstance: assayInstance])
            return
        }
        flash.message = message(code: 'default.created.message', args: [message(code: 'assay.label', default: 'Assay'), savedAssay.id])
        redirect(action: "show", id: savedAssay.id)
    }

    def show() {
        def assayInstance = Assay.get(params.id)
        JSON measureTreeAsJson = null

        if (!assayInstance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        } else {
            flash.message = null
            measureTreeAsJson = new JSON(measureTreeService.createMeasureTree(assayInstance, false))
        }

        [assayInstance: assayInstance, measureTreeAsJson: measureTreeAsJson]
    }

    def editContext() {
        def assayInstance = Assay.get(params.id)

        if (!assayInstance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        }

        [assayInstance: assayInstance]
    }

    def editMeasure() {
        JSON measuresTreeAsJson = null;

        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
            // FIXME:  Should not use flash if we do not redirect afterwards
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        } else {
            measuresTreeAsJson = new JSON(measureTreeService.createMeasureTree(assayInstance, false));
        }

        [assayInstance: assayInstance, measuresTreeAsJson: measuresTreeAsJson]
    }

    def deleteMeasure() {
        def measure = Measure.get(params.measureId)

        if (measure.childMeasures.size() != 0) {
            flash.message = "Cannot delete measure \"${measure.displayLabel}\" because it has children"
        } else if (measure.experimentMeasures.size() != 0) {
            flash.message = "Cannot delete measure \"${measure.displayLabel}\" because it is used in an experiment definition"
        } else {
            measure.delete()
        }

        redirect(action: "editMeasure", id: params.id)
    }

    def addMeasure() {
        final Assay assayInstance = Assay.get(params.id)
        final Element resultType = Element.get(params.resultTypeId)
        final String parentChildRelationship = params.relationship
        HierarchyType hierarchyType = null

        if (!resultType) {
            render status: HttpServletResponse.SC_BAD_REQUEST, text: 'Result Type is Required!'
        } else {
            def parentMeasure = null
            if (params.parentMeasureId) {
                parentMeasure = Measure.get(params.parentMeasureId)
            }
            //if there is a parent measure then there must be a selected relationship
            if (parentMeasure && (StringUtils.isBlank(parentChildRelationship) || "null".equals(parentChildRelationship))) {
                render status: HttpServletResponse.SC_BAD_REQUEST, text: 'Relationship to Parent is required!'
                return
            } else {
                if (StringUtils.isNotBlank(parentChildRelationship)) {
                    hierarchyType = HierarchyType.byId(parentChildRelationship.trim())
                }
                def statsModifier = null
                if (params.statisticId) {
                    statsModifier = Element.get(params.statisticId)
                }


                def entryUnit = null
                if (params.entryUnitName) {
                    entryUnit = Element.findByLabel(params.entryUnitName)
                }

                Measure newMeasure = assayContextService.addMeasure(assayInstance, parentMeasure, resultType, statsModifier, entryUnit, hierarchyType)
                render status: HttpServletResponse.SC_OK, text: "Successfully added measure " + newMeasure.displayLabel

            }
        }
    }

    def disassociateContext() {
        def measure = Measure.get(params.measureId)
        def context = AssayContext.get(params.assayContextId)

        if (measure == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'measure.label', default: 'Measure'), params.id])
        } else if (context == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayContext.label', default: 'AssayContext'), params.id])
        } else {
            flash.message = null
            assayContextService.disassociateContext(measure, context)
        }

        redirect(action: "editMeasure", id: context.assay.id)
    }

    def associateContext() {
        def measure = Measure.get(params.measureId)
        def context = null
        if (params.assayContextId && 'null' != params.assayContextId) {
            context = AssayContext.get(params.assayContextId)
        }

        if (measure == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'measure.label', default: 'Measure'), params.id])
        } else if (context == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayContext.label', default: 'AssayContext'), params.assayContextId])
        } else {

            assayContextService.associateContext(measure, context)
            flash.message = "Measure '${measure?.displayLabel}' successfully associated to Context '${context?.contextName}'"
        }

        redirect(action: "editMeasure", id: params.id)
    }

    def changeRelationship() {
        def measure = Measure.get(params.measureId)
        def parentChildRelationship = params.relationship

        HierarchyType hierarchyType = null
        if (StringUtils.isNotBlank(parentChildRelationship)) {
            hierarchyType = HierarchyType.byId(parentChildRelationship.trim())
        }
        if (measure == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'measure.label', default: 'Measure'), params.id])
        } else {
            flash.message = null
            if (measure.parentMeasure) { //if this measure has no parent then do nothing
                assayContextService.changeParentChildRelationship(measure, hierarchyType)
            }
        }

        redirect(action: "editMeasure", id: params.id)
    }

    def findById() {
        if (params.assayId && params.assayId.isLong()) {
            def assayInstance = Assay.findById(params.assayId.toLong())
            log.debug "Find assay by id"
            if (assayInstance?.id)
                redirect(action: "show", id: assayInstance.id)
            else
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.assayId])
        }
    }

    def findByName() {
        if (params.assayName) {
            def assays = Assay.findAllByAssayNameIlikeOrAssayShortNameIlike("%${params.assayName}%", "%${params.assayName}%")
            if (assays?.size() > 1) {
                if (params.sort == null) {
                    params.sort = "id"
                }
                assays.sort {
                    a, b ->
                        if (params.order == 'desc') {
                            b."${params.sort}" <=> a."${params.sort}"
                        } else {
                            a."${params.sort}" <=> b."${params.sort}"
                        }
                }
                render(view: "findByName", params: params, model: [assays: assays])
            } else if (assays?.size() == 1)
                redirect(action: "show", id: assays.get(0).id)
            else
                flash.message = message(code: 'default.not.found.property.message', args: [message(code: 'assay.label', default: 'Assay'), "name", params.assayName])
        }
    }

    def reloadCardHolder(Long assayId) {
        def assay = Assay.get(assayId)
        if (assay) {
            render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
        } else {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        }
    }

    def launchEditItemInCard(Long assayContextId, Long assayContextItemId) {
        def assayContextItem = AssayContextItem.get(assayContextItemId)
        render(template: "editItemForm", model: [assayContextItem: assayContextItem, assayContextId: assayContextId])
    }

    def updateCardName(String edit_card_name, Long contextId) {
        AssayContext assayContext = assayContextService.updateCardName(contextId, edit_card_name)
        Assay assay = assayContext.assay
        render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
    }
    /**
     *
     * @param context_group - The new context group
     * @param contextMoveId - The context that we are moving to new group
     *
     */
    def moveCard(String context_group, Long contextMoveId) {
        AssayContext assayContext = AssayContext.findById(contextMoveId)
        if (assayContext.contextGroup != context_group) {
            assayContext.setContextGroup(context_group)
        }
        Assay assay = assayContext.assay
        render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
    }

    def showMoveItemForm(Long assayId, Long itemId) {
        def assayInstance = Assay.get(assayId)
        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), assayId])
            return
        }
        render(template: "moveItemForm", model: [instance: assayInstance, assayId: assayId, itemId: itemId])
    }

    def moveCardItem(Long cardId, Long assayContextItemId, Long assayId) {
        AssayContext targetAssayContext = AssayContext.findById(cardId)
        AssayContextItem source = AssayContextItem.findById(assayContextItemId)
        assayContextService.addItem(source, targetAssayContext)
        Assay assay = targetAssayContext.assay
        render(template: "../context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
    }


    def showEditSummary(Long instanceId) {
        def assayInstance = Assay.findById(instanceId)
        render(template: "editSummary", model: [assay: assayInstance])
    }

    def moveMeasureNode(Long measureId, Long parentMeasureId) {
        Measure measure = Measure.get(measureId)
        Measure parentMeasure = null
        if (parentMeasureId) {
            parentMeasure = Measure.get(parentMeasureId)
        }
        measure.parentMeasure = parentMeasure
        if (!measure.parentChildRelationship) {
            if (parentMeasure) {
                measure.parentChildRelationship = HierarchyType.SUPPORTED_BY
            }
        }


        render new JSONArray()
    }
}
class EditingHelper {
    static final DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy")


    def generateAndRenderJSONResponse(Long currentVersion, String modifiedBy, String shortName, Date lastUpdated, final String newValue) {
        Map<String, String> dataMap = [:]
        dataMap.put('version', currentVersion.toString())
        dataMap.put('modifiedBy', modifiedBy)
        dataMap.put('lastUpdated', formatter.format(lastUpdated))
        if (shortName?.trim()) {
            dataMap.put("shortName", shortName)
        }
        dataMap.put("data", newValue)

        JSON jsonResponse = dataMap as JSON
        render status: HttpServletResponse.SC_OK, text: jsonResponse, contentType: 'text/json', template: null
    }

    def conflictMessage(String message) {
        render(status: HttpServletResponse.SC_CONFLICT, text: message, contentType: 'text/plain', template: null)
    }

    def editErrorMessage() {
        render(status: HttpServletResponse.SC_INTERNAL_SERVER_ERROR, text: message(code: 'editing.error.message'), contentType: 'text/plain', template: null)
    }

}