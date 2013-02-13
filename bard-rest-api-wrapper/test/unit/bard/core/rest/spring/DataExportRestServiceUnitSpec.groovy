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
    void cleanup(){
        //Clean up the metaClass mocking we added.
        def remove = GroovySystem.metaClassRegistry.&removeMetaClass
    }


    void "getDictionary with exception"() {
        when:
        CapDictionary capDictionary = service.getDictionary(ReloadCache.YES)
        then:
        service.metaClass.getExchange(_, _, _) >> {throw new Exception()}
        assert !capDictionary.elements


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


