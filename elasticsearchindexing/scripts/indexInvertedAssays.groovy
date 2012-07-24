
import wslite.json.JSONArray
import wslite.rest.RESTClient
//import bardelasticsearch.ElasticSearchCompoundsIndexService
import bardelasticsearch.ElasticSearchCompoundsIndexService
import bardelasticsearch.ElasticSearchCompoundIndexService
import bardelasticsearch.RestClientFactoryService
/**
 * Used for reindexing aids that failed during the run of the
 * indexAssayCompounds script.
 * Perhaps we could put the logic here into that script and use a switch statement to run
 * either script based on user input.
 */

//get the service from the context
ElasticSearchCompoundIndexService elasticSearchCompoundIndexService = ctx.getBean("elasticSearchCompoundIndexService")
assert elasticSearchCompoundIndexService

RestClientFactoryService restClientFactoryService = ctx.getBean("restClientFactoryService")
assert restClientFactoryService

//the name of the elasticsearch index for assays
String indexName = "compound"
String indexType = "xcompound"

//url to root of NCGC REST API
final String ncgcRootURL = elasticSearchCompoundIndexService.ncgcRootURL
assert ncgcRootURL
//the relative url of the NCGC REST API services - /bard/rest/v1/
final String ncgcRelativeURL = elasticSearchCompoundIndexService.ncgcRelativeURL
assert ncgcRelativeURL
//url to NCGC to fetch experiments
final String ncgcExperimentsURL = "${ncgcRootURL}${ncgcRelativeURL}experiments/"
final String ncgcAssaysURL = "${ncgcRootURL}${ncgcRelativeURL}assays/"
final String ncgcCompoundsURL = "${ncgcRootURL}${ncgcRelativeURL}compounds/"

//url to root of elasticsearch server
final String elasticSearchURL = "http://bard-dev-vm:9200/" //elasticSearchCompoundsIndexService.elasticSearchURL
assert elasticSearchURL

//elasticSearchAssayIndexService.createIndexAndMappingForAssayType(indexName, indexType)

//get a RESTClient using the url to the elasticsearch assays index
RESTClient cloneRestClient = restClientFactoryService.createNewRestClient("${elasticSearchURL}${indexName}")


// get a list of cids from elsatic search
/*List<String> cids
try {
    cids = elasticSearchCompoundIndexService.fetchAllUniqueCIDsFromElasticSearch()
} catch (Exception eee) {
    println eee.message
}

println "fetched ${cids.size()} cids.  Now process them one at a time."
*/
//File f = new File("c:/Temp/compounds.txt")
//for (String cid in cids) {
//    f<<"${cid}\n"
//}
//f.flush()
//File f = new File("e:/Temp/error.txt")







File errorfile = new File("c:/Temp/err.txt")
File infile = new File("c:/Temp/cmpd1.txt")

println "processing the file = ${infile.name}"





// now cycle over every compound
infile.eachLine{cid->
//for (String cid in cids) {


    if (!("0".equals(cid.trim()) ))
        try {
            // compose a URL and query ncgc for assay IDS
            cloneRestClient.url = ncgcCompoundsURL+cid+"/experiments"
            def combinedAids = new  StringBuffer()
            boolean stillMoreToDo=true
            while (stillMoreToDo) {
                wslite.json.JSONObject jsonObject = elasticSearchCompoundIndexService.executeGetRequestJSON(cloneRestClient)
                assert jsonObject
                // take apart the respose to get the assay ids
                wslite.json.JSONArray jsonArray=jsonObject."collection"
                for (def exptURL in jsonArray) {
                    combinedAids <<   exptURL.toString().replaceAll('/bard/rest/v1/experiments/', '') + ","
                }
                // do we have more?
                def link=jsonObject."link"
                stillMoreToDo = ((!wslite.json.JSONObject.NULL.equals(link)) )
                if (stillMoreToDo) {
                    cloneRestClient.url = "${ncgcRootURL.substring(0,ncgcRootURL.size()-1)}${link.toString()}"
                }
            }
            // remove last character ( a comma)
            combinedAids = combinedAids.substring(0,combinedAids.size()-1)

            // compose a URL and query ncgc for compounds specific
            cloneRestClient.url = ncgcCompoundsURL+cid
            jsonObject = elasticSearchCompoundIndexService.executeGetRequestJSON(cloneRestClient)
            assert jsonObject
            // take apart the respose to get the assay ids
            wslite.json.JSONArray sidsArray=jsonObject."sids"
            def probeId=jsonObject."probeId"
            def url=jsonObject."url"
            def smiles=jsonObject."smiles"?.toString()?.replace("\\", "\\\\")
            // insert into elastic search index

            final String cidJson = "{smiles: \"${smiles.toString()}\",url: ${url.toString()}, apids:[${combinedAids.toString()}],sids:[${sidsArray.toString()}], probeId:${probeId.toString ()},cid: ${cid}}"
            cloneRestClient.url = "${elasticSearchURL}${indexName}/${indexType}"
            elasticSearchCompoundIndexService.postRequest( cloneRestClient,cidJson.toString() )

        } catch (Exception ee) {
            ee.printStackTrace()
            println ee.message
            errorfile << "${cid}\n"
        }
}
// finally, send a refresh so that everything is ready to go

try{
    cloneRestClient.url = "${elasticSearchURL}${indexName}/_refresh"
    elasticSearchCompoundIndexService.postRequest( cloneRestClient,"" )

} catch (Exception ee) {
    ee.printStackTrace()
    println ee.message
    errorfile << "failed on refresh\n"
}

