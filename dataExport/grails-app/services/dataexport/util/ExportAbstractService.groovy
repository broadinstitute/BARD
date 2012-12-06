package dataexport.util

import bard.db.dictionary.Element
import bard.db.model.AbstractContextItem
import bard.db.model.AbstractDocument
import bard.db.registration.ExternalReference
import dataexport.registration.MediaTypesDTO
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class ExportAbstractService {


    public void generateExternalReferencesLink(final MarkupBuilder markupBuilder,
                                               final List<ExternalReference> externalReferences,
                                               final LinkGenerator grailsLinkGenerator,
                                               final MediaTypesDTO mediaTypesDTO) {
        for (ExternalReference externalReference : externalReferences) {

            generateLink(
                    [
                            mapping: 'externalReference',
                            absolute: true, rel: 'related',
                            mediaType: mediaTypesDTO.externalReferenceMediaType,
                            params: [id: externalReference.id]
                    ],
                    markupBuilder, grailsLinkGenerator)
        }
    }
// TODO look into why this code is here
    protected String generateHref(final Map map, final LinkGenerator grailsLinkGenerator) {
        final String mappingVal = map.get("mapping")
        final Map parameters = (Map) map.get("params")
        Long idVal = null
        Map paramMap = [:]
        if (parameters) {
            idVal = (Long) parameters.get("id")
            if (parameters.get("offset") > -1) {
                paramMap.put("offset", parameters.get("offset"))
            }
        }
        if (idVal) {
            paramMap.put("id", idVal)
        }
        if (paramMap.isEmpty()) {
            return grailsLinkGenerator.link(mapping: mappingVal, absolute: true).toString()
        }
        return grailsLinkGenerator.link(mapping: mappingVal, absolute: true, params: paramMap).toString()


    }

    public String generateLink(final Map map, final MarkupBuilder markupBuilder, final LinkGenerator grailsLinkGenerator) {
        generateHref(map, grailsLinkGenerator)
        String relVal = map.get("rel") ?: 'related'
        String mediaType = map.get("mediaType")

        String currentHref = generateHref(map, grailsLinkGenerator)
        markupBuilder.link(rel: relVal, href: currentHref, type: mediaType)
    }
    /**
     *
     * @param grailsLinkGenerator
     * @param markupBuilder
     * @param abstractDocument
     * @param resourceName
     * @param relatedResourceName
     * @param entityId
     * @param relatedEntityId
     * @param mediaType
     * @param relatedMediaType
     */
    public void generateDocument(
            final LinkGenerator grailsLinkGenerator,
            final MarkupBuilder markupBuilder,
            final AbstractDocument abstractDocument,
            final String resourceName, final String relatedResourceName,
            final Long entityId,
            final Long relatedEntityId,
            final String mediaType, final String relatedMediaType) {


        markupBuilder."${resourceName}"(documentType: abstractDocument.documentType) {
            if (abstractDocument.documentName) {
                documentName(abstractDocument.documentName)
            }
            if (abstractDocument.documentContent) {
                documentContent(abstractDocument.documentContent)
            }
            generateLink(
                    [
                            mapping: resourceName,
                            absolute: true, rel: 'self',
                            mediaType: mediaType,
                            params: [id: entityId]
                    ],
                    markupBuilder, grailsLinkGenerator)

            generateLink(
                    [
                            mapping: relatedResourceName,
                            absolute: true, rel: 'related',
                            mediaType: relatedMediaType,
                            params: [id: relatedEntityId]
                    ],
                    markupBuilder, grailsLinkGenerator)
        }
    }

    protected Map<String, String> createAttributesForContextItem(AbstractContextItem contextItem, String attributeType, Long contextItemId, String resourceName, int displayOrder) {
        final Map<String, String> attributes = [:]
        final String idName = resourceName + "Id"
        attributes.put(idName, contextItemId.toString())
        attributes.put('displayOrder', displayOrder.toString())
        if(attributeType){
            attributes.put('attributeType',attributeType)
        }
        if (contextItem.qualifier) {
            attributes.put('qualifier', contextItem.qualifier)
        }
        if (contextItem.valueDisplay) {
            attributes.put('valueDisplay', contextItem.valueDisplay)
        }
        if (contextItem.valueNum || contextItem.valueNum.toString().isInteger()) {
            attributes.put('valueNum', contextItem.valueNum.toString())
        }
        if (contextItem.valueMin || contextItem.valueMin.toString().isInteger()) {
            attributes.put('valueMin', contextItem.valueMin.toString())
        }
        if (contextItem.valueMax || contextItem.valueMax.toString().isInteger()) {
            attributes.put('valueMax', contextItem.valueMax.toString())
        }

        return attributes;
    }

    protected void generateContextItem(final MarkupBuilder markupBuilder,
                                       final AbstractContextItem contextItem,
                                       final String attributeType,
                                       final String resourceName,
                                       final String elementMediaType,
                                       final LinkGenerator linkGenerator,
                                       final Long contextItemId,
                                       final int displayOrder) {
        final Map<String, String> attributes = createAttributesForContextItem(contextItem, attributeType, contextItemId, resourceName, displayOrder)

        markupBuilder."${resourceName}"(attributes) {
            final Element valueElement = contextItem.valueElement
            final Element attributeElement = contextItem.attributeElement

            //add value id element
            if (valueElement) {
                valueId(label: valueElement.label) {

                    generateLink(
                            [
                                    mapping: 'element',
                                    absolute: true,
                                    rel: 'related',
                                    mediaType: elementMediaType,
                                    params: [id: valueElement.id]
                            ],
                            markupBuilder, linkGenerator
                    )
                }
            }
            //add attributeId element
            if (attributeElement) {

                final Map<String, String> attributeIdAttributes = [:]
                if (attributeType) {
                    attributeIdAttributes.put("attributeType", attributeType)
                }
                attributeIdAttributes.put("label", attributeElement.label)

                attributeId(attributeIdAttributes) {
                    generateLink(
                            [
                                    mapping: 'element',
                                    absolute: true,
                                    rel: 'related',
                                    mediaType: elementMediaType,
                                    params: [id: attributeElement.id]
                            ],
                            markupBuilder, linkGenerator
                    )
                }
            }
            if (contextItem.extValueId) {
                extValueId(contextItem.extValueId)
            }
        }

    }
}