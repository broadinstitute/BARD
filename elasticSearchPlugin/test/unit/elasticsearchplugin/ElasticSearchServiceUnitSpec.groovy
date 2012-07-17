package elasticsearchplugin

import grails.test.mixin.TestFor
import spock.lang.Specification
import wslite.json.JSONObject

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(ElasticSearchService)
class ElasticSearchServiceUnitSpec extends Specification {

    QueryExecutorService queryExecutorService

    final static jsonAssayResponseString = '''{"hits":
        {"hits": [
            {"_index": "assays", "_type": "compound", "_id": 644, "_source": {"cids": [1234, 5678]}},
            {"_index": "assays", "_type": "assay", "_id": 644, "_source":
                {"aid": 644, "title": "Kinetic mechanism", "targets": [
                    {"resourcePath": "/bard/rest/v1/targets/accession/O75116", "taxId": 9606, "acc": "O75116"}]
                }
            }]
        }
    }'''


    void setup() {
        queryExecutorService = Mock(QueryExecutorService)
        service.queryExecutorService = queryExecutorService
        service.elasticSearchBaseUrl = 'http://mockMe'
        service.assayIndexName = 'assays'
        service.assayIndexTypeName = 'assay'
        service.compoundIndexName = 'compounds'
        service.compoundIndexTypeName = 'compound'
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test search #label"() {

        when:
        Map<String, List> response = service.search(searchTerm, 999999)
        println()

        then:
        queryExecutorService.postRequest(_, _) >> { assayJson }

        assert response.assays.size() == expectedAssayCount
        assert response.compounds.size() == expectedCompoundCount

        where:
        label     | searchTerm | assayJson                               | expectedAssayCount | expectedCompoundCount
        "for AID" | "644"      | new JSONObject(jsonAssayResponseString) | 1                  | 2
        "for AID" | "644"      | new JSONObject()                        | 0                  | 0
    }

    void "test getAssayDocument #label"() {

        when:
        JSONObject response = service.getAssayDocument(assayId)
        println()

        then:
        queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response.keySet().size() == expectedKeySetSize

        where:
        label     | assayId        | assayJson                          | expectedKeySetSize
        "for AID" | 644 as Integer | new JSONObject('{"key": "value"}') | 1
        "for AID" | 644 as Integer | new JSONObject()                   | 0
    }

    void "test getAssayDocumentWithCID #label"() {

        when:
        JSONObject response = service.getAssayDocument(assayId)
        println()

        then:
        queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response.keySet().size() == expectedKeySetSize

        where:
        label     | assayId        | assayJson                          | expectedKeySetSize
        "for AID" | 174 as Integer | new JSONObject('{"key": "value"}') | 1
        "for AID" | 174 as Integer | new JSONObject()                   | 0
    }

    void "test query String Query #label"() {
        when:
        final JSONObject response = service.searchQueryStringQuery(url, queryStringDSL)

        then:
        queryExecutorService.postRequest(_, _) >> { queryStringDSL }
        assert response.keySet().size() == expectedKeySetSize

        where:
        label                  | url                     | queryStringDSL           | expectedKeySetSize
        "Non Empty JSONObject" | "http://localhost:9200" | new JSONObject([a: "b"]) | 1
        "Empty JSONObject"     | "http://localhost:9200" | new JSONObject()         | 0
    }
}
