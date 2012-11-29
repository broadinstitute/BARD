package bard.db.registration

import bard.db.dictionary.*;
import grails.converters.JSON

class OntologyJSonController {
	
	OntologyDataAccessService ontologyDataAccessService

    def index() {
		redirect(action: "getAssayContextItems", params: params)
	}
	
	def getAssayContextItems(){
		if(params.assayContextId && params.assayContextId.isLong()){			
			List<AssayContextItem> assayContextItems = ontologyDataAccessService.getElementsInTree(params.assayContextId.toLong(), "test")
//			render(contentType: "text/json") {
//				if(assayContextItems){
//					for (aci in assayContextItems) {
//						element aci.attributeElement.label
//					}
//				}
//				else
//					element "null"
//			}
			render assayContextItems as JSON
		}
		else{
			render(contentType: "text/json") {
				element "error"
			}
		}
	}
}
