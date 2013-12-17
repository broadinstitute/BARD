package external.ontology.proxy

import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import bard.validation.ext.ExternalOntologyFactory
import grails.converters.JSON
import org.springframework.util.Assert

import static bard.validation.ext.ExternalOntologyNCBI.NCBI_EMAIL
import static bard.validation.ext.ExternalOntologyNCBI.NCBI_TOOL

class ExternalOntologyController {

    ExternalOntologyFactory externalOntologyFactory

    private static final int DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE = 50

    private static final Properties EXTERNAL_ONTOLOGY_PROPERTIES = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'default@bard.nih.gov'])

    def findExternalItemById(String externalUrl, String id) {
        Assert.hasText(externalUrl, "externalUrl cannot be blank")
        Assert.hasText(id, "id cannot be blank")
        final Map responseMap = [:]

        try {
            ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES)
            if (externalOntology) {
                final ExternalItem externalItem = externalOntology.findById(id)
                responseMap.putAll(toMapForSelect2(externalItem))
            }
        } catch (ExternalOntologyException e) {
            log.warn("Exception when calling externalOntology.findById(${id}) with externalUrl: $externalUrl ${e.message}")
            responseMap.error = "Integrated lookup for the External Ontology Id: ${id} was not successful at this time."
            throw e
        }
        render responseMap as JSON
    }
    /**
     *
     * @param externalUrl acts as a key to get an underlying implementation for a resource
     * @param term to search for
     * @param limit optional, set the max number of items to be returned, defaults to DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE
     * @return a MAP as JSON like
     * success :
     * [externalItems: [["id": id, "text": "(id) name", "display": "display"]...]]
     *
     * error:
     * ["externalItems": [],"error": "some error message"]
     */
    def findExternalItemsByTerm(String externalUrl, String term, int limit) {

        Assert.hasText(externalUrl, "externalUrl cannot be blank")
        Assert.hasText(term, "term cannot be blank")
        limit = Math.max(limit, DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE)
        final List<ExternalItem> externalItems = []
        final Map responseMap = ['externalItems': []]
        try {
            ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES)
            if (externalOntology) {
                externalItems.addAll(externalOntology.findMatching(term, limit))
                responseMap.externalItems.addAll(externalItems.collect { ExternalItem item -> toMapForSelect2(item) })
            }
            externalItems.sort(true) { ExternalItem a, ExternalItem b -> a.display?.toLowerCase() <=> b.display?.toLowerCase() }
        } catch (ExternalOntologyException e) {
            log.warn("Exception when calling externalOntology.findMatching(${term}) with externalUrl: $externalUrl", e)
            responseMap.error = "Integrated lookup with the term: ${term} was not successful at this time."
        }
        render responseMap as JSON
    }

    /**
     *
     * @param externalUrl acts as a key to get an underlying implementation for a resource
     * @return Map as JSON of the form [hasSupport: false] or [hasSupport: true]
     */
    def externalOntologyHasIntegratedSearch(String externalUrl) {
        Map responseMap = [hasSupport: false]
        try {
            if (externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES)) {
                responseMap.hasSupport = true
            }
        }
        catch (ExternalOntologyException e) {
            log.warn("Exception when calling getExternalOntologyAPI with externalUrl: $externalUrl ,therefore, no itegrated search will be presented to user.")
        }
        render responseMap as JSON
    }

    private Map toMapForSelect2(ExternalItem item) {
        Map map = [:]
        if (item) {
            map = ['id': item.id, 'text': "(${item.id}) ${item.display}", 'display': item.display]
        }
        map
    }
}
