package dataexport.registration

import bard.db.registration.ExternalReference
import bard.db.registration.ExternalSystem
import exceptions.NotFoundException
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class ExternalReferenceExportService {

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
            final String externalSystemHref = grailsLinkGenerator.link(mapping: 'externalSystem', absolute: true, params: [id: externalSystem.id]).toString()
            final String externalSystemsHref = grailsLinkGenerator.link(mapping: 'externalSytems', absolute: true).toString()

            markupBuilder.link(rel: 'self', href: "${externalSystemHref}", type: "${this.mediaTypesDTO.externalSystemMediaType}")
            markupBuilder.link(rel: 'up', href: "${externalSystemsHref}", type: "${this.mediaTypesDTO.externalSystemsMediaType}")

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
                final String externalSystemHref = grailsLinkGenerator.link(mapping: 'externalSystem', absolute: true, params: [id: externalReference.externalSystem.id]).toString()
                markupBuilder.link(rel: 'related', href: "${externalSystemHref}", type: "${this.mediaTypesDTO.externalSystemMediaType}")

            }
            final String externalReferenceHref = grailsLinkGenerator.link(mapping: 'externalReference', absolute: true, params: [id: externalReference.id]).toString()
            final String externalReferencesHref = grailsLinkGenerator.link(mapping: 'externalReferences', absolute: true).toString()

            markupBuilder.link(rel: 'self', href: "${externalReferenceHref}", type: "${this.mediaTypesDTO.externalReferenceMediaType}")
            markupBuilder.link(rel: 'up', href: "${externalReferencesHref}", type: "${this.mediaTypesDTO.externalReferenceMediaType}")

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
