/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package bard.db.registration

import bard.db.dictionary.BardDescriptor
import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.dictionary.ElementStatus
import bard.db.dictionary.OntologyDataAccessService
import bard.db.enums.ExpectedValueType
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
    @Cacheable(value = "contextItemAttributeDescriptors")
    def getStatsModifierDescriptors(String startOfFullPath) {
        final List<Descriptor> descriptors = ontologyDataAccessService.getDescriptors(startOfFullPath, null)
        final List<Map> attributes = descriptors.collect { Descriptor descriptor ->
            asMapForSelect2(descriptor, false)
        }
        final Map map = ['results': attributes]
        render map as JSON
    }
    /**
     * @return List of elements to be used as attributes for ContextItems
     *
     * TODO: Change stats modifier to also use this
     */
    @Cacheable(value = "contextItemAttributeDescriptors")
    def getAttributeDescriptorsNoExpectedValueType(String startOfFullPath) {
        final List<Descriptor> descriptors = ontologyDataAccessService.getDescriptors(startOfFullPath, null)
        final List<Map> attributes = descriptors.collect { Descriptor descriptor ->
            asMapForSelect2(descriptor, false)
        }
        final Map map = ['results': attributes]
        render map as JSON
    }
    /**
     * @return List of elements to be used as attributes for ContextItems
     *
     * note: this cache will need to be cleared if the element hierarchy is changed or a new element is added
     */
    @Cacheable(value = "contextItemAttributeDescriptors")
    def getAttributeDescriptors(String startOfFullPath) {
        final List<Descriptor> descriptors = ontologyDataAccessService.getDescriptorsForAttributes(startOfFullPath)
        final List<Map> attributes = descriptors.collect { Descriptor descriptor ->
            asMapForSelect2(descriptor, true)
        }
        final Map map = ['results': attributes]
        render map as JSON
    }

    /**
     *
     * @param attributeId
     * @return all the eligible descriptors that can be used for values based on the attributeId
     *
     * note: this cache will need to be cleared if the element hierarchy is changed or a new element is added
     */
    @Cacheable(value = "contextItemValueDescriptors")
    def getValueDescriptorsV2(Long attributeId) {
        final List<Descriptor> descriptors = ontologyDataAccessService.getDescriptorsForValues(attributeId)
        final List<Map> values = descriptors.collect { Descriptor descriptor ->
            asMapForSelect2(descriptor, false)
        }
        final Map map = ['results': values]
        render map as JSON
    }

    private Map asMapForSelect2(Element element) {
        boolean hasIntegratedSearch = false;
        if (StringUtils.isNotBlank(element.externalURL)) {
            hasIntegratedSearch = ontologyDataAccessService.externalOntologyHasIntegratedSearch(element.externalURL)
        }
        [
                "id": element.id,
                "text": element.label,
                "unitId": element.unit?.id,
                "expectedValueType": element.expectedValueType.name(),
                "externalUrl": element.externalURL,
                "hasIntegratedSearch": hasIntegratedSearch,
                "description": element.description,
                "addChildMethod": element.addChildMethod.name()
        ]
    }

    private Map asMapForSelect2(Descriptor descriptor, boolean removeIdForExpectedValueTypeNone) {
        Map map = asMapForSelect2(descriptor.element)
        if (removeIdForExpectedValueTypeNone && descriptor.element.expectedValueType == ExpectedValueType.NONE) {
            map.remove('id')
        }
        map.put('fullPath', descriptor.fullPath.replace('BARD> ', ''))
        map.put('parentFullPath', descriptor.parent?.fullPath?.replace('BARD> ', ''))
        return map
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
                responseMap.error = "Integrated lookup for the External Ontology Id: ${id} was not successful at this time."
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
                responseMap.error = "Integrated lookup for attribute ${element.label} with the term: ${term} was not successful at this time.  Please try again or, enter the External Ontology Id and Display Value directly."
            }
        }
        render responseMap as JSON
    }

    private Map toMapForSelect2(ExternalItem item) {
        Map map = [:]
        if (item) {
            map = ['id': item.id, 'text': "(${item.id}) ${item.display}", 'display': item.display]
        }
        map
    }

}
