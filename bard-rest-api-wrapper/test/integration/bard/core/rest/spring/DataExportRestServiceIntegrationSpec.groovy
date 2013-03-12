package bard.core.rest.spring

import bard.core.rest.spring.util.CapDictionary
import bard.core.rest.spring.util.Node
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

@Unroll
class DataExportRestServiceIntegrationSpec extends IntegrationSpec {
    DataExportRestService dataExportRestService


    void "getDictionary #label"() {
        when:
        CapDictionary capDictionary = dataExportRestService.getDictionary()
        then:
         assert capDictionary.getDictionaryElementMap().isEmpty()  == isEmpty
        where:
        label                            | isEmpty
        "Force a reload of cache"        | false
        "Do not force a reload of cache" | false

    }
    void "Dictionary #label"() {
        when:
        final Node dictionaryElement = dataExportRestService.findDictionaryElementById(dictionaryId)
        then:
        assert dictionaryElement
        assert dictionaryElement.elementId == dictionaryId
        assert dictionaryElement.label == elementLabel
        assert dictionaryElement.elementStatus == elementStatus
        where:
        label                                                | dictionaryId | elementLabel      | elementStatus
        "Element with Id 3"                                  | 3            | "assay protocol"  | "Published"
        "Element with Id 4"                                  | 4            | "assay component" | "Published"
        "Element with Id 3, should be called from the cache" | 3            | "assay protocol"  | "Published"
        "Element with Id 4, should be called from the cache" | 4            | "assay component" | "Published"

    }

}

