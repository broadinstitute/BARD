package elasticsearchplugin

//import org.codehaus.groovy.grails.web.json.JSONArray
//import org.codehaus.groovy.grails.web.json.JSONObject
import wslite.rest.RESTClientException
//import grails.converters.JSON
import wslite.json.JSONObject
import wslite.json.JSONArray
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ElasticSearchService {

    static transactional = false

    QueryExecutorService queryExecutorService
    String elasticSearchBaseUrl
    String assayIndexName
    String assayIndexTypeName
    String compoundIndexName
    String compoundIndexTypeName
    final static String searchParamName = '_search'

    final static String ES_QUERY_STRING_TEMPLATE = '''{
        "query": {
            "query_string": {
                "default_field": "_all",
                "query": "*"
            }
        },
        "size": 200
    }'''

    /**
     * @param queryObject
     * The JSONObject should conform to an ES query syntax otherwise the parser would throw an exception
     *  Uses the ES query String query
     * @return the data retrieved from the Server as a JSONObject
     */
    JSONObject searchQueryStringQuery(final String url, final JSONObject queryObject) {
            def response = queryExecutorService.postRequest(url, queryObject.toString())
        if (response instanceof JSONObject) {
            return response
        }
        if (response instanceof JSONArray) {
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
    Map<String, List> search(String searchTerm, Integer max = 99999) {
        String queryObject = searchTerm
        if (searchTerm) {
            queryObject = ES_QUERY_STRING_TEMPLATE.replaceAll("\\*", searchTerm)

        }

        //1. Query all assays
        //2. Query all compounds
        //3. Build the returned map.
        String elasticSearchQueryString = "${elasticSearchBaseUrl}/${assayIndexName}/${searchParamName}"

        JSONObject response = new JSONObject()
        try {
            response = searchQueryStringQuery(elasticSearchQueryString, new JSONObject(queryObject))
        }
        catch (RESTClientException exp) {
            String message = exp?.response?.statusMessage
            Integer code = exp?.response?.statusCode
            log.error("Error querying the ElasticSearcg API server: ${code} - ${message}")
        }

        JSONArray hits = response?.hits?.hits ?: [] as JSONArray
        List<ESAssay> assays = []
        List<ESCompound> compounds = []
        final Map<TargetAccessionNumber, Set<String>> accessionNumberToAssayIds = [:]

        for (JSONObject hit in hits) {
            if (hit._type == assayIndexTypeName) {
                aggregateAccessionNumbersToAssayNumbers(hit, accessionNumberToAssayIds)
                ESAssay esAssay = new ESAssay(hit)
                assays.add(esAssay)
            } else if (hit._type == compoundIndexTypeName) {
                //'compound' type in the 'assays' index is just a list of CIDs.
                JSONArray cids = hit?._source?.cids ?: [] as JSONArray
                for (def cid in cids) {
                    ESCompound esCompound = new ESCompound(_id: cid as String, _index: compoundIndexName, _type: compoundIndexTypeName, cid: cid as String)
                    JSONObject compoundESDocument = getCompoundDocument(new Integer(cid.toString()))
                    String smiles = compoundESDocument?._source?.smiles
                    esCompound.smiles = smiles
                    compounds.add(esCompound)
                }
            }
        }
        return ["assays": assays, "compounds": compounds, "compoundHeaderInfo": accessionNumberToAssayIds]
    }
    /**
     * A custom constructor to instantiate an assay from an ES assay doc type.
     * @param _source
     */
    protected void aggregateAccessionNumbersToAssayNumbers(JSONObject hitJsonObj, Map<TargetAccessionNumber, Set<String>> accessionNumberToAssayIds) {
        final JSONObject assaySource = hitJsonObj?._source
        def assayNumber = assaySource?.aid

        JSONArray assayTargets = assaySource.targets
        for (JSONObject assayTarget in assayTargets) {
            final TargetAccessionNumber targetAccessionNumber = new TargetAccessionNumber(accessionNumber: assayTarget.acc, targetName: assayTarget.name)
            Set<String> setOfAssayIds = accessionNumberToAssayIds.get(targetAccessionNumber)
            if (!setOfAssayIds) {
                setOfAssayIds = [] as Set<String>
            }
            if (setOfAssayIds.size() <= 3) {  //we will only display a max of 3 assay numbers per target
                //this should probably be done on the client side
                setOfAssayIds.add(assayNumber.toString())
                accessionNumberToAssayIds.put(targetAccessionNumber, setOfAssayIds)
            }
        }
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
            result = (JSONObject)queryExecutorService.executeGetRequestJSON(elasticSearchQueryString, null)
        }
        catch (RESTClientException exp) {
            String message = exp?.response?.statusMessage
            Integer code = exp?.response?.statusCode
            log.error("Error querying the ElasticSearch API server: ${code} - ${message}")
        }

        return result
    }
}

/**
 * Describes a single ElasticSearch result item
 */
public abstract class ESResult implements Serializable{
    String _index
    String _type
    String _id
}

/**
 * A single assay result item, returned from ElasticSearch search.
 */
public class ESAssay extends ESResult implements Serializable{
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
public class ESCompound extends ESResult implements Serializable{
    String cid
    String smiles

    ESCompound() {
        super()
    }

    public ESCompound(JSONObject hitJsonObj) {
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
public class TargetAccessionNumber implements Serializable, Comparable<TargetAccessionNumber> {
    String targetName
    String accessionNumber

    public TargetAccessionNumber() {

    }

    @Override
    int compareTo(TargetAccessionNumber o) {
        return this.accessionNumber.compareTo(o.accessionNumber)
    }

    @Override
    public boolean equals(Object obj) {
        final TargetAccessionNumber otherObject = (TargetAccessionNumber) obj;

        return new EqualsBuilder()
                .append(this.accessionNumber, otherObject.accessionNumber)
                .append(this.targetName, otherObject.targetName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.accessionNumber)
                .append(this.targetName)
                .toHashCode();
    }
}