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

import bard.core.rest.spring.util.CapDictionary
import bard.core.rest.spring.util.Node
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

@Unroll
class DictionaryRestServiceIntegrationSpec extends IntegrationSpec {
    DictionaryRestService dictionaryRestService


    void "getDictionary #label"() {
        when:
        CapDictionary capDictionary = dictionaryRestService.getDictionary()
        then:
         assert capDictionary.getDictionaryElementMap().isEmpty()  == isEmpty
        where:
        label                            | isEmpty
        "Force a reload of cache"        | false
        "Do not force a reload of cache" | false

    }
    void "Dictionary #label"() {
        when:
        final Node dictionaryElement = dictionaryRestService.findDictionaryElementById(dictionaryId)
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

