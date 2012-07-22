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
String indexName = "invertedassays"
String indexType = "compond"

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

//construct url to NCGC to fetch compounds
final String ncgcCompoundsURL = "${ncgcRootURL}${ncgcRelativeURL}compounds/"

//url to root of elasticsearch server
final String elasticSearchURL = "http://bard-dev-vm:9200/" //elasticSearchCompoundsIndexService.elasticSearchURL
assert elasticSearchURL

//get a RESTClient using the url to the elasticsearch assays index
RESTClient cloneRestClient = restClientFactoryService.createNewRestClient("${elasticSearchURL}${indexName}")


// get a list of cids from elsatic search
List<String> cids
try {
 cids = elasticSearchCompoundsIndexService.fetchAllUniqueCIDsFromElasticSearch()
} catch (Exception eee) {
    println eee.message
}

println "fetched ${cids.size()} cids.  Now process them one at a time."

//File f = new File("e:/Temp/compounds.txt")
//for (String cid in cids) {
//  f<<"${cid}\n"
//}
File f = new File("e:/Temp/error.txt")
//File f = new File("e:/Temp/compounds.txt")

// now cycle over every compound
for (String cid in cids) {
   if (!("0".equals(cid.trim()) ))
   try {
      // compose a URL and query ncgc
      cloneRestClient.url = ncgcCompoundsURL+cid+"/exptdata"
      wslite.json.JSONObject jsonObject = elasticSearchCompoundsIndexService.executeGetRequestJSON(cloneRestClient)
      assert jsonObject
      
      // take apart the respose to get the assay ids
      wslite.json.JSONArray jsonArray=jsonObject["collection"]
      def combinedAids = new  StringBuffer()
      for (def exptURL in jsonArray) {
//      def exptNumber = exptURL.toString().replaceAll('/bard/rest/v1/exptdata/', '')
         combinedAids <<   exptURL.toString().replaceAll('/bard/rest/v1/exptdata/', '') + ","
      }
      // remove last character ( a comma)
     combinedAids = combinedAids.substring(0,combinedAids.size()-1)
     
      // insert into elastic search index
      final String cidJson = "{cid: ${cid}, apids:[${combinedAids.toString()}]}"
      final  wslite.json.JSONObject requestToSend = new wslite.json.JSONObject(cidJson)
                
      cloneRestClient.url = "${elasticSearchURL}${indexName}/${indexType}"
      elasticSearchCompoundsIndexService.postRequest( cloneRestClient,requestToSend.toString() )
  
    } catch (Exception ee) {
       ee.printStackTrace()
       println ee.message
       f << "failed on ${cid}\n"
   }   
}

// finally, send a refresh so that everything is ready to go
try{
   cloneRestClient.url = "${elasticSearchURL}${indexName}/_refresh"
   elasticSearchCompoundsIndexService.postRequest( cloneRestClient,"" )

} catch (Exception ee) {
    ee.printStackTrace()
    println ee.message
}   

