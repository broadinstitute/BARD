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
			println "# of AssayContextitems: " + assayContextItems.size()
			render(contentType: "text/json") {
				if(assayContextItems){
					for (aci in assayContextItems) {
						element aci.attributeElement.ontologyBreadcrumb.preferedDescriptor.label
					}
				}
				else
					element "null"
			}
//			render assayContextItems as JSON
		}
		else{
			render(contentType: "text/json") {
				element "error"
			}
		}
	}
	
	def getDescriptors(){
		if(params?.term && params?.section){
			println "params.term: " + params.term
			println "params.section: " + params.section
			List<Descriptor> descriptors = ontologyDataAccessService.getAttributeDescriptors(params.section, params.term)
			println "# of Attribute Descriptors: " + descriptors.size()
			if(descriptors){
				List attributes = new ArrayList();
				for (d in descriptors) {
					def item = [
						"label" : d.label,
						"value" : d.label,
						"elementId" : d.element.id,
//						"fpath" : d.fullPath
					]
					attributes.add(item)
				}
				render attributes as JSON
			}
			else{
				render(contentType: "text/json") {				
					element "null"
				}
			}
		}
	}
	
	def getValueDescriptors(){
		if(params?.term && params?.section && params?.attributeId){
			println "params.term: " + params.term
			println "params.section: " + params.section
			println "params.attributeId: " + params.attributeId
			Long eid = params.attributeId.toLong()
			List<Descriptor> descriptors = ontologyDataAccessService.getValueDescriptors(params.attributeId.toLong(), params.section, params.term)
			
			if(descriptors){
				println "# of Value Descriptors: " + descriptors.size()
				List attributes = new ArrayList();
				for (d in descriptors) {
					def unit = d?.unit?.abbreviation
					unit = unit ?: (d?.unit?.label ?: "")
					def item = [
						"label" : d.label,
						"value" : d.label,
						"elementId" : d.element.id,
						"unit" : unit
					]
					attributes.add(item)
				}
				render attributes as JSON
			}
			else{
				println "Descriptor is null. Value: " + descriptors
				render(contentType: "text/json") {
					element "null"
				}
			}
		}
	}
	
	def saveItemInCard(AttributeCommand attributeCmd, ValueTypeCommand valueTypeCmd, FixedValueCommand fixedValueCmd){
		if(valueTypeCmd?.valueTypeOption?.equals(AttributeType.Fixed)){
			
		}
	}
}
