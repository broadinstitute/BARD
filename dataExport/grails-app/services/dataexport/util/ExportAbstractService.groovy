package dataexport.util

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
            //link to fetch external reference
            final String currentHref = generateHref('externalReference', externalReference.id, grailsLinkGenerator)
            generateLink(markupBuilder, currentHref, 'related', mediaTypesDTO.externalReferenceMediaType)
        }
    }
    /**
     * Generate a string that could be used as the href value for a Link object
     * @param resourceName
     * @param entityId
     * @return a string that could be used as the href value for a Link object
     */
    public String generateHref(String resourceName, Long entityId,final LinkGenerator grailsLinkGenerator) {
        if (entityId == null) {
            return grailsLinkGenerator.link(mapping: resourceName, absolute: true).toString()
        }
        return grailsLinkGenerator.link(mapping: resourceName, absolute: true, params: [id: entityId]).toString()
    }
    /**
     *
     * @param markupBuilder
     * @param currentHref
     * @param linkRelation
     * @param mediaType
     * Generate the link element for an xml document
     */
    public void generateLink(final MarkupBuilder markupBuilder, final String currentHref, final String linkRelation, final String mediaType) {
        markupBuilder.link(rel: linkRelation, href: currentHref, type: mediaType)
    }

    /**
     *
     * @param markupBuilder
     * @param assayDocument
     * @param generateContent - True if we should add the contents of this document
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
            final String documentHref = generateHref(resourceName, entityId,grailsLinkGenerator)
            generateLink(markupBuilder, documentHref, 'self', mediaType)

            final String parentEntityHref = generateHref(relatedResourceName, relatedEntityId,grailsLinkGenerator)
            generateLink(markupBuilder, parentEntityHref, 'related', relatedMediaType)
        }
    }
}