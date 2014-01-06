package external.ontology.proxy

import bard.validation.ext.ExternalItem
import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import bard.validation.ext.ExternalOntologyFactory
import grails.converters.JSON
import org.apache.commons.lang3.StringUtils
import org.springframework.util.Assert

import javax.servlet.http.HttpServletResponse

import static bard.validation.ext.ExternalOntologyNCBI.NCBI_EMAIL
import static bard.validation.ext.ExternalOntologyNCBI.NCBI_TOOL

class ExternalOntologyController {

    ExternalOntologyFactory externalOntologyFactory

    public static final int MAX_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE = 500
    public static final int MIN_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE = 1

    private static final Properties EXTERNAL_ONTOLOGY_PROPERTIES = new Properties([(NCBI_TOOL): 'bard', (NCBI_EMAIL): 'default@bard.nih.gov'])

    def findExternalItemById(String externalUrl, String id) {
        Assert.hasText(externalUrl, "externalUrl cannot be blank")
        Assert.hasText(id, "id cannot be blank")
        final Map responseMap = [:]
        try {
            final ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl.decodeURL(), EXTERNAL_ONTOLOGY_PROPERTIES)
            println(externalOntology)
            if (externalOntology) {
                final String urlDecodedId = id.decodeURL()
                final ExternalItem externalItem = externalOntology.findById(urlDecodedId)
                println("externalItem: ${externalItem}")
                responseMap.putAll(toMapForSelect2(externalItem))
            }
            render responseMap as JSON
        } catch (ExternalOntologyException e) {
            String msg = "Exception when calling externalOntology.findById(${id}) with externalUrl: $externalUrl ${e.message}"
            log.warn(msg, e)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg)
        }
    }
    /**
     *
     * @param externalUrl acts as a key to get an underlying implementation for a resource
     * @param term to search for
     * @param limit max number of items to be returned up to a max of 500 if less than 1 or more than 500 then defaults to DEFAULT_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE
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
        limit = Math.max(limit,MIN_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE)
        limit = Math.min(limit,MAX_EXTERNAL_ONTOLOGY_MATCHING_PAGE_SIZE)
        final Map responseMap = ['externalItems': []]
        try {
            final ExternalOntologyAPI externalOntology = externalOntologyFactory.getExternalOntologyAPI(externalUrl.decodeURL(), EXTERNAL_ONTOLOGY_PROPERTIES)
            if (externalOntology) {
                final List<ExternalItem> matching = externalOntology.findMatching(term.decodeURL(), limit)
                final List<ExternalItem> externalItems = matching ?: []
                externalItems.sort(true) { ExternalItem a, ExternalItem b -> a.display?.toLowerCase() <=> b.display?.toLowerCase() }
                responseMap.externalItems.addAll(externalItems.collect { ExternalItem item -> toMapForSelect2(item) })
            }
            render responseMap as JSON
        } catch (ExternalOntologyException e) {
            String msg = "Exception when calling externalOntology.findMatching(${term}) with externalUrl: $externalUrl ${e.message}"
            log.warn(msg, e)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg)
        }
    }

    /**
     *
     * @param externalUrl acts as a key to get an underlying implementation for a resource
     * @return Map as JSON of the form [hasSupport: false] or [hasSupport: true]
     */
    def externalOntologyHasIntegratedSearch(String externalUrl) {
        Map responseMap = [hasSupport: false]
        try {
            if (StringUtils.isNotBlank(externalUrl) && externalOntologyFactory.getExternalOntologyAPI(externalUrl, EXTERNAL_ONTOLOGY_PROPERTIES)) {
                responseMap.hasSupport = true
            }
            render responseMap as JSON
        }
        catch (ExternalOntologyException e) {
            String msg = "Exception when calling getExternalOntologyAPI with externalUrl: $externalUrl ${e.message}"
            log.warn(msg, e)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
        }
    }

    private Map toMapForSelect2(ExternalItem item) {
        Map map = [:]
        if (item) {
            map = ['id': item.id, 'display': item.display?:'']
        }
        map
    }
}
