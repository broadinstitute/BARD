package dataexport.registration

import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import dataexport.util.ExportAbstractService
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class ExternalReferenceExportService extends ExportAbstractService {

    LinkGenerator grailsLinkGenerator
    MediaTypesDTO mediaTypesDTO

    ExternalReferenceExportService() {
    }

    /**
     * Stream an external reference with a given id
     * @param markupBuilder
     * @param externalReferenceId
     */
    public Long generateExternalReference(
            final MarkupBuilder markupBuilder, final Long externalReferenceId) {
        final ExternalReference externalReference = ExternalReference.get(externalReferenceId)
        if (!externalReference) {
            final String message = "External Reference with Id ${externalReference} does not exist"
            log.error(message)
            throw new NotFoundException(message)
        }

        this.generateExternalReference(markupBuilder, externalReference)
        return externalReference.version
    }
    /**
     * Stream an externalSystem with an given id
     * @param markupBuilder
     * @param externalSystemId
     */
    public Long generateExternalSystem(
            final MarkupBuilder markupBuilder, final Long externalSystemId) {
        final ExternalSystem externalSystem = ExternalSystem.get(externalSystemId)
        if (!externalSystem) {
            final String message = "External System with Id ${externalSystem} does not exist"
            log.error(message)
            throw new NotFoundException(message)
        }

        this.generateExternalSystem(markupBuilder, externalSystem)
        return externalSystem.version
    }
    /**
     * Stream an external system
     * @param markupBuilder
     * @param externalSystem
     */
    public void generateExternalSystem(
            final MarkupBuilder markupBuilder, final ExternalSystem externalSystem) {
        final Map<String, String> attributes = [:]
        attributes.put('name', externalSystem.systemName)

        if (externalSystem.owner) {
            attributes.put('owner', externalSystem.owner)
        }

        markupBuilder.externalSystem(attributes) {
            if (externalSystem.systemUrl) {
                systemUrl(externalSystem.systemUrl)
            }
            final String externalSystemHref = generateHref('externalSystem', externalSystem.id, this.grailsLinkGenerator)
            this.generateLink(markupBuilder, externalSystemHref, 'self', this.mediaTypesDTO.externalSystemMediaType)

            final String externalSystemsHref = generateHref('externalSystem', null, this.grailsLinkGenerator)
            this.generateLink(markupBuilder, externalSystemsHref, 'up', this.mediaTypesDTO.externalSystemsMediaType)
        }
    }
    /**
     * Stream an external reference
     * @param markupBuilder
     * @param externalReference
     */
    public void generateExternalReference(
            final MarkupBuilder markupBuilder, final ExternalReference externalReference) {

        markupBuilder.externalReference() {
            if (externalReference.extAssayRef) {
                externalAssayRef(externalReference.extAssayRef)
            }
            if (externalReference.externalSystem) {
                final String externalSystemHref = this.generateHref('externalSystem', externalReference.externalSystem.id, this.grailsLinkGenerator)
                this.generateLink(markupBuilder, externalSystemHref, 'related', this.mediaTypesDTO.externalSystemMediaType)
            }
            final String externalReferenceHref = generateHref('externalReference', externalReference.id, this.grailsLinkGenerator)
            this.generateLink(markupBuilder, externalReferenceHref, 'self', this.mediaTypesDTO.externalReferenceMediaType)

            final String externalReferencesHref = generateHref('externalReferences', null, this.grailsLinkGenerator)
            this.generateLink(markupBuilder, externalReferencesHref, 'up', this.mediaTypesDTO.externalReferencesMediaType)

        }
    }
    /**
     * Stub for generating external references from the CAP
     * @param markupBuilder
     */
    public void generateExternalReferences(
            final MarkupBuilder markupBuilder) {

        final List<ExternalReference> externalReferences = ExternalReference.findAll()

        final int numberOfExternalReferences = externalReferences.size()


        markupBuilder.externalReferences(count: numberOfExternalReferences) {
            for (ExternalReference externalReference : externalReferences) {
                generateExternalReference(markupBuilder, externalReference)
            }
        }
    }
    /**
     * Stub for generating externalSystems
     * @param markupBuilder
     */
    public void generateExternalSystems(
            final MarkupBuilder markupBuilder) {

        final List<ExternalSystem> externalSystems = ExternalSystem.findAll()

        final int numberOfExternalSystems = externalSystems.size()


        markupBuilder.externalSystems(count: numberOfExternalSystems) {
            for (ExternalSystem externalSystem : externalSystems) {
                generateExternalSystem(markupBuilder, externalSystem)
            }
        }
    }
}
