package bard.db.registration

import bard.services.ElasticSearchService


class AssayDefinitionController {
	
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    ElasticSearchService elasticSearchService

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
		render(view: "description", model: [assayInstance: assayInstance])
//		redirect(action: "show", id: assayInstance.id)
	}

	def show() {

        def assayJson = elasticSearchService.searchForAssay(params.id as Integer)
        //def assayInstance = Assay.get(params.id)
        def assayInstance = assayJson._source

        if (!assayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
			return
		}
		else
			flash.message = null

		[assayInstance: assayInstance]
	}
	
	def findById(){
		if(params.assayId && params.assayId.isLong()){
			def assayInstance = Assay.findById(params.assayId.toLong())
			log.debug "Find assay by id"
			if(assayInstance?.id)
				redirect(action: "show", id: assayInstance.id)
			else
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.assayId])
		}					
	}
	
	def findByName() {
		if(params.assayName){
			def assays = Assay.findAllByAssayNameIlike("%${params.assayName}%")
			if(assays?.size() != 0){
				if(assays.size() > 1)
					render(view: "findByName", params: params, model: [assays: assays])
				else
					redirect(action: "show", id: assays.get(0).id)
			}
			else
				flash.message = message(code: 'default.not.found.property.message', args: [message(code: 'assay.label', default: 'Assay'), "name", params.assayName])
				
		}
	}

}
