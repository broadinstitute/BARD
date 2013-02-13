package bard.core.rest.spring

import bard.core.rest.spring.util.DictionaryElement
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import spock.lang.IgnoreRest
import bard.core.rest.spring.util.CapDictionary

@Unroll
class DataExportRestServiceIntegrationSpec extends IntegrationSpec {
    DataExportRestService dataExportRestService


    void "getDictionary #label"() {
        when:
        CapDictionary capDictionary = dataExportRestService.getDictionary()
        then:
        assert dataExportRestService.dictionaryElementMap.isEmpty() == isEmpty
        where:
        label                            | isEmpty
        "Force a reload of cache"        | false
        "Do not force a reload of cache" | false

    }

    void "#label"() {
        when:
        final DictionaryElement dictionaryElement = dataExportRestService.findDictionaryElementById(dictionaryId)
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

