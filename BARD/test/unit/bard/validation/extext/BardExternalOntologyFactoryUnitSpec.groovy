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
