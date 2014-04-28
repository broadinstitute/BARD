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

            generateLink(
                    [
                            mapping: 'externalSystem', absolute: true,
                            rel: 'self', mediaType: this.mediaTypesDTO.externalSystemMediaType,
                            params: [id: externalSystem.id]
                    ],
                    markupBuilder,
                    this.grailsLinkGenerator
            )
            generateLink(
                    [
                            mapping: 'externalSystems', absolute: true,
                            rel: 'up', mediaType: this.mediaTypesDTO.externalSystemsMediaType
                    ],
                    markupBuilder,
                    this.grailsLinkGenerator
            )
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
                generateLink(
                        [
                                mapping: 'externalSystem', absolute: true,
                                rel: 'related', mediaType: this.mediaTypesDTO.externalSystemMediaType,
                                params: [id: externalReference.externalSystem.id]
                        ],
                        markupBuilder,
                        this.grailsLinkGenerator
                )
            }
            generateLink(
                    [
                            mapping: 'externalReference', absolute: true,
                            rel: 'self', mediaType: this.mediaTypesDTO.externalReferenceMediaType,
                            params: [id: externalReference.id]
                    ],
                    markupBuilder,
                    this.grailsLinkGenerator
            )
            generateLink(
                    [
                            mapping: 'externalReferences', absolute: true,
                            rel: 'up', mediaType: this.mediaTypesDTO.externalReferencesMediaType
                    ],
                    markupBuilder,
                    this.grailsLinkGenerator
            )
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
