import wslite.json.JSONArray
import wslite.rest.RESTClient
import bardelasticsearch.ElasticSearchCompoundsIndexService
import bardelasticsearch.RestClientFactoryService
/**
 * Used for reindexing aids that failed during the run of the
 * indexAssayCompounds script.
 * Perhaps we could put the logic here into that script and use a switch statement to run
 * either script based on user input.
 */

//get the service from the context
ElasticSearchCompoundsIndexService elasticSearchCompoundsIndexService = ctx.getBean("elasticSearchCompoundsIndexService")
assert elasticSearchCompoundsIndexService

RestClientFactoryService restClientFactoryService = ctx.getBean("restClientFactoryService")
assert restClientFactoryService

//the name of the elasticsearch index for assays
String indexName = "assays"

//url to root of NCGC REST API
final String ncgcRootURL = elasticSearchCompoundsIndexService.ncgcRootURL
assert ncgcRootURL
//the relative url of the NCGC REST API services - /bard/rest/v1/
final String ncgcRelativeURL = elasticSearchCompoundsIndexService.ncgcRelativeURL
assert ncgcRelativeURL
//url to NCGC to fetch experiments
final String ncgcExperimentsURL = "${ncgcRootURL}${ncgcRelativeURL}experiments/"

//construct url to NCGC to fetch assays
final String ncgcAssaysURL = "${ncgcRootURL}${ncgcRelativeURL}assays/"

//url to root of elasticsearch server
final String elasticSearchURL = elasticSearchCompoundsIndexService.elasticSearchURL
assert elasticSearchURL

//get a RESTClient using the url to the elasticsearch assays index
RESTClient cloneRestClient = restClientFactoryService.createNewRestClient("${elasticSearchURL}${indexName}")

//create the index if it does not already exist
try {
    elasticSearchCompoundsIndexService.putRequest(cloneRestClient, "")
} catch (Exception eee) {
    println eee.message
}

try {
    //reset the rest client to all NCGC assays
    cloneRestClient.url = ncgcAssaysURL
    //lets fetch all of the aids from NCGC
    def jsonArray = elasticSearchCompoundsIndexService.executeGetRequestJSON(cloneRestClient)
    assert jsonArray instanceof JSONArray
    //start indexing concurrently
    elasticSearchCompoundsIndexService.startIndexingAssayCompoundsConcurrently(ncgcExperimentsURL, jsonArray)
} catch (Exception ee) {
    ee.printStackTrace()
    println ee.message
}
final Set<String> aidsWhoseCompoundsFailedIndexing = elasticSearchCompoundsIndexService.assayCompoundsFailedIndexing
if(aidsWhoseCompoundsFailedIndexing){
    println "Please reindex the following aids ${aidsWhoseCompoundsFailedIndexing.toString()} using the reIndexFailedAssayCompounds.groovy script"
}
//Construct a JSONArray from the List and reindex