package bardelasticsearch

import org.junit.After
import org.junit.Before
import org.junit.Test
import wslite.json.JSONArray
import wslite.rest.RESTClient

class ElasticSearchAssayIndexServiceTests {
    ElasticSearchCompoundsIndexService elasticSearchCompoundsIndexService
    def grailsApplication

    @Before
    void setUp() {
        // Setup logic here
    }

    @After
    void tearDown() {
        // Tear down logic here
    }
    @Test
    void testCompoundIndexing(){

        final String elasticSearchURL = elasticSearchCompoundsIndexService.elasticSearchURL
        final String indexName = "compounds"
        //create the index if it does not already exist
        final RESTClient cloneRestClient = elasticSearchCompoundsIndexService.cloneRestClient("${elasticSearchURL}${indexName}")
        //create the index
        elasticSearchCompoundsIndexService.putRequest(cloneRestClient, "")


        final String ncgcRootURL = elasticSearchCompoundsIndexService.ncgcRootURL
        final String ncgcRelativeURL = elasticSearchCompoundsIndexService.ncgcRelativeURL
        final String ncgcCompoundsURL = "${ncgcRootURL}${ncgcRelativeURL}compounds/"
        elasticSearchCompoundsIndexService.startIndexingCompounds(ncgcCompoundsURL)
    }
    @Test
    void testAssayCompoundsIndexing() {
        String indexName = "assays"
        //url to NCGC to fetch compounds
        final String ncgcRootURL = elasticSearchCompoundsIndexService.ncgcRootURL
        final String ncgcRelativeURL = elasticSearchCompoundsIndexService.ncgcRelativeURL


        //url to NCGC to fetch experiments
        final String ncgcExperimentsURL = "${ncgcRootURL}${ncgcRelativeURL}experiments/"
        println ncgcExperimentsURL
        //url to NCGC to fetch assays
        final String ncgcAssaysURL = "${ncgcRootURL}${ncgcRelativeURL}assays/"
        final String elasticSearchURL = elasticSearchCompoundsIndexService.elasticSearchURL

        //create the index if it does not already exist
        RESTClient cloneRestClient = elasticSearchCompoundsIndexService.cloneRestClient("${elasticSearchURL}${indexName}")
        //create the index
        elasticSearchCompoundsIndexService.putRequest(cloneRestClient, "")

        try {
            cloneRestClient = elasticSearchCompoundsIndexService.cloneRestClient(ncgcAssaysURL)
            //lets fetch all of the aids from NCGC
            def jsonArray = elasticSearchCompoundsIndexService.executeGetRequestJSON(cloneRestClient)
            //start indexing concurrently
            elasticSearchCompoundsIndexService.startIndexingAssayCompoundsConcurrently(ncgcExperimentsURL, jsonArray)

        } catch (Exception ee) {
            ee.printStackTrace()
            println ee.message
        }
    }
}
