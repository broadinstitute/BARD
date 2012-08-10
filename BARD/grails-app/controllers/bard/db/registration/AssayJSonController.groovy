package bard.db.registration

import grails.converters.JSON

class AssayJSonController {
	
	CardFactoryService cardFactoryService

    def index() {
		redirect(action: "getNames", params: params)
	}
	
	def getNames() {
		def results
		if(params?.term)
			results = Assay.findAllByAssayNameIlike("%${params.term}%")
		else
			results = Assay.list()	
					
		render(contentType: "text/json") {
			if(results){
				for (a in results) {
					element a.assayName
				}
			}
			else
				element ""
		}
	}
	
	def getCards(){
		def assayInstance = Assay.get(params.id)
		List<CardDto> cardDtoList = new ArrayList<CardDto>()
		if(assayInstance?.id)
			cardDtoList = cardFactoryService.createCardDtoListForAssay(assayInstance)
		render cardDtoList as JSON
	}

}
