package bard.db.registration

import java.util.List;

import bard.db.dictionary.*;
import bard.db.registration.*;
import bard.db.registration.additemwizard.*;
import grails.converters.JSON

class OntologyJSonController {

	OntologyDataAccessService ontologyDataAccessService

    def index() {
		redirect(action: "getAssayContextItems", params: params)
	}

	def getAssayContextItems(){
		if(params.assayContextId && params.assayContextId.isLong()){
			List<AssayContextItem> assayContextItems = ontologyDataAccessService.getElementsInTree(params.assayContextId.toLong(), "test")
			render(contentType: "text/json") {
				if(assayContextItems){
					for (aci in assayContextItems) {
						element aci.attributeElement.ontologyBreadcrumb.preferedDescriptor.label
					}
				}
			}
		}
	}

    def getLabelsFromTree() {
        List elements = ontologyDataAccessService.getElementsFromTree(params.tree, params.label)
        List results = elements.collect { [label: it.label, elementId: it.elementId]}
        render results as JSON
    }

	def getDescriptors(){
		if(params?.term){
			List<Element> elements = ontologyDataAccessService.getElementsForAttributes(params.term)
				List attributes = new ArrayList();
				for (Element element in elements) {
					def item = [
						"label" : element.label,
						"value" : element.label,
						"elementId" : element.id,
					]
					attributes.add(item)
				}
                render attributes as JSON
		}
	}

	def getValueDescriptors(){
		if(params?.term && params?.section && params?.attributeId){
            List<Element> elements = ontologyDataAccessService.getElementsForValues(params.attributeId.toLong(), params.term)
            List attributes = new ArrayList();
            for (Element element in elements) {
                def unit = element?.unit?.abbreviation
                unit = unit ?: (element?.unit?.label ?: "")
                def item = [
                    "label" : element.label,
                    "value" : element.label,
                    "elementId" : element.id,
                    "unit" : unit
                ]
                attributes.add(item)
            }
            render attributes as JSON
		}
	}

	def getBaseUnits(){
//		if(params?.elementId && params?.toUnitId){
//			List<UnitTree> units = ontologyDataAccessService.getBaseUnits(params.elementId.toLong(), params.toUnitId.toLong())
//			if(units){
//				render(contentType: "text/json") {
//					for (u in units) {
//						element u.label
//					}
//				}
//			}
//			else{
//				println "Units is null."
//				render(contentType: "text/json") {
//					element "null"
//				}
//			}
//		}
	}

}
