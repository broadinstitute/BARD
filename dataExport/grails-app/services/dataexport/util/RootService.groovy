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

package dataexport.util

import dataexport.registration.MediaTypesDTO
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class RootService {
    LinkGenerator grailsLinkGenerator
    MediaTypesDTO mediaTypesDTO


    public void generateRootElement(final MarkupBuilder xml) {
        xml.bardexport() {
            final String dictionaryHref = grailsLinkGenerator.link(mapping: 'dictionary', absolute: true).toString()
            link(rel: 'item', title: "The BARD Dictionary", href: "${dictionaryHref}", type: "${this.mediaTypesDTO.dictionaryMediaType}") {

            }
            final String assaysHref = grailsLinkGenerator.link(mapping: 'assays', absolute: true).toString()
            link(rel: 'item', title: "List of assays, ready for extraction", href: "${assaysHref}", type: "${this.mediaTypesDTO.assaysMediaType}") {

            }
            final String projectsHref = grailsLinkGenerator.link(mapping: 'projects', absolute: true).toString()
            link(rel: 'item', title: "List of projects, ready for extraction", href: "${projectsHref}", type: "${this.mediaTypesDTO.projectsMediaType}") {

            }

            final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true).toString()
            link(rel: 'item', title: 'List of experiments, ready for extraction', type: "${this.mediaTypesDTO.experimentsMediaType}", href: "${experimentsHref}") {
            }

            final String externalReferencesHref = grailsLinkGenerator.link(mapping: 'externalReferences', absolute: true).toString()
            link(rel: 'item', title: 'List of external references', type: "${this.mediaTypesDTO.externalReferencesMediaType}", href: "${externalReferencesHref}") {
            }

            final String externalSystemsHref = grailsLinkGenerator.link(mapping: 'externalSystems', absolute: true).toString()
            link(rel: 'item', title: 'List of external systems', type: "${this.mediaTypesDTO.externalSystemsMediaType}", href: "${externalSystemsHref}") {
            }
        }
    }
}
