/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        restTemplate.getForEntity(_ as URI, _ as Class) >> {throw exceptionThrown}
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
        restTemplate.getForEntity(_ as URI, _ as Class) >> {throw exceptionThrown}
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
        restTemplate.postForEntity(_ as URI, _ as Map, _ as Class) >> {throw exceptionThrown}
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

