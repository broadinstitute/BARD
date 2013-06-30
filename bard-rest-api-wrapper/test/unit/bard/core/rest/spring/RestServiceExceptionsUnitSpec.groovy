package bard.core.rest.spring

import bard.core.exceptions.RestApiException
import bard.core.helper.LoggerService
import bard.core.util.ExternalUrlDTO
import grails.test.mixin.TestFor
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(SubstanceRestService)
class RestServiceExceptionsUnitSpec extends Specification {
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

    void "newETag with throws #label"() {
        when:
        service.newETag("name", [1, 2, 3])
        then:
        restTemplate.exchange(_, _, _, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)


        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException
    }


    void "putETag with throws #label"() {
        when:
        service.putETag("name", [1, 2, 3])
        then:
        restTemplate.exchange(_, _, _, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)
        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException
    }

    void "getForObject with 2 args throws #label"() {
        when:
        service.getForObject(new URI("http://test.com"), String.class)
        then:
        restTemplate.getForObject(_, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)
        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException

    }

    void "getForObject with 3 args throws #label"() {
        when:
        service.getForObject("http://test.com", String.class, [:])
        then:
        restTemplate.getForObject(_, _, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)
        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException

    }

    void "postForObject with 3 args throws #label"() {
        when:
        service.postForObject(new URI("http://test.com"), String.class, [:])
        then:
        restTemplate.postForObject(_, _, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)
        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException

    }

    void "postExchange throws #label"() {
        when:
        service.postExchange("http://test.com",new HttpEntity<List>(),String.class)
        then:
        restTemplate.exchange(_, _, _, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)
        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException

    }

    void "getExchange throws #label"() {
        when:
        service.getExchange(new URI("http://test.com"),new HttpEntity<List>(),String.class)
        then:
        restTemplate.exchange(_, _, _, _) >> {throw exceptionThrown}
        thrown(expectedThrownExceptionClass)
        where:
        label                 | exceptionThrown                                    | expectedThrownExceptionClass
        "HttpClientError"     | new HttpClientErrorException(HttpStatus.NOT_FOUND) | HttpClientErrorException
        "RestClientException" | new RestClientException("Some Message")            | RestApiException

    }
}

