package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.exceptions.RestApiException
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.ETagCollection
import bard.core.rest.spring.util.Facet
import bard.core.rest.spring.util.StructureSearchParams
import grails.test.mixin.TestFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

import bard.core.rest.spring.compounds.*
import bard.core.rest.spring.util.DictionaryElement
import bard.core.rest.spring.util.CapDictionary
import spock.lang.IgnoreRest

@Unroll
@TestFor(DataExportRestService)
class DataExportRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    String dataExportApiKey = "apiKey"
    String dictionaryAcceptType = "accept"
    String dataExportDictionaryURL = "http://test.com"




    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.dataExportApiKey = dataExportApiKey
        service.dictionaryAcceptType = dictionaryAcceptType
        service.dataExportDictionaryURL = dataExportDictionaryURL
        service.baseUrl = "http://ncgc"
    }

    void cleanup() {
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
    }


    void "getDictionary with exception"() {
        when:
        CapDictionary capDictionary = service.getDictionary()
        then:
        service.metaClass.getExchange(_, _, _) >> {throw new Exception()}
        assert !capDictionary.elements


    }


    void "getLoadDictionary #label"() {
        when:
        service.loadDictionary(capDictionary)

        then:
        assert service.dictionaryElementMap.isEmpty() == isDictionaryElementMapEmpty
        where:
        label                             | capDictionary                                                                                                                               | isDictionaryElementMapEmpty
        "Cap Dictionary With Elements"    | new CapDictionary(elements: [new DictionaryElement(elementId: 11, label: "label", elementStatus: "Published", description: "description")]) | false
        "Cap Dictionary with no elements" | new CapDictionary()                                                                                                                         | true
    }

    void "findDictionaryElementById with loaded Dictionary Map"() {
        given:
        Long keyVal = new Long(11)
        final DictionaryElement element = new DictionaryElement(elementId: new Long(11), label: "label", elementStatus: "Published", description: "description")
        service.dictionaryElementMap.put(keyVal,element)

        when:
        DictionaryElement dictionaryElement = service.findDictionaryElementById(keyVal)

        then:
        assert dictionaryElement
    }

    void "findDictionaryElementById with empty Dictionary Map"() {
        given:
        service.dictionaryElementMap = [:]
        service.metaClass.getDictionary = {new CapDictionary(elements: [new DictionaryElement(elementId: 11, label: "label", elementStatus: "Published", description: "description")])}

        when:
        DictionaryElement dictionaryElement = service.findDictionaryElementById(11)

        then:
        assert !dictionaryElement
    }

    void "getResourceContext"() {
        when:
        String resourceContext = service.getResourceContext()

        then:
        assert !resourceContext
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
        assert !resource

    }
}


