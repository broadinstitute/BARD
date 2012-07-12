package bardelasticsearch

import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.domain.DomainClassUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll
import wslite.http.HTTPClient
import wslite.http.auth.HTTPBasicAuthorization
import wslite.rest.RESTClient
import wslite.rest.Response

/**
 *
 */
@Unroll
@TestFor(ElasticSearchAssayIndexService)
@TestMixin(DomainClassUnitTestMixin)
class ElasticSearchAssayIndexServiceUnitSpec extends Specification {
    RESTClient restClient = Mock(RESTClient.class)
    HTTPClient httpClient = Mock(HTTPClient.class)
    HTTPBasicAuthorization authorization = Mock(HTTPBasicAuthorization.class)
    RestClientFactoryService restClientFactoryService = Mock(RestClientFactoryService.class)

    void setup() {
        this.restClient.url = "http://localhost/"
        this.service.restClientFactoryService = this.restClientFactoryService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Put Request"() {
        Response response = Mock(Response.class)
        when:
        this.service.putRequest(restClient, "stuff")
        then:
        restClient.put(_ as Closure) >> response
    }

    void "test Post Request"() {
        Response response = Mock(Response.class)
        when:
        this.service.postRequest(restClient, "stuff")
        then:
        restClient.post(_ as Closure) >> response
    }

    void "test execute Get Request JSON"() {
        Response response = Mock(Response.class)
        when:
        this.service.executeGetRequestJSON(restClient)
        then:
        restClient.get() >> response
    }


    /**
     *
     */
    void "create Index And Mapping For Assay Type"() {

        Response response = Mock()
        this.service.elasticSearchURL = "http://localhost/"
        when:
        this.service.createIndexAndMappingForAssayType("index", "indexType")

        then:
        this.restClientFactoryService.createNewRestClient(_) >> restClient
        2 * this.restClient.put(_ as Closure) >> response
    }
}
