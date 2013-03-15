package bard.core.rest.spring

import grails.test.mixin.TestFor

import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

import bard.core.rest.spring.util.CapDictionary

import bard.core.rest.spring.util.Node
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.util.ExternalUrlDTO

@Unroll
@TestFor(DataExportRestService)
class DataExportRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    LoggerService loggerService
    final String baseUrl ="http://ncgc"


    void setup() {
        this.restTemplate = Mock(RestTemplate)
        this.loggerService = Mock(LoggerService)
        service.restTemplate = this.restTemplate
        service.loggerService = loggerService
        ExternalUrlDTO externalUrlDTO = new ExternalUrlDTO(promiscuityUrl:"badapple",baseUrl: "http://ncgc" )
        service.externalUrlDTO = externalUrlDTO

    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
    }

    void "findDictionaryElementById"() {
        given:
        service.metaClass.getDictionary = {new CapDictionary(nodes: [new Node(elementId: 11, label: "label", elementStatus: "Published", description: "description")])}

        when:
        Node dictionaryElement = service.findDictionaryElementById(11)

        then:
        assert dictionaryElement
    }

    void "getResourceContext"() {
        when:
        String resourceContext = service.getResourceContext()

        then:
        assert RestApiConstants.DICTIONARY_RESOURCE == resourceContext
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
        assert baseUrl + RestApiConstants.DICTIONARY_RESOURCE ==  resource

    }
}


