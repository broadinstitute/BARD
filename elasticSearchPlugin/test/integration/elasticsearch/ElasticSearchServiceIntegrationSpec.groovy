package elasticsearch

import elasticsearchplugin.ElasticSearchService
import elasticsearchplugin.BardQueryType
import grails.plugin.spock.IntegrationSpec
import wslite.json.JSONObject

/*
 *
 */
class ElasticSearchServiceIntegrationSpec extends IntegrationSpec {

    //This is the service we are here to test
    ElasticSearchService  elasticSearchService
    def grailsApplication

    void setup() {
    }

    void tearDown() {

    }




    void "test elasticSearchQuery to see it search the assay  index  for  a single CID"() {

        given:
        elasticSearchService != null


        when:
        final String cidQuerySpecifier = "174"
        def returnJson = elasticSearchService.elasticSearchQuery(BardQueryType.Assay,cidQuerySpecifier,BardQueryType.Compound)
        println returnJson

        then:
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()>0
        assert jsonHitObject["hits"][0]._type=="compound"

    }



    void "test elasticSearchQuery to handle the search  assay index CIDs"() {

        given:
        elasticSearchService != null


        when:
        final String cidQuerySpecifier = "174 3237916"
        def returnJson = elasticSearchService.elasticSearchQuery(BardQueryType.Assay,cidQuerySpecifier,BardQueryType.Compound)
        println returnJson

        then:
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()>10 // will be equal to max # per query for common compounds
        assert jsonHitObject["hits"][0]._type=="compound"

    }


    void "test elasticSearchQuery to search the assay index for a single AID"() {

        given:
        elasticSearchService != null


        when:
        final String cidQuerySpecifier = "644"
        def returnJson = elasticSearchService.elasticSearchQuery(BardQueryType.Assay,cidQuerySpecifier,BardQueryType.Assay)
        println returnJson

        then:
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()==1
        assert jsonHitObject["hits"][0]._type=="assay"

    }



    void "test elasticSearchQuery to search the assay index for a multiple AIDs"() {

        given:
        elasticSearchService != null


        when:
        final String cidQuerySpecifier = "644 643 647"
        def returnJson = elasticSearchService.elasticSearchQuery(BardQueryType.Assay,cidQuerySpecifier,BardQueryType.Assay)
        println returnJson

        then:
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()==3
        assert jsonHitObject["hits"][0]._type=="assay"

    }


    void "test elasticSearchQuery to search  compound index for a single  CID"() {

        given:
        elasticSearchService != null


        when:
        final String cidQuerySpecifier ="174"
        def returnJson = elasticSearchService.elasticSearchQuery(BardQueryType.Compound,cidQuerySpecifier,BardQueryType.Compound)
        println returnJson

        then:
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()==1
        assert jsonHitObject["hits"][0]._type=="compound"

    }







    void "test elasticSearchQuery to search  compound index for a multiple  CIDs"() {

        given:
        elasticSearchService != null


        when:
        def  cidQuerySpecifier =  [174, 3237916]

        def returnJson = elasticSearchService.elasticSearchQuery(BardQueryType.Compound,cidQuerySpecifier,BardQueryType.Compound)
        println returnJson

        then:
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()==2
        assert jsonHitObject["hits"][0]._type=="compound"

    }






    void "test searchQueryStringQuery  to see it pull back  everything  in the assay index which  matches an AID"() {

        given:
        elasticSearchService != null
        String elasticNodeSpecifier =  grailsApplication.config.elasticSearchService.restNode.baseUrl +
                                       grailsApplication.config.elasticSearchService.restNode.elasticAssayIndex +
                                       grailsApplication.config.elasticSearchService.restNode.elasticSearchRequester
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"644"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""


        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticNodeSpecifier,jsonObject)

        then:

        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()==2
        assert jsonHitObject["hits"][0]._type=="compound"
        assert jsonHitObject["hits"][1]._type=="assay"

    }





    void "test searchQueryStringQuery and pull back  everything  in the compound index a matching a single CID"() {

        given:
        elasticSearchService != null
        String elasticNodeSpecifier =  grailsApplication.config.elasticSearchService.restNode.baseUrl +
                                       grailsApplication.config.elasticSearchService.restNode.elasticCompoundIndex +
                                       grailsApplication.config.elasticSearchService.restNode.elasticSearchRequester
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"174"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""


        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticNodeSpecifier,jsonObject)

        then:

        // demonstrate that something came back
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()==1
        assert jsonHitObject["hits"][0]._type=="compound"
        assert jsonHitObject["hits"][0]._id=="174"

    }





    void "test searchQueryStringQuery and pull back  everything  in the compound  index matching a pair of CIDs"() {

        given:
        elasticSearchService != null
        String elasticNodeSpecifier =  grailsApplication.config.elasticSearchService.restNode.baseUrl +
                                       grailsApplication.config.elasticSearchService.restNode.elasticCompoundIndex +
                                       grailsApplication.config.elasticSearchService.restNode.elasticSearchRequester
        String testStr = """
{"query":{"bool":{"must":[{"query_string":{"default_field":"_all","query":"174 3237916"}}],"must_not":[],"should":[]}},"from":0,"size":50,"sort":[],"facets":{}}"""


        when:

        final JSONObject jsonObject =  new JSONObject(testStr.toString())
        def returnJson =   elasticSearchService.searchQueryStringQuery(elasticNodeSpecifier,jsonObject)

        then:

        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size() > 0

    }




}

