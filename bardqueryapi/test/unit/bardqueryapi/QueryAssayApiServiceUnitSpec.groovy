package bardqueryapi

import static org.junit.Assert.*



import grails.converters.JSON
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(QueryAssayApiService)
class QueryAssayApiServiceUnitSpec extends Specification {

    QueryExecutorService queryExecutorService


    void setup() {
        queryExecutorService = Mock()
        service.queryExecutorService = queryExecutorService
        service.grailsApplication.config.bard.services.elasticSearchService.restNode.baseUrl = 'httpMock://'
    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     */
    void "test getTotalAssayCompounds #label"() {

        when:
        Integer response = service.getTotalAssayCompounds(assayId)

        then:
        1 * queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response == expectedCompounds

        where:
        label                        | assayId          | assayJson                                           | expectedCompounds
        "Return 1 compound"          | new Integer(872) | JSON.parse('{"compounds": 10}')                     | new Integer(10)
        "Return error (0 compounds)" | new Integer(872) | ["errorMessage": "error", "errorCode": 404] as JSON | new Integer(0)
    }

    /**
     */
    void "test getAssayCompoundsResultset #label"() {

        when:
        List<String> response = service.getAssayCompoundsResultset(max, offset, assayId)

        then:
        1 * queryExecutorService.executeGetRequestJSON(_, _) >> { assayJson }

        assert response == expectedCompoundList

        where:
        label                        | assayId          | max | offset | assayJson                                                          | expectedCompoundList
        "Return 1 compound"          | new Integer(872) | 10  | 2      | JSON.parse('{"collection": ["/bard/rest/v1/compounds/661090"]}')   | ['661090']
        "Return error (0 compounds)" | new Integer(872) | 10  | 2      | JSON.parse('{"noCollection": ["/bard/rest/v1/compounds/661090"]}') | []
    }
}
