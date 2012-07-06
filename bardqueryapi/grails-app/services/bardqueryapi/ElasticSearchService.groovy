package bardqueryapi

import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.json.JSONArray

class ElasticSearchService {

    def grailsApplication
    QueryExecutorService queryExecutorService

    /**
     * Returns a map of lists of assays, compounds, etc. as returned and parsed from the ElasticSearch query.
     * @param searchString
     * @return
     */
    Map<String, List<ESResult>> search(String searchString) {
        //TODO
        //1. Query all assays
        //2. Query all compounds
        //3. Build the returned map.
        String elasticSearchBaseUrl = grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl
        String elasticSearchQueryString = "${elasticSearchBaseUrl}/assays/_search?q=${searchString}&size=99999"
        JSONObject response = queryExecutorService.executeGetRequestJSON(elasticSearchQueryString, null)
        JSONArray hits = response.hits.hits
        List<ESAssay> assays = []
        List<ESCompound> compounds = []

        for (JSONObject hit in hits) {
            if (hit._type == 'assay') {
                String targetName =   hit._source.targets != null ? hit._source.targets[0].name   : "Unknown target"
                ESAssay esAssay = new ESAssay(_index: hit._index, _type: hit._type, _id: hit._id, assayNumber: hit._source.aid, assayName: targetName + ' (' + hit._source.targets[0].acc + ')')
                assays.add(esAssay)
            } else if (hit._type == 'compound') {
                ESCompound esCompound = new ESCompound(_index: hit._index, _type: hit._type, _id: hit._id, cid: hit._source.cid, smiles: hit._source.smiles)
                compounds.add(esCompound)
            }
        }

        return [assays: assays, compounds: compounds] as Map
    }
}

/**
 * Describes a single ElasticSearch result item
 */
public abstract class ESResult {
    String _index
    String _type
    String _id
    abstract String  makePretty()
}

/**
 * A single assay result item, returned from ElasticSearch search.
 */
public class ESAssay extends ESResult {
    String assayNumber
    String assayName
    String makePretty() {
        assayName
    }
}

/**
 * A single compound result item, returned from ElasticSearch search.
 */
public class ESCompound  extends ESResult {
    String cid
    String smiles
    String  makePretty() {
        smiles
    }
}