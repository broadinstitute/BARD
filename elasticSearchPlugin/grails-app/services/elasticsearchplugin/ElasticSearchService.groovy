package elasticsearchplugin

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import wslite.rest.RESTClientException

class ElasticSearchService {

    static transactional = false

    QueryExecutorService queryExecutorService
    String elasticSearchBaseUrl
    String assayIndexName
    String assayIndexTypeName
    String compoundIndexName
    String compoundIndexTypeName
    final static String searchParamName = '_search?q='


    /**
     *  TODO: Unify the return types so they both return one type of JSONObject
     *
     *  We should absolutely do that in the next iteration. Right now it is too late to refactor
     * @param queryObject
     * The JSONObject should conform to an ES query syntax otherwise the parser would throw an exception
     *  Uses the ES query String query
     * @return  the data retrieved from the Server as a JSONObject
     */
    wslite.json.JSONObject searchQueryStringQuery(final String url, final wslite.json.JSONObject queryObject) {
        def response = queryExecutorService.postRequest(url, queryObject.toString())
        if(response instanceof wslite.json.JSONObject){
            return response
        }
        if(response instanceof wslite.json.JSONArray){
            return response.toJSONObject(response)
        }
        //the response should either be a JSOnObject or a JSONArray
        return null
    }

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

        JSONObject response = new JSONObject()
        try {
            response = queryExecutorService.executeGetRequestJSON(elasticSearchQueryString, null)
        }
        catch (RESTClientException exp) {
            String message = exp?.response?.statusMessage
            Integer code = exp?.response?.statusCode
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

        final JSONObject responseObject = new JSONObject([assays: assays, compounds: compounds])
        return responseObject
    }

    JSONObject getAssayDocument(Integer docId) {
        String elasticSearchQueryString = "${elasticSearchBaseUrl}/${assayIndexName}/${assayIndexTypeName}/${docId}"
        return getElasticSearchDocument(elasticSearchQueryString)
    }

    JSONObject getCompoundDocument(Integer docId) {
        String elasticSearchQueryString = "${elasticSearchBaseUrl}/${compoundIndexName}/${compoundIndexTypeName}/${docId}"
        return getElasticSearchDocument(elasticSearchQueryString)
    }

    private JSONObject getElasticSearchDocument(String elasticSearchQueryString) {
        JSONObject result = [:] as JSONObject
        try {
            result = queryExecutorService.executeGetRequestJSON(elasticSearchQueryString, null)
        }
        catch (RESTClientException exp) {
            String message = exp?.response?.statusMessage
            Integer code = exp?.response?.statusCode
            log.error("Error querying the ElasticSearcg API server: ${code} - ${message}")
        }

        return result
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
    String assayNumber
    String assayName

    ESAssay() {
        super()
    }

    /**
     * A custom constructor to instantiate an assay from an ES assay doc type.
     * @param _source
     */
    public ESAssay(JSONObject hitJsonObj) {
        this._index = hitJsonObj?._index
        this._type = hitJsonObj?._type
        this._id = hitJsonObj?._id
        JSONObject assaySource = hitJsonObj?._source
        this.assayNumber = assaySource?.aid
        JSONObject firstAssayTarget = assaySource.targets ? assaySource.targets[0] : [:] as JSONObject
        this.assayName = "${firstAssayTarget?.name} (${firstAssayTarget?.acc})"
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