package bard.db.registration

import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class AssayDefinitionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST", associateContext: "POST", disassociateContext: "POST", deleteMeasure : "POST", addMeasure: "POST"]

    AssayContextService assayContextService
    OntologyDataAccessService ontologyDataAccessService;

    def index() {
        redirect(action: "description", params: params)
    }

    def description() {
        [assayInstance: new Assay(params)]
    }

    def save() {
        def assayInstance = new Assay(params)
        if (!assayInstance.save(flush: true)) {
            render(view: "description", model: [assayInstance: assayInstance])
            return
        }
        flash.message = message(code: 'default.created.message', args: [message(code: 'assay.label', default: 'Assay'), assayInstance.id])
        redirect(action: "show", id: assayInstance.id)
    }

    def show() {
        def assayInstance = Assay.get(params.id)

        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        } else {
            flash.message = null
        }
        [assayInstance: assayInstance]
    }

    def edit() {
        def assayInstance = Assay.get(params.id)

        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        } else {
            flash.message = null
        }
        [assayInstance: assayInstance]
    }

    def editMeasure() {
        // while not directly used in the rendering of this page, make sure the tree is cached before rendering the
        // edit page to ensure the autocomplete comes up quickly when the user tries.
        // Perhaps a better approach would be to simply ensure some loading indicator is more predominant when the autocomplete is running.
        ontologyDataAccessService.ensureTreeCached()

        def assayInstance = Assay.get(params.id)
        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        } else {
            flash.message = null
        }

        [assayInstance: assayInstance]
    }

    def deleteMeasure() {
        def measure = Measure.get(params.measureId)
        measure.delete()
        redirect(action: "editMeasure", id: params.id)
    }

    def addMeasure() {
        def assayInstance = Assay.get(params.id)
        def resultType = Element.get(params.resultTypeId)

        def parentMeasure = null
        if (params.parentMeasureId) {
            parentMeasure = Measure.get(params.parentMeasureId)
        }

        def statsModifier = null
        if (params.statisticId) {
            statsModifier = Element.get(params.statisticId)
        }

        def entryUnit = null
        if (params.entryUnitName) {
            entryUnit = Element.findByLabel(params.entryUnitName)
        }

        Measure newMeasure = assayContextService.addMeasure(assayInstance, parentMeasure, resultType, statsModifier, entryUnit)

        flash.message = "Successfully added measure " + newMeasure.displayLabel
        redirect(action: "editMeasure", id: params.id)
    }

    def disassociateContext() {
        def measure = Measure.get(params.measureId)
        def context = AssayContext.get(params.assayContextId)

        if(measure == null) {
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
        def context = AssayContext.get(params.assayContextId)

        if(measure == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'measure.label', default: 'Measure'), params.id])
        } else if (context == null) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assayContext.label', default: 'AssayContext'), params.id])
        } else {
            flash.message = null
            assayContextService.associateContext(measure, context)
        }

        redirect(action: "editMeasure", id: context.assay.id)
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
            def assays = Assay.findAllByAssayNameIlike("%${params.assayName}%")
            if (assays?.size() > 1){
                if (params.sort == null) {
                    params.sort = "id"
                }
                assays.sort{
                    a, b->
                    if (params.order == 'desc') {
                        b."${params.sort}" <=> a."${params.sort}"
                    } else {
                        a."${params.sort}" <=> b."${params.sort}"
                    }
                }
                render(view: "findByName", params: params, model: [assays: assays])
            }
            else if (assays?.size() == 1)
                redirect(action: "show", id: assays.get(0).id)
            else
                flash.message = message(code: 'default.not.found.property.message', args: [message(code: 'assay.label', default: 'Assay'), "name", params.assayName])
        }
    }

    def addItemToCardAfterItem(Long src_assay_context_item_id, Long target_assay_context_item_id) {
        AssayContextItem target = AssayContextItem.findById(target_assay_context_item_id)
        AssayContextItem source = AssayContextItem.findById(src_assay_context_item_id)
        AssayContext targetAssayContext = target.assayContext
        int index = targetAssayContext.assayContextItems.indexOf(target)
        assayContextService.addItem(index, source, targetAssayContext)
        Assay assay = targetAssayContext.assay
        render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
    }

    def addItemToCard(Long src_assay_context_item_id, Long target_assay_context_id) {
        AssayContext targetAssayContext = AssayContext.findById(target_assay_context_id)
        AssayContextItem source = AssayContextItem.findById(src_assay_context_item_id)
        assayContextService.addItem(source, targetAssayContext)
        Assay assay = targetAssayContext.assay
        render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
    }
	
	def reloadCardHolder(Long assayId){
		def assay = Assay.get(assayId)
		if (assay) {
			render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
		} else {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
			return
		}
	}


    def updateCardTitle(Long src_assay_context_item_id, Long target_assay_context_id) {
        AssayContextItem sourceAssayContextItem = AssayContextItem.findById(src_assay_context_item_id)
        AssayContext targetAssayContext = AssayContext.findById(target_assay_context_id)
        assayContextService.updateContextName(targetAssayContext, sourceAssayContextItem)
        render(template: "/context/list", model: [contextOwner: targetAssayContext, contexts: targetAssayContext.groupContexts(), subTemplate: 'edit'])
    }


    def deleteItemFromCard(Long assay_context_item_id) {
        def assayContextItem = AssayContextItem.get(assay_context_item_id)
        if (assayContextItem) {
            AssayContext assayContext = assayContextService.deleteItem(assayContextItem)
            Assay assay = assayContext.assay
            render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
        }
    }

    def deleteEmptyCard(Long assay_context_id) {
        AssayContext assayContext = AssayContext.findById(assay_context_id)
        Assay assay = assayContext.assay
        if (assayContext.assayContextItems.size() == 0) {
            assay.removeFromAssayContexts(assayContext)
            assayContext.delete(flush: true)
        }
        render(template: "/context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
    }

    def createOrEditCardName(String edit_card_name, Long instanceId, Long contextId) {
        AssayContext assayContext = assayContextService.createOrEditCardName(instanceId, contextId, edit_card_name)
        Assay assay = assayContext.assay
        render(template: "../context/list", model: [contextOwner: assay, contexts: assay.groupContexts(), subTemplate: 'edit'])
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


}
