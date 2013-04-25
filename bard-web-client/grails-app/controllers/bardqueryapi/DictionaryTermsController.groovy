package bardqueryapi

import bard.core.rest.spring.DataExportRestService
import bard.core.rest.spring.util.CapDictionary
import grails.plugins.springsecurity.Secured

@Secured(['isRememberMe()', 'isAuthenticated()'])
class DictionaryTermsController {
    DataExportRestService dataExportRestService


    def dictionaryTerms() {
        final CapDictionary capDictionary = dataExportRestService.getDictionary()
        render view: "dictionaryTerms", model: [capDictionary: capDictionary]
    }
}
