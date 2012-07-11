package elasticsearchplugin

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject

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
        queryExecutorService = Mock()
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
        JSONObject response = service.search(searchTerm, 999999)

        then:
        1 * queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response.assays.size == expectedAssayCount
        assert response.compounds.size == expectedCompoundCount

        where:
        label     | searchTerm | assayJson                           | expectedAssayCount | expectedCompoundCount
        "for AID" | "644"      | JSON.parse(jsonAssayResponseString) | 1                  | 2
        "for AID" | "644"      | JSON.parse('{}')                    | 0                  | 0
    }

    void "test getAssayDocument #label"() {

        when:
        JSONObject response = service.getAssayDocument(assayId)
        println()

        then:
        1 * queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response.keySet().size() == expectedKeySetSize

        where:
        label     | assayId        | assayJson                      | expectedKeySetSize
        "for AID" | 644 as Integer | JSON.parse('{"key": "value"}') | 1
        "for AID" | 644 as Integer | JSON.parse('{}')               | 0
    }
}
