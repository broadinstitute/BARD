package bard.db.registration

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
		if(params?.term && params?.section){
			List<Descriptor> descriptors = ontologyDataAccessService.getAttributeDescriptors(params.section, params.term)
            descriptors = descriptors.findAll{it.elementStatus != ElementStatus.Retired}
            Set<Element> uniqueElements = descriptors.collect{ it.element } as Set
			if(uniqueElements){
				List attributes = new ArrayList();
				for (Element element in uniqueElements) {
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
	}

	def getValueDescriptors(){
		if(params?.term && params?.section && params?.attributeId){
			List<Descriptor> descriptors = ontologyDataAccessService.getValueDescriptors(params.attributeId.toLong(), params.section, params.term)

            Set<Element> uniqueElements = descriptors.collect{ it.element } as Set
			if(uniqueElements){
				List attributes = new ArrayList();
				for (Element element in uniqueElements) {
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
	}

}
