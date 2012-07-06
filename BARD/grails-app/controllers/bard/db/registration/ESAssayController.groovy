package bard.db.registration

import bard.services.ElasticSearchService

class ESAssayController {
	
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	ElasticSearchService elasticSearchService

    def index() { }
	
	def show() {
		
		def assayJson = elasticSearchService.searchForAssay(params.id as Integer)
		def assayInstance = assayJson._source
		
		if (!assayInstance) {
		flash.message = message(code: 'default.not.found.message', args: [message(code: 'assay.label', default: 'Assay'), params.id])
			return
		}
		else
			flash.message = null
					
			[assayInstance: assayInstance]
		}
}
