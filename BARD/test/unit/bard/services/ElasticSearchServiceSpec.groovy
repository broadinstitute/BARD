package bard.services

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import spock.lang.Specification
import spock.lang.Unroll
import grails.converters.JSON

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(ElasticSearchService)
@Unroll
class ElasticSearchServiceSpec extends Specification {

    QueryExecutorService queryExecutorService

    void setup() {
        queryExecutorService = Mock()
        service.queryExecutorService = queryExecutorService
        service.grailsApplication.config.ncgc.server.root.url = 'httpMock://'
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test searchForAssay #label"() {

//        String url = "httpMock:///assays/assay/${assayId}"

        when:
        def response = service.searchForAssay(assayId)

        then:
        1 * queryExecutorService.executeGetRequestJSON(_, _) >> { expectedJson }

        assert response == expectedJson



        where:
        label                 | assayId        | expectedJson
        'search for assay id' | 123 as Integer | ['key': 'value'] as JSON
    }
}
