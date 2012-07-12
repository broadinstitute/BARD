package bardelasticsearch

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*
import wslite.rest.RESTClient
import wslite.http.HTTPClient
import wslite.http.auth.HTTPBasicAuthorization
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(RestClientFactoryService)
@TestMixin(GrailsUnitTestMixin)
class RestClientFactoryServiceSpec extends  Specification{
    RESTClient restClient = Mock(RESTClient.class)
    HTTPClient httpClient = Mock(HTTPClient.class)
    HTTPBasicAuthorization authorization = Mock(HTTPBasicAuthorization.class)

    void setup() {
        this.service.restClient = restClient
    }

    void tearDown() {
        // Tear down logic here
    }


    void "test Create New Rest Client"() {

        final String newURL = "Stuff"
        when:
        final RESTClient clonedClient = this.service.createNewRestClient(newURL)
        then:
        restClient.httpClient >> httpClient
        restClient.httpClient.authorization >> authorization
        assert clonedClient
        assert clonedClient.url == newURL
    }

}
