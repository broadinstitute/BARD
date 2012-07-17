package elasticsearch

import elasticsearchplugin.ElasticSearchService
import grails.plugin.spock.IntegrationSpec
import wslite.json.JSONObject

/*
 *
 */
class ElasticSearchServiceIntegrationSpec extends IntegrationSpec {

    //This is the service we are here to test
    ElasticSearchService  elasticSearchService

    String elasticSearchRoot
    String elasticAssayIndex
    String elasticCompoundIndex
    String elasticSearchRequester


    void setup() {
        elasticSearchRoot =  "http://bard-dev-vm:9200"
        elasticAssayIndex =  "/assays"
        elasticCompoundIndex =  "/compounds"
        elasticSearchRequester  =  "/_search"
    }

    void tearDown() {
        // Tear down logic here
    }



    void testElasticSearch_SingleAID_AllQry_AssayIndex() {

        given:
        elasticSearchService != null
        String elasticNodeSpecifier =  elasticSearchRoot +   elasticAssayIndex +   elasticSearchRequester
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"174"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""


        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticNodeSpecifier,jsonObject)

        then:

        // demonstrate that something came back
        assert returnJson

        // the Json starts off the right way
        assert returnJson.containsKey("hits")

        // we need back a couple of different types
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")

        // now let's look at the real contents
        assert jsonHitObject["hits"].size()==2
        assert jsonHitObject["hits"][0]._type=="compound"
        assert jsonHitObject["hits"][1]._type=="assay"

    }






    void testElasticSearch_AllQry_AssayIndex() {

        given:
        elasticSearchService != null
        String elasticNodeSpecifier =  elasticSearchRoot +   elasticAssayIndex +   elasticSearchRequester
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"644"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""


        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticNodeSpecifier,jsonObject)

        then:

        // demonstrate that something came back
        assert returnJson

        // the Json starts off the right way
        assert returnJson.containsKey("hits")

        // we need back a couple of different types
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")

        // now let's look at the real contents
        assert jsonHitObject["hits"].size()==2
        assert jsonHitObject["hits"][0]._type=="compound"
        assert jsonHitObject["hits"][1]._type=="assay"

    }





    void testElasticSearch_SingleCID_AllQry_AssayIndex() {

        given:
        elasticSearchService != null
        String elasticNodeSpecifier =  elasticSearchRoot +   elasticCompoundIndex +   elasticSearchRequester
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"174"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""


        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticNodeSpecifier,jsonObject)

        then:

        // demonstrate that something came back
        assert returnJson

        // the Json starts off the right way
        assert returnJson.containsKey("hits")

        // we need back a couple of different types
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
//
//        // now let's look at the real contents
        assert jsonHitObject["hits"].size()==1
        assert jsonHitObject["hits"][0]._type=="compound"
        assert jsonHitObject["hits"][0]._id=="174"

    }





    void testElasticSearch_MultipleCID_AllQry_CompoundsIndex() {

        given:
        elasticSearchService != null
        String elasticNodeSpecifier =  elasticSearchRoot +   elasticCompoundIndex +   elasticSearchRequester
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"174 3237916"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""


        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticNodeSpecifier,jsonObject)

        then:

        // demonstrate that something came back
        assert returnJson

        // we need back a couple of different types
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")

        // now let's look at the real contents
        assert jsonHitObject["hits"].size() > 0

    }




}

