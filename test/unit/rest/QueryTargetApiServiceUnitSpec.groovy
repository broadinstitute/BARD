package rest

import grails.converters.JSON
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.QueryTargetApiService
import bardqueryapi.QueryExecutorInternalService

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

class QueryTargetApiServiceUnitSpec extends Specification {

    QueryTargetApiService queryTargetApiService

    QueryExecutorInternalService queryExecutorInternalService


    void setup() {
        queryExecutorInternalService = Mock()
        queryTargetApiService = new QueryTargetApiService("accessionUrl", "geneUrl")
        queryTargetApiService.queryExecutorInternalService = queryExecutorInternalService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Get Assays For Accession Target #label"() {
        given:
        final String accessionUrl = "P01112"

        when:
        final List<String> assays = queryTargetApiService.findAssaysForAccessionTarget(accessionUrl)

        then:
        1 * queryExecutorInternalService.executeGetRequestJSON(_, _) >> { assayJson }

        assert assays == expectedAssayList
        where:
        label                        | assayId              | max | offset | assayJson                                                | expectedAssayList
        "Return 1 assay"             | new Integer(872)     | 10  | 2      | JSON.parse('["/bard/rest/v1/targets/accession/661090"]') | ['661090']
        "Return 0 assays"            | new Integer(872)     | 10  | 2      | JSON.parse('[]')                                         | []
        "Return error (0 compounds)" | new Integer(1234567) | 10  | 2      | ["errorMessage": "error", "errorCode": 404] as JSON      | []
    }


}
