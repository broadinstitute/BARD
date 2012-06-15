package bard.db.registration


class AssayDefinitionController {
	
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

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
		
		def assayInstance = Assay.get(params.id)
		if (!assayInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
			return
		}
		else
			flash.message = null

		[assayInstance: assayInstance]
	}

}
