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