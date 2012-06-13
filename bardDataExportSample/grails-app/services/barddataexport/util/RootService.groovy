package barddataexport.util

import groovy.xml.MarkupBuilder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

class RootService {
    LinkGenerator grailsLinkGenerator
    final String dictionaryXmlMimeType

    RootService(final String dictionaryXmlMimeType){
      this.dictionaryXmlMimeType = dictionaryXmlMimeType
    }
    //TODO URLMappings tests
    public void generateRootElement(final MarkupBuilder xml) {
        xml.bardexport() {

//            final String projectsHref = grailsLinkGenerator.link(mapping: 'projects', absolute: true).toString()
//            final String PROJECTS_MEDIA_TYPE = grailsApplication.config.bard.data.export.cap.projects.xml
//            link(rel: 'item', title: "List of projects, ready for extraction", href: "${projectsHref}", type: "${PROJECTS_MEDIA_TYPE}"){

     //       }


            final String dictionaryHref = grailsLinkGenerator.link(mapping: 'dictionary', absolute: true).toString()
            final String DICTIONARY_MEDIA_TYPE = dictionaryXmlMimeType
            link(rel: 'item', title: "The BARD Dictionary", href: "${dictionaryHref}", type: "${DICTIONARY_MEDIA_TYPE}") {

            }

//            final String experimentsHref = grailsLinkGenerator.link(mapping: 'experiments', absolute: true).toString()
//            final String EXPERIMENTS_MEDIA_TYPE = grailsApplication.config.bard.data.export.data.experiments.xml
//            link(rel: 'item', title: 'List of experiments, ready for extraction', type: "${EXPERIMENTS_MEDIA_TYPE}", href: "${experimentsHref}") {
//            }
        }
    }
}