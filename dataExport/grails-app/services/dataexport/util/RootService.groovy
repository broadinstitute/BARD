package dataexport.util

import dataexport.registration.MediaTypesDTO
import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class RootService {
    LinkGenerator grailsLinkGenerator
    final String dictionaryMediaType
    final String assaysMediaType
    final String projectsMediaType
    final String experimentsMediaType
    /**
     * This is wired in resources.groovy
     *
     * @param mediaTypesDTO
     */
    RootService(final MediaTypesDTO mediaTypesDTO) {
        this.dictionaryMediaType = mediaTypesDTO.dictionaryMediaType
        this.assaysMediaType = mediaTypesDTO.assaysMediaType
        this.projectsMediaType = mediaTypesDTO.projectsMediaType
        this.experimentsMediaType = mediaTypesDTO.experimentsMediaType
    }

    public void generateRootElement(final MarkupBuilder xml) {
        xml.bardexport() {
            final String dictionaryHref = grailsLinkGenerator.link(mapping: 'dictionary', absolute: true).toString()
            link(rel: 'item', title: "The BARD Dictionary", href: "${dictionaryHref}", type: "${this.dictionaryMediaType}") {

            }
            final String assaysHref = grailsLinkGenerator.link(mapping: 'assays', absolute: true).toString()
            link(rel: 'item', title: "List of assays, ready for extraction", href: "${assaysHref}", type: "${this.assaysMediaType}") {

            }
            final String projectsHref = grailsLinkGenerator.link(mapping: 'projects', absolute: true).toString()
            link(rel: 'item', title: "List of projects, ready for extraction", href: "${projectsHref}", type: "${this.projectsMediaType}") {

            }

            final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true).toString()
            link(rel: 'item', title: 'List of experiments, ready for extraction', type: "${this.experimentsMediaType}", href: "${experimentsHref}") {
            }
        }
    }
}