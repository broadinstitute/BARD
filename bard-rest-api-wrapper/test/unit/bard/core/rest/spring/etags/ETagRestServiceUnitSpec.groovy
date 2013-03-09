package bard.core.rest.spring.etags

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.AssayRestService
import grails.test.mixin.TestFor
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.assays.*
import bard.core.rest.spring.ETagRestService
import org.springframework.web.client.HttpClientErrorException
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestClientException
import bard.core.exceptions.RestApiException

@Unroll
@TestFor(ETagRestService)
class ETagRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    LoggerService loggerService

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.baseUrl = "http://ncgc"
        this.loggerService = Mock(LoggerService)
        service.loggerService = this.loggerService
    }

    void "newCompositeETag"(){
        when:
        service.newCompositeETag("name", ["1", "23"])
        then:
        restTemplate.exchange(_, _, _, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)


        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException

    }
    void "buildURLToCreateETag"() {
        when:
        final String resourceURL = service.buildURLToCreateETag()
        then:
        assert resourceURL == "http://ncgc/etags/etag"

    }

    void "getResourceContext"() {
        when:
        String resourceContext = service.getResourceContext()

        then:
        assert resourceContext == RestApiConstants.ETAGS_RESOURCE
    }


    void "getSearchResource"() {
        when:
        String searchResource = service.getSearchResource()
        then:
        assert !searchResource
    }

    void "getResource"() {
        when:
        String resource = service.getResource()
        then:
        assert resource == "http://ncgc/etags/"

    }


}


