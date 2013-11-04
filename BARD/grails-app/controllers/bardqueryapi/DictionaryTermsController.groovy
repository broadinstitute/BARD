package bardqueryapi
import bard.db.dictionary.Descriptor
import bard.db.dictionary.OntologyDataAccessService

class DictionaryTermsController {

    OntologyDataAccessService ontologyDataAccessService

    def index() {
        redirect(action: "dictionaryTerms")
    }

    def dictionaryTerms() {
        final List<Descriptor> descriptors = ontologyDataAccessService.getDescriptors(null, null)
        render view: "dictionaryTerms", model: [capDictionary: descriptors]
    }
}
