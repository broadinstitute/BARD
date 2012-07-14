import bardelasticsearch.ElasticSearchCompoundsIndexService
import wslite.json.JSONArray

/**
 * This is for reindexing compounds associated with an aid
 * that could not be fully indexed by the indexAssayCompounds.groovy script
 *
 */
//get the service from the context
ElasticSearchCompoundsIndexService elasticSearchCompoundsIndexService = ctx.getBean("elasticSearchCompoundsIndexService")
assert elasticSearchCompoundsIndexService

//url to root of NCGC REST API
final String ncgcRootURL = elasticSearchCompoundsIndexService.ncgcRootURL
assert ncgcRootURL
//the relative url of the NCGC REST API services - /bard/rest/v1/
final String ncgcRelativeURL = elasticSearchCompoundsIndexService.ncgcRelativeURL
assert ncgcRelativeURL
//url to NCGC to fetch experiments
final String ncgcExperimentsURL = "${ncgcRootURL}${ncgcRelativeURL}experiments/"

final List<String>  failedIndexing = []


try {
    //Construct a JSONArray from the List and reindex
    final JSONArray jsonArray =new JSONArray(failedIndexing)
    //start indexing concurrently
    elasticSearchCompoundsIndexService.startIndexingAssayCompoundsConcurrently(ncgcExperimentsURL, jsonArray)
} catch (Exception ee) {
    ee.printStackTrace()
    println ee.message
}
final Set<String> aidsWhoseCompoundsFailedIndexing = elasticSearchCompoundsIndexService.assayCompoundsFailedIndexing
if (aidsWhoseCompoundsFailedIndexing) {
    println "Please reindex the following aids ${aidsWhoseCompoundsFailedIndexing.toString()} using the current script"
}
