package bard.db.registration

import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.dictionary.OntologyDataAccessService
import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyException
import grails.converters.JSON
import grails.plugin.cache.Cacheable
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

    def getElement(Long id) {
        Element element = Element.get(id)

        render asMapForSelect2(element) as JSON
    }
    /**
     * @return List of elements to be used as attributes for ContextItems
     */
    def getDescriptors() {
        if (params?.term) {
            List<Element> elements = ontologyDataAccessService.getElementsForAttributes(params.term)
            List<Map> attributes = elements.collect { Element element ->
                asMapForSelect2(element)
            }
            Map map = ['results': attributes]
            render map as JSON
        }
    }
    /**
     * @return List of elements to be used as attributes for ContextItems
     */
    @Cacheable("contextItemAttributeDescriptors")
    def getAttributeDescriptors() {

        List<Descriptor> descriptors = ontologyDataAccessService.getDescriptorsForAttributes()
        List<Map> attributes = descriptors.collect { Descriptor descriptor ->
            asMapForSelect2(descriptor)
        }
        Map map = ['results': attributes]
        render map as JSON

    }

    private Map asMapForSelect2(Element element) {
        boolean hasIntegratedSearch = false;
        if (StringUtils.isNotBlank(element.externalURL)) {
            hasIntegratedSearch = true//ontologyDataAccessService.externalOntologyHasIntegratedSearch(element.externalURL)
        }
        [
                "id": element.id,
                "text": element.label,
                "unitId": element.unit?.id,
                "expectedValueType": element.expectedValueType.name(),
                "externalUrl": element.externalURL,
                "hasIntegratedSearch": hasIntegratedSearch
        ]
    }

    private Map asMapForSelect2(Descriptor descriptor) {
        boolean hasIntegratedSearch = false;
        if (StringUtils.isNotBlank(descriptor.externalURL)) {
            hasIntegratedSearch = true // ontologyDataAccessService.externalOntologyHasIntegratedSearch(descriptor.externalURL)
        }
        Map map = asMapForSelect2(descriptor.element)
        map.put('fullPath', descriptor.fullPath)
        return map
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

    @Cacheable("contextItemValueDescriptors")
    def getValueDescriptorsV2(Long attributeId) {
        List<Descriptor> descriptors = ontologyDataAccessService.getDescriptorsForValues(attributeId)
        List<Map> values = descriptors.collect{   Descriptor descriptor->
            asMapForSelect2(descriptor)
        }
        Map map = ['results': values]
        render map as JSON
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

    def getUnits(Long toUnitId) {
        List<Element> units = []
        if (toUnitId) {
            units = ontologyDataAccessService.getConvertibleUnits(params.toUnitId.toLong())
        } else {
            units = ontologyDataAccessService.getAllUnits()
        }
        render createIdLabelList(units) as JSON
    }

    private List createIdLabelList(List<Element> units) {
        List idLabelList = units.collect { Element unit ->
            [value: unit.id, label: [unit.abbreviation, unit.label].findAll().join(' - ')]
        }
        idLabelList
    }

    def findExternalItemById(Long elementId, String id) {
        id = StringUtils.trimToNull(id)
        Element element = Element.get(elementId)
        final String externalUrl = element?.externalURL
        Map responseMap = [:]
        if (id && externalUrl) {
            try {
                ExternalItem externalItem = ontologyDataAccessService.findExternalItemById(externalUrl, id)
                responseMap = toMapForSelect2(externalItem)
            }
            catch (ExternalOntologyException e) {
                responseMap.error = e.message
            }
        }
        render responseMap as JSON
    }

    def findExternalItemsByTerm(Long elementId, String term) {
        term = StringUtils.trimToNull(term)
        Element element = Element.get(elementId)
        final String externalUrl = element?.externalURL
        Map responseMap = ['externalItems': []]
        if (term && externalUrl) {
            try {
                final List<ExternalItem> foundItems = ontologyDataAccessService.findExternalItemsByTerm(externalUrl, term)
                responseMap.externalItems.addAll(foundItems.collect { ExternalItem item -> toMapForSelect2(item) })
            }
            catch (ExternalOntologyException e) {
                responseMap.error = e.message
            }
        }
        println(responseMap)
        render responseMap as JSON
    }

    private Map toMapForSelect2(ExternalItem item) {
        ['id': item.id, 'text': "(${item.id}) ${item.display}", 'display': item.display]
    }

}
