package elasticsearchplugin

//import org.codehaus.groovy.grails.web.json.JSONArray
//import org.codehaus.groovy.grails.web.json.JSONObject


//import grails.converters.JSON


import wslite.json.JSONArray
import wslite.json.JSONException
import wslite.json.JSONObject
import wslite.rest.RESTClientException

class ElasticSearchService {

    QueryExecutorService queryExecutorService
    // Question:  why not this->def grailsConfig
    // instead of the following lines?
    def baseUrl =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.baseUrl
    def elasticAssayIndex =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticAssayIndex
    def elasticCompoundIndex =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticCompoundIndex
    def elasticSearchRequester =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticSearchRequester
    def elasticAssayType =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticAssayType
    def elasticXCompoundType =  "xcompound"
    def elasticXCompoundIndex =  "/compound"
    def elasticCompoundType =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticCompoundType

    static transactional = false
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
     * General-purpose Elastic Search method. Based on the the searching term you're providing (  that's the inBardQueryType
     * along with a particular value in inBardQueryType ), and the value you'd like to end up with ( that's the  outBardQueryType )
     * this method should figure out which Elastic Search index is the right one to give you what you need. Note also  the
     * parameter ( additionalParms which we can use to pass in anything else but the method needs.  By providing a default
     * value for this parameter it becomes optional, and by making this parameter the first weekend choose to sprinkle those
     * keyvalue pairs throughout the method call specification as desired, knowing that they will all get patched up into
     * the additionalParms map.
     *
     * The reason for making searchValue an object is that we can easily handle either lists or strings. Note that Elastic Search
     * want strings to be space delimited, so replace, those with spaces if necessary.
     *
     * @param additionalParms
     * @param inBardQueryType
     * @param searchValue
     * @return
     */
    JSONObject elasticSearchQuery( LinkedHashMap additionalParms=[:],
                                   BardQueryType inBardQueryType,
                                   Object searchValue,
                                   BardQueryType outBardQueryType ) {
        // prepare the search value.  Take strings or lists, as long as toString makes it into something useful
        String searchParmForEs =  searchValue?.toString()
        if  (searchParmForEs.contains(","))
            searchParmForEs=searchParmForEs.replaceAll(","," ")
        searchParmForEs-="["
        searchParmForEs-="]"
        // put together the URL for elastic search
        String elasticNodeSpecifier =  baseUrl +
                chooseIndexToSearch( inBardQueryType, outBardQueryType) +
                elasticSearchRequester
        // Prepare to page, if necessary
        Integer fromValue = 0
        Integer sizeValue = 500
        if (additionalParms.containsKey("from"))
            fromValue = additionalParms ["from"]
        if (additionalParms.containsKey("size"))
            sizeValue = additionalParms ["size"]
        //  Combine everything together to make the final JSON request
        String searchSpecifier = chooseSearchSpecifier(inBardQueryType,outBardQueryType,searchParmForEs,fromValue,sizeValue)
        JSONObject  jSONObject
        try {
            println  searchSpecifier
            jSONObject = new JSONObject(searchSpecifier)
        }
        catch (JSONException exp) {
            String message = exp?.toString()
            log.error("Error building JSON  to send  to Elastic Search: ${message}")
        }
        searchQueryStringQuery(  elasticNodeSpecifier,  jSONObject )
    }

    /**
     *   Simplified signature of  elasticSearchQuery for the most common 'all' search.
     * @param additionalParms
     * @param searchValue
     * @return
     */
    def elasticSearchQuery( LinkedHashMap additionalParms=[:],
                                   Object searchValue ) {
        JSONObject response //= new  JSONObject ()
        if (additionalParms.size() == 0)
            response = elasticSearchQuery(additionalParms, BardQueryType.Xcompound, searchValue, BardQueryType.Default )
        else {
            if (additionalParms.containsKey("searchIndex")) {
                def requestedSearchIndex =  additionalParms["searchIndex"]
                if (requestedSearchIndex instanceof BardQueryType ) {
                    BardQueryType bardQueryType =  requestedSearchIndex as BardQueryType
                   response =   elasticSearchQuery(additionalParms,bardQueryType, searchValue, BardQueryType.Default )
                    print  response.toString()
                }

            }
            else
                response =   elasticSearchQuery(additionalParms,requestedSearchIndex as BardQueryType, searchValue, BardQueryType.Default )
        }
        JSONArray hits = response?.hits?.hits ?: [] as JSONArray
        List<ESAssay> assays = []
        List<ESCompound> compounds = []
        List<ESXCompound> xcompounds = []

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
            }  else if (hit._type == elasticXCompoundType) {
                 xcompounds.add( new ESXCompound(hit) )
            }
        }

        return ["assays":assays, "compounds":compounds, "xcompounds": xcompounds]

    }




/**
 * Here we pick which Elastic Search index to use.  Note that if nothing is selected then
 *  we return a blank, which is fine because Elastic Search interprets that blank
 *  as a request to search all indexes. The logic thus becomes "pick an index if we
 *  know which one to choose, otherwise search everything
 *
 * @param inBardQueryType
 * @param outBardQueryType
 */
    private String chooseIndexToSearch(BardQueryType inBardQueryType,BardQueryType outBardQueryType) {
        // for now all searches handled by the plug-in will cross the "compound" index ( type xcompound  ) and
        //  the "assays" index (type assay).
        String index = ""
        index  = elasticXCompoundIndex+",assays"
    }



    /**
     * Change the JSON that specifies how we search elastic search
     * @param indexBardQueryType
     * @param outBardQueryType
     * @param inValue
     * @param fromIndex
     * @param size
     * @return
     */
    private String chooseSearchSpecifier( BardQueryType indexBardQueryType,
                                         BardQueryType outBardQueryType,
                                         String inValue,
                                         Integer fromIndex,
                                         Integer size) {
        // For now every search will be in "all field" search.   This method should be responsible for inserting values for paging.
        String searchSpecifier = """{
                       "query":{
                           "query_string":{
                               "default_field":"_all",
                               "query":"$inValue"
                            }
                       },
                       "from":${fromIndex},"size":${size}
                    }"""

        searchSpecifier.toString()
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

        return ["assays":assays, "compounds":compounds]
    }

    JSONObject getAssayDocument(Integer docId) {
        String elasticSearchQueryString = "${elasticSearchBaseUrl}/${assayIndexName}/${assayIndexTypeName}/${docId}"
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
            log.error("Error querying the ElasticSearch API server: ${code} - ${message}")
        }

        return result
    }
}



public enum BardQueryType {
    Assay,
    Compound,
    Probe,
    Experiment,
    Project,
    Target,
    Xcompound,
    Default
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


/**
 * A single compound result item, returned from ElasticSearch search.
 */
public class ESXCompound extends ESResult implements Serializable{
    String cid
    String probeId
    String smiles
    String url
    List <Integer>  apids
    List <Integer>  sids


    ESXCompound() {
        super()
    }

    ESXCompound(JSONObject hitJsonObj) {
        this._index = hitJsonObj?._index
        this._type = hitJsonObj?._type
        this._id = hitJsonObj?._id
        this.cid = hitJsonObj?._source.cid
        JSONObject xcompoundSource = hitJsonObj?._source
        this.cid = xcompoundSource?.cid
        this.probeId = xcompoundSource?.probeId
        this.smiles = xcompoundSource?.smiles
        this.url = xcompoundSource?.url
        this.apids = xcompoundSource?.apids?.toList()
        List listOfLists  = xcompoundSource?.sids?.toList() // This line is a workaround for an accidental list of lists
        this.sids = listOfLists ? listOfLists[0].toList() :  listOfLists
    }


    static List<Integer> combinedApids (List <ESXCompound> listOfEsxCompounds) {
       def apidLists = new  ArrayList< List< Integer>>()
       for (ESXCompound eSXCompound in listOfEsxCompounds)
          apidLists << eSXCompound.apids
       mergeLists( apidLists )
    }


    static List<Integer> combinedSids (List <ESXCompound> listOfEsxCompounds) {
        def sidLists = new  ArrayList<List< Integer>>()
        for (ESXCompound eSXCompound in listOfEsxCompounds)
            sidLists << eSXCompound.sids
        mergeLists( sidLists )
    }




    static List<Integer> mergeLists( List< List< Integer>> lists) {
        List<Integer>  first
        if (lists.size()==0)
            return (new ArrayList())
        else {
            first = lists.first()
            if ((lists.size()>1)) {
                for(  List< Integer> aList in (lists.drop(1) ) ) {
                    first=first.plus(aList).unique()
                }
            }
        }
        return first
    }


    @Override
    String toString() {
        return this.cid
    }
}