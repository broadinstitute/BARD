package bard.core.rest.spring.etags
import bard.core.exceptions.RestApiException
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.ETagRestService
import bard.core.util.ExternalUrlDTO
import grails.test.mixin.TestFor
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(ETagRestService)
class ETagRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    LoggerService loggerService
    @Shared
    ExternalUrlDTO externalUrlDTO = new ExternalUrlDTO(promiscuityUrl:"badapple", ncgcUrl: "http://ncgc" )

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.externalUrlDTO = externalUrlDTO

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


