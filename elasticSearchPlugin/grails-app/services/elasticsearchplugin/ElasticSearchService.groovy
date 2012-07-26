package elasticsearchplugin


import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import wslite.json.JSONArray
import wslite.json.JSONObject
import wslite.rest.RESTClientException

class ElasticSearchService {

    QueryExecutorService queryExecutorService
    // references into elastic search, set in Config
    String baseUrl =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.baseUrl
    String elasticAssayIndex =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticAssayIndex
    String elasticCompoundIndex =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticCompoundIndex
    String elasticSearchRequester =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticSearchRequester
    String elasticAssayType =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticAssayType
    String elasticXCompoundType =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticXCompoundType
    String elasticXCompoundIndex =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticXCompoundIndex
    String elasticCompoundType =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.restNode.elasticCompoundType
    // elements per page by default
    int defaultElementsPerPage =  org.codehaus.groovy.grails.commons.ConfigurationHolder.config.elasticSearchService.defaultElementsPerPage
    // following values set in resources.groovy
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
        String elasticNodeSpecifier =  "${baseUrl}${chooseIndexToSearch( inBardQueryType, outBardQueryType)}${elasticSearchRequester}"
        // Prepare to page, if necessary
        Integer fromValue = 0
        Integer sizeValue = defaultElementsPerPage
        if (additionalParms.containsKey("from"))
            fromValue = additionalParms ["from"]
        if (additionalParms.containsKey("size"))
            sizeValue = additionalParms ["size"]

        //  Combine everything together to make the final JSON request
        String searchSpecifier = chooseSearchSpecifier(inBardQueryType,outBardQueryType,searchParmForEs,fromValue,sizeValue)
        JSONObject  jSONObject =  new JSONObject(searchSpecifier)
        searchQueryStringQuery(  elasticNodeSpecifier,  jSONObject )
    }

    /**
     * elasticSearchQuery provides an easy way to call Elastic Search.  Note that by putting the LinkedHashMap ( with
     * a default value ) as the first parameter that you can optionally specify additional parameters, and that if the
     * values are specified as a keyvalue pairs then the ordering doesn't matter. Therefore you should be able to call:
     *            elasticSearchQuery( "644" )
     *            elasticSearchQuery( "644", size: 100 )
     *            elasticSearchQuery( from: 0, "644", size: 100 )
     *  Passing an a search value that is actually a list ( as opposed to a string ) is also fine.
     *
     * @param additionalParms
     * @param searchValue
     * @return
     */
     Map<String, List>  elasticSearchQuery( LinkedHashMap additionalParms=[:],
                                   Object searchValue ) {
        JSONObject response
        if (additionalParms.size() == 0)
            // by default search the Assays AND Compound index
            response = elasticSearchQuery(additionalParms, BardQueryType.Xcompound, searchValue, BardQueryType.Default )
        else {
            // Allow the specified search index. This should be useful when people start searching "as target" or whatever else...
            if (additionalParms.containsKey("searchIndex")) {
                def requestedSearchIndex =  additionalParms["searchIndex"]
                if (requestedSearchIndex instanceof BardQueryType ) {
                    BardQueryType bardQueryType =  requestedSearchIndex as BardQueryType
                    response =   elasticSearchQuery(additionalParms,bardQueryType, searchValue, BardQueryType.Default )
                 }

            }
            else
                response =   elasticSearchQuery(additionalParms,requestedSearchIndex as BardQueryType, searchValue, BardQueryType.Default )
        }
        JSONArray hits = response?.hits?.hits ?: [] as JSONArray
        List<ESAssay> assays = []
        List<ESCompound> compounds = []
        List<ESXCompound> xcompounds = []
        final Map<TargetAccessionNumber, Set<String>> accessionNumberToAssayIds = [:]

        for (JSONObject hit in hits) {
            if (hit._type == assayIndexTypeName) {
                aggregateAccessionNumbersToAssayNumbers(hit, accessionNumberToAssayIds)
                ESAssay esAssay = new ESAssay(hit)
                assays.add(esAssay)
            }
            else if (hit._type == elasticXCompoundType) {
                 xcompounds.add( new ESXCompound(hit) )
            }
        }

        return ["assays":assays, "compounds":compounds, "xcompounds": xcompounds, "compoundHeaderInfo": accessionNumberToAssayIds]

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
        return "${elasticXCompoundIndex},${assayIndexName}"
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


    /**
     * Merge together the elements in a list of lists. Perform the merging with sets,
     * but change everything back to a list on the way out.
     * @param lists
     * @return
     */
    static List<Integer> mergeLists( List< List< Integer>> lists) {
        Set<Integer>  first   = new LinkedHashSet(0)
        if (lists.size() > 0) {

            first = lists.first() as Set
            if ((lists.size()>1)) {

                for(  List< Integer> aList in (lists.drop(1) ) ) {
                    first+= aList as Set
                    first.flatten()
                }
            }
        }
        return first as List
    }


    @Override
    String toString() {
        return this.cid

    }
}