import bardelasticsearch.ElasticSearchCompoundsIndexService
import bardelasticsearch.RestClientFactoryService
import wslite.rest.RESTClient

/***
 * Index all compounds stored in NCGC
 * This same data can be obtained from PubChem so perhaps we do not need NCGC for this at all
 *
 * Any compounds that could not be indexed, as a result of errors is logged into
 * the failedIndexing list and printed out at the end of execution
 * Copy the list of failed compounds, and use the indexExtraCompounds script to re-execute them
 *
 */
//get the service from the context
final ElasticSearchCompoundsIndexService elasticSearchCompoundsIndexService = ctx.getBean("elasticSearchCompoundsIndexService")
assert elasticSearchCompoundsIndexService

RestClientFactoryService restClientFactoryService = ctx.getBean("restClientFactoryService")
assert restClientFactoryService

//the root url to elasticsearch-http://localhost:9200/
final String elasticSearchURL = elasticSearchCompoundsIndexService.elasticSearchURL
assert elasticSearchURL

//index name of compounds in elasticsearch
final String indexName = "compounds"

//construct url to create the index
final RESTClient cloneRestClient = restClientFactoryService.createNewRestClient("${elasticSearchURL}${indexName}")
//create the index if it does not already exist
elasticSearchCompoundsIndexService.putRequest(cloneRestClient, "")

//the root url of the NCGC server
final String ncgcRootURL = elasticSearchCompoundsIndexService.ncgcRootURL

//the relative url - /bard/rest/v1/
final String ncgcRelativeURL = elasticSearchCompoundsIndexService.ncgcRelativeURL

//the url to the list of compounds, we will append the cid of each compound to it
final String ncgcCompoundsURL = "${ncgcRootURL}${ncgcRelativeURL}compounds/"

//we start indexing compounds concurrently
elasticSearchCompoundsIndexService.startIndexingCompounds(ncgcCompoundsURL)


final Set<String> failedIndexing = elasticSearchCompoundsIndexService.compoundsFailedIndexing

println failedIndexing.size() + " compounds, failed indexing"
if(!failedIndexing.isEmpty()){
    println "Use the indexExtraCompounds.groovy script to index the extra compounds"
}
println(failedIndexing.toString())


   