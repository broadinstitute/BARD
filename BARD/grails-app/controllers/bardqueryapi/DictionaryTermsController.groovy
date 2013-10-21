package bardqueryapi

import bard.core.rest.spring.DictionaryRestService
import bard.core.rest.spring.util.CapDictionary
import bard.db.dictionary.Element
import grails.plugins.springsecurity.Secured

class DictionaryTermsController {

    def index() {
        redirect(action: "dictionaryTerms")
    }

    def dictionaryTerms() {
        final List<Element> elements = Element.list()
        render view: "dictionaryTerms", model: [capDictionary: elements]
    }
}
