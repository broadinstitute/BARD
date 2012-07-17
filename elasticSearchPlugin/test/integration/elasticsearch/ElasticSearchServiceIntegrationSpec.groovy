package elasticsearch

import elasticsearchplugin.ElasticSearchService
import grails.plugin.spock.IntegrationSpec
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
class ElasticSearchServiceIntegrationSpec extends IntegrationSpec {

    ElasticSearchService  elasticSearchService


    void setup() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }


    void testElasticSearchServiceQry() {

        given:
        elasticSearchService != null
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"644"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""
        String elasticSearchNode =  "http://bard-dev-vm:9200/assays/_search"

        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticSearchNode,jsonObject)
//        File fk = new File("f.f")

        then:

//        fk << "returnJson.getClass().name=${returnJson.getClass().name}" +
//                "returnJson=${returnJson.toString()}"
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
}

  /*




/*        given:
elasticSearchService != null

when:
def foo ='bar'
then:
1==1
String elasticSearchNode =  "http://bard-dev-vm:9200/assays/_search"
String testStr = """ {
"fields": ["name"],
"query": {
"query_string": {
"default_field": "name",
"query": "644*"
}
},
"size": 10
}"""
final JSONObject jsonObject =  new JSONObject(testStr.toString())
def f = elasticSearchService.searchQueryStringQuery(elasticSearchNode,jsonObject)
println f.toString()toString
*/
