import wslite.rest.RESTClient
import bardelasticsearch.RestClientFactoryService

//get the service from the context
elasticSearchCompoundsIndexService = ctx.getBean("elasticSearchCompoundsIndexService")
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

   