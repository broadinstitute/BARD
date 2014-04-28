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

package bard.validation.extext

import bard.validation.ext.ExternalOntologyAPI
import bard.validation.ext.ExternalOntologyException
import groovy.json.JsonException
import org.apache.commons.logging.Log
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/7/14
 * Time: 2:42 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class BardExternalOntologyFactoryUnitSpec extends Specification {

    private static final String VALID_BASE_URL = 'http://somebaseurl/'
    private static final String INVALID_BASE_URL = 'somebaseurl'
    BardExternalOntologyFactory bardExternalOntologyFactory = new BardExternalOntologyFactory()

    RestTemplate restTemplate = Mock(RestTemplate)
    Log log = Mock(Log)

    void setup() {
        bardExternalOntologyFactory.externalOntologyProxyUrlBase = VALID_BASE_URL
        bardExternalOntologyFactory.restTemplate = restTemplate
        bardExternalOntologyFactory.log = log
    }



    void "test getExternalOntologyAPI for success #desc"() {
        when: ''
        final ExternalOntologyAPI externalOntologyAPI = bardExternalOntologyFactory.getExternalOntologyAPI(externalUrl)

        then:
        restTemplateInvocations * restTemplate.getForObject(_, String.class) >> { restTemplateCallable.call() }
        externalOntologyAPI?.getClass() == externalOntologyAPIClass

        where:
        desc                                     | externalUrl                        | restTemplateInvocations | restTemplateCallable       | externalOntologyAPIClass
        "returns ExternalOntologyPerson"         | PersonCreator.PERSON_URI as String | 0                       | { null }                   | ExternalOntologyPerson
        "returns BardDelegatingExternalOntology" | 'http://foo'                       | 1                       | { '{"hasSupport":true}' }  | BardDelegatingExternalOntology
        "returns null, no implementation found"  | 'http://bar'                       | 1                       | { '{"hasSupport":false}' } | null

    }

    void "test getExternalOntologyAPI for exception #desc"() {
        given:
        bardExternalOntologyFactory.externalOntologyProxyUrlBase = externalOntologyProxyUrlBase

        when:
        final ExternalOntologyAPI externalOntologyAPI = bardExternalOntologyFactory.getExternalOntologyAPI(externalUrl)

        then:
        restTemplateInvocations * restTemplate.getForObject(_, String.class) >> { restTemplateCallable.call() }
        ExternalOntologyException e = thrown(ExternalOntologyException)
        e.cause.getClass() == expectedCause


        where:
        desc                    | externalOntologyProxyUrlBase | externalUrl | restTemplateInvocations | restTemplateCallable                     | expectedCause
        "RestClientException"   | VALID_BASE_URL               | 'foo'       | 1                       | { throw new RestClientException("boo") } | RestClientException
        "JsonException"         | VALID_BASE_URL               | 'foo'       | 1                       | { '{"hasSupport":' }                     | JsonException
        "MalformedURLException" | INVALID_BASE_URL             | 'foo'       | 0                       | { null }                                 | MalformedURLException

    }
}
