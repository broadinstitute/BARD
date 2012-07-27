package elasticsearch

import elasticsearchplugin.BardQueryType
import elasticsearchplugin.ESAssay
import elasticsearchplugin.ESXCompound
import elasticsearchplugin.ElasticSearchService
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
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



    @Unroll("Use new search")
    def "test simplified elasticSearchQuery call"() {

        given:
        elasticSearchService != null


        when:
        def  cidQuerySpecifier =  "174 881181"

        def mapOfObjects = elasticSearchService.elasticSearchQuery(searchIndex:BardQueryType.Xcompound,cidQuerySpecifier)
        List<ESXCompound> listOfESXCompounds =  mapOfObjects."xcompounds"


        then:
        assert listOfESXCompounds.size()==2
        def apisList = ESXCompound.combinedApids(listOfESXCompounds)
        assert  apisList.size() > 300
    }



    @Unroll("Use new search")
    def "test assorted apid and cid parms elasticSearchQuery call"() {

        given:
        elasticSearchService != null


        when:
        def  cidQuerySpecifier =  "174 644"

        def mapOfObjects = elasticSearchService.elasticSearchQuery(searchIndex:BardQueryType.Xcompound,cidQuerySpecifier)
        List<ESXCompound> listOfESXCompounds =  mapOfObjects."xcompounds"
        List<ESAssay> listOfESXAssays =  mapOfObjects."assays"


        then:
        assert listOfESXCompounds.size()>10
        def apisList = ESXCompound.combinedApids(listOfESXCompounds)
        assert  apisList.size() > 200
        assert  listOfESXAssays.size()>10
    }



    @Unroll("Use new search")
    void "test simplified elasticSearchQuery with everybodies favorite 644 search"() {

        given:
        elasticSearchService != null


        when:
        def  cidQuerySpecifier =  "644"

        def returnMap = elasticSearchService.elasticSearchQuery(cidQuerySpecifier)

        then:
        assert returnMap.assays.size()==1
        assert returnMap.xcompounds.size()==206

    }

    @Unroll("Use new search")
    void "test  elasticSearchQuery with additional parameters but no searchIndex"() {

        given:
        elasticSearchService != null


        when:
        def  cidQuerySpecifier =  "644"

        def returnMap = elasticSearchService.elasticSearchQuery(cidQuerySpecifier, unkownparameter: 20)

        then:
        assert returnMap.assays.size()==1
        assert returnMap.xcompounds.size()==206

    }


    @Unroll("Use new search")
    void "test  elasticSearchQuery with from and size parameters"() {

        given:
        elasticSearchService != null


        when:
        def  cidQuerySpecifier =  "644"

        def returnMap = elasticSearchService.elasticSearchQuery(cidQuerySpecifier, from:  0, size: 20)

        then:
        assert ((returnMap.assays.size()+returnMap.xcompounds.size()) == 20)
    }




    @Unroll("Use new search")
    void "test elasticSearchQuery to search everything"() {

        given:
        elasticSearchService != null


        when:
        def  cidQuerySpecifier =  "174"

        def returnJson = elasticSearchService.elasticSearchQuery(BardQueryType.Assay,cidQuerySpecifier,BardQueryType.Default)

        then:
        assert returnJson
        assert returnJson.containsKey("hits")
        JSONObject jsonHitObject = returnJson["hits"]
        assert jsonHitObject.containsKey("total")
        assert jsonHitObject["hits"].size()>10

    }




    @Unroll("Use new search")
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

    }




    @Unroll("Use new search")
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
        assert jsonHitObject["hits"][0]._type=="xcompound"

    }




    @Unroll("Use new search")
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

