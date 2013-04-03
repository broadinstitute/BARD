package bard.db.registration

import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyException
import grails.converters.JSON
import org.apache.commons.lang.StringUtils

class OntologyJSonController {

    OntologyDataAccessService ontologyDataAccessService

    def index() {
        redirect(action: "getAssayContextItems", params: params)
    }

    def getAssayContextItems() {
        if (params.assayContextId && params.assayContextId.isLong()) {
            List<AssayContextItem> assayContextItems = ontologyDataAccessService.getElementsInTree(params.assayContextId.toLong(), "test")
            render(contentType: "text/json") {
                if (assayContextItems) {
                    for (aci in assayContextItems) {
                        element aci.attributeElement.ontologyBreadcrumb.preferedDescriptor.label
                    }
                }
            }
        }
    }

    def getLabelsFromTree() {
        List elements = ontologyDataAccessService.getElementsFromTree(params.tree, params.label)
        List results = elements.collect { [label: it.label, elementId: it.elementId] }
        render results as JSON
    }

    def getDescriptors() {
        if (params?.term) {
            List<Element> elements = ontologyDataAccessService.getElementsForAttributes(params.term)
            List attributes = new ArrayList();
            for (Element element in elements) {
                def item = [
                        "label": element.label,
                        "value": element.label,
                        "elementId": element.id,
                        "unitId": element.unit?.id
                ]
                attributes.add(item)
            }
            render attributes as JSON
        }
    }

    def getValueDescriptors() {
        if (params?.term && params?.attributeId) {
            List<Element> elements = ontologyDataAccessService.getElementsForValues(params.attributeId.toLong(), params.term)
            List attributes = new ArrayList()
            for (Element element in elements) {
                def unit = element?.unit?.abbreviation
                unit = unit ?: (element?.unit?.label ?: "")
                def item = [
                        "label": element.label,
                        "value": element.label,
                        "elementId": element.id,
                        "unit": unit
                ]
                attributes.add(item)
            }
            render attributes as JSON
        }
    }

    def getAllUnits() {
        List<Element> elements = ontologyDataAccessService.getAllUnits()
        render createIdLabelList(elements) as JSON
    }

    def getConvertibleUnits() {
        if (params?.elementId && params?.toUnitId) {
            List<Element> units = ontologyDataAccessService.getConvertibleUnits(params.toUnitId.toLong())
            render createIdLabelList(units) as JSON
        }
    }

    private List createIdLabelList(List<Element> units) {
        List idLabelList = units.collect { Element unit ->
            [value: unit.id, label: [unit.abbreviation, unit.label].findAll().join(' - ') ]
        }
        idLabelList
    }

    def findExternalItemsByTerm(Long elementId, String term) {
        term = StringUtils.trimToNull(term)
        Element element = Element.get(elementId)
        final String externalUrl = element?.externalURL
        List externalItems = []
        Map responseMap = ['externalItems': externalItems]
        if (term && externalUrl) {
            try {
                final List<ExternalItem> foundItems = ontologyDataAccessService.findExternalItemsByTerm(externalUrl, term)
                externalItems.addAll(foundItems.collect { ExternalItem item -> ['id': item.id, 'text': "(id:${item.id}) ${item.display}", 'display': item.display] })
            }
            catch (ExternalOntologyException e) {
                responseMap.error = e.message
            }
        }
        println(responseMap)
        render responseMap as JSON
    }

}
