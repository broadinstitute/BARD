package elasticsearchplugin

import grails.test.mixin.TestFor
import spock.lang.Specification
import grails.converters.JSON

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(ElasticSearchService)
class ElasticSearchServiceSpec extends Specification {

    QueryExecutorService queryExecutorService

    void setup() {
        queryExecutorService = Mock()
        service.queryExecutorService = queryExecutorService
        service.grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl = 'httpMock://'
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test search #label"() {

        when:
        Integer response = service.search(searchTerm)

        then:
        1 * queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response == expectedResult

        where:
        label                        | searchTerm       | assayJson                                                                                                                                                                                                                                                                                                                                                                                               | expectedResult
//        "for AID"                    | "644"            | JSON.parse('{"hits": {"hits": [{"_index": "assays", "_type": "compound", "_id": 644, "_source": {}} {"_index": "assays", "_type": "assay", "_id": 644, "_source": {"aid": 644, "title": "Kinetic mechanism", "targets": [{"resourcePath": "/bard/rest/v1/targets/accession/O75116", "taxId": 9606, "acc": "O75116"}]}}]}}') | [assays: [], compounds: []]
        "for AID"                    | "644"            | JSON.parse('{"hits": {"hits": [{"_index": "assays", "_type": "compound", "_id": 644 }]}}') | [assays: [], compounds: []]
    }
}
