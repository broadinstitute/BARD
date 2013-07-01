package bardqueryapi

import bard.core.rest.spring.DictionaryRestService
import bard.core.rest.spring.util.CapDictionary
import grails.plugins.springsecurity.Secured

@Secured(['isAuthenticated()'])
class DictionaryTermsController {
    DictionaryRestService dictionaryRestService


    def dictionaryTerms() {
        final CapDictionary capDictionary = dictionaryRestService.getDictionary()
        render view: "dictionaryTerms", model: [capDictionary: capDictionary]
    }
}
