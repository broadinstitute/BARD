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
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.CapDictionary
import bard.core.rest.spring.util.Node
import bard.core.util.ExternalUrlDTO
import grails.test.mixin.TestFor
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(DictionaryRestService)
class DictionaryRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    LoggerService loggerService
    String capUrl ="http://cap"
    String ncgcUrl ="http://ncgc"

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        this.loggerService = Mock(LoggerService)
        service.restTemplate = this.restTemplate
        service.loggerService = loggerService
        ExternalUrlDTO externalUrlDTO = new ExternalUrlDTO(promiscuityUrl:"badapple", ncgcUrl: ncgcUrl, capUrl: capUrl )
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
        assert ncgcUrl + RestApiConstants.DICTIONARY_RESOURCE ==  resource

    }
}


