package bard.db.registration

import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class AssayDefinitionController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    CardFactoryService cardFactoryService
    AssayContextService assayContextService

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
//        render(view: "description", model: [assayInstance: assayInstance])
		redirect(action: "show", id: assayInstance.id)
    }

    def show() {
        def assayInstance = Assay.get(params.id)

        if (!assayInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
            return
        }
        else
            flash.message = null

        Map<String , CardDto> cardDtoMap = cardFactoryService.createCardDtoMapForAssay(assayInstance)

        [assayInstance: assayInstance, cardDtoMap: cardDtoMap]
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
            if (assays?.size() != 0) {
                if (assays.size() > 1)
                    render(view: "findByName", params: params, model: [assays: assays])
                else
                    redirect(action: "show", id: assays.get(0).id)
            }
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
        Map<String , CardDto> cardDtoMap = cardFactoryService.createCardDtoMapForAssay(targetAssayContext.assay)
        render(template: "cards", model: [cardDtoMap: cardDtoMap])
    }

    def addItemToCard(Long src_assay_context_item_id, Long target_assay_context_id) {
        AssayContext targetAssayContext = AssayContext.findById(target_assay_context_id)
        AssayContextItem source = AssayContextItem.findById(src_assay_context_item_id)
        assayContextService.addItem(source, targetAssayContext)
        Map<String , CardDto> cardDtoMap = cardFactoryService.createCardDtoMapForAssay(targetAssayContext.assay)
        render(template: "cards", model: [cardDtoMap: cardDtoMap])
    }


    def updateCardTitle(Long src_assay_context_item_id, Long target_assay_context_id) {
        AssayContextItem sourceAssayContextItem = AssayContextItem.findById(src_assay_context_item_id)
        AssayContext targetAssayContext = AssayContext.findById(target_assay_context_id)
        assayContextService.updateContextName(targetAssayContext, sourceAssayContextItem)
        CardDto cardDto = cardFactoryService.createCardDto(targetAssayContext)
        render(template: "cardDto", model: [card: cardDto])
    }


    def deleteItemFromCard(Long assay_context_item_id) {
        def assayContextItem = AssayContextItem.get(assay_context_item_id)
        if (assayContextItem) {
            AssayContext assayContext = assayContextService.deleteItem(assayContextItem)
            CardDto cardDto = cardFactoryService.createCardDto(assayContext)
            render(template: "cardDto", model: [card: cardDto])
        }
    }

    def deleteEmptyCard(Long assay_context_id) {
        AssayContext assayContext = AssayContext.findById(assay_context_id)
        Assay assay = assayContext.assay
        if (assayContext.assayContextItems.size() == 0 && assayContext.measures.empty) {
            assay.removeFromAssayContexts(assayContext)
            assayContext.delete()
        }
        Map<String , CardDto> cardDtoMap = cardFactoryService.createCardDtoMapForAssay(assay)
        render(template: "cards", model: [cardDtoMap: cardDtoMap])
    }

    def createOrEditCardName(String edit_card_name, Long assayId, Long assayContextId){
        AssayContext assayContext = assayContextService.createOrEditCardName(assayId, assayContextId, edit_card_name)
        Map<String , CardDto> cardDtoMap = cardFactoryService.createCardDtoMapForAssay(assayContext.assay)
        render(template: "cards", model: [cardDtoMap: cardDtoMap])
    }
	
	def showMoveItemForm(Long assayId, Long itemId){
		def assayInstance = Assay.get(assayId)
		if (!assayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), assayId])
            return
		}
		Map<String , CardDto> cardDtoMap = cardFactoryService.createCardDtoMapForAssay(assayInstance)
		render(template: "moveItemForm", model: [cardDtoMap: cardDtoMap, assayId: assayId, itemId: itemId])
	}
	
	def moveCardItem(Long cardId, Long assayContextItemId, Long assayId){
//		println "moveCardItem form params -> CardId: " + cardId + "	AssayContextItemId: " + assayContextItemId + "	assayId: " + assayId
		AssayContext targetAssayContext = AssayContext.findById(cardId)
		AssayContextItem source = AssayContextItem.findById(assayContextItemId)
		assayContextService.addItem(source, targetAssayContext)
		Map<String , CardDto> cardDtoMap = cardFactoryService.createCardDtoMapForAssay(targetAssayContext.assay)
		render(template: "cards", model: [cardDtoMap: cardDtoMap, assayId: assayId])
	}

}
