package elasticsearchplugin

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import wslite.rest.RESTClientException
import grails.converters.JSON

class ElasticSearchService {

    QueryExecutorService queryExecutorService
    String elasticSearchBaseUrl
    String assayIndexName
    String assayIndexTypeName
    String compoundIndexName
    String compoundIndexTypeName
    final static String searchParamName = '_search?q='
    /**
     * Returns a json map of lists of assays, compounds, etc. as returned and parsed from the ElasticSearch query.
     * @param searchString
     * @return
     */
    JSONObject search(String searchTerm, Integer max = 99999) {
        //1. Query all assays
        //2. Query all compounds
        //3. Build the returned map.
        String elasticSearchQueryString = "${elasticSearchBaseUrl}/${assayIndexName}/${searchParamName}${searchTerm}&size=${max}"

        JSONObject response
        try {
            response = queryExecutorService.executeGetRequestJSON(elasticSearchQueryString, null)
        }
        catch (RESTClientException exp) {
            String message = exp.response.statusMessage
            final int code = exp.response.statusCode
            log.error("Error querying the ElasticSearcg API server: ${code} - ${message}")
        }

        JSONArray hits = response?.hits?.hits ?: [] as JSONArray
        List<ESAssay> assays = []
        List<ESCompound> compounds = []

        for (JSONObject hit in hits) {
            if (hit._type == assayIndexTypeName) {
                ESAssay esAssay = new ESAssay(hit)
                assays.add(esAssay)
            } else if (hit._type == compoundIndexTypeName) {
                //'compound' type in the 'assays' index is just a list of CIDs.
                JSONArray cids = hit?._source?.cids ?: [] as JSONArray
                for (def cid in cids) {
                    ESCompound esCompound = new ESCompound(_id: cid as String, _index: compoundIndexName, _type: compoundIndexTypeName, cid: cid as String)
                    compounds.add(esCompound)
                }
            }
        }

        return [assays: assays, compounds: compounds] as JSONObject
    }

    JSONObject getAssayDocument(Integer docId) {
        String elasticSearchQueryString = "${elasticSearchBaseUrl}/${assayIndexName}/${assayIndexTypeName}/${docId}"
        JSONObject result
        try {
            result = queryExecutorService.executeGetRequestJSON(elasticSearchQueryString, null)
        }
        catch (RESTClientException exp) {
            String message = exp.response.statusMessage
            final int code = exp.response.statusCode
            log.error("Error querying the ElasticSearcg API server: ${code} - ${message}")
        }

        return result ?: [] as JSONObject
    }
}

/**
 * Describes a single ElasticSearch result item
 */
public abstract class ESResult {
    String _index
    String _type
    String _id
}

/**
 * A single assay result item, returned from ElasticSearch search.
 */
public class ESAssay extends ESResult {
    final String assayNumber
    final String assayName

    /**
     * A custom constructor to instantiate an assay from an ES assay doc type.
     * @param _source
     */
    public ESAssay(JSONObject hitJsonObj) {
        this._index = hitJsonObj?._index
        this._type = hitJsonObj?._type
        this._id = hitJsonObj?._id
        JSONObject assaySource = hitJsonObj?._source
        this.assayNumber = assaySource.aid
        JSONObject firstAssayTarget = assaySource.targets[0]
        this.assayName = "${firstAssayTarget?.targetName} (${firstAssayTarget.acc})"
    }

    @Override
    String toString() {
        return "${this.assayName} (${this.assayNumber})"
    }
}

/**
 * A single compound result item, returned from ElasticSearch search.
 */
public class ESCompound extends ESResult {
    String cid

    ESCompound() {
        super()
    }

    ESCompound(JSONObject hitJsonObj) {
        this._index = hitJsonObj?._index
        this._type = hitJsonObj?._type
        this._id = hitJsonObj?._id
        this.cid = hitJsonObj?._source.cid
    }

    @Override
    String toString() {
        return this.cid
    }
}