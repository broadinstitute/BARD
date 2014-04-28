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

package bard.core.rest.spring.assays

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class CompUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String COMP = '''
   {
        "entityId": null,
        "entity": "assay",
        "source": "cap-context",
        "id": 7186,
        "display": ".05 um",
        "contextRef": "Context for percent activity",
        "key": "screening concentration",
        "value": null,
        "extValueId": null,
        "url": null,
        "displayOrder": 0,
        "related": "measureRefs:22510"
    }
       '''

    @Shared Annotation comp1 = new Annotation(display: 'comp1 name', key: 'key1', value: 'value1')
    @Shared Annotation comp2 = new Annotation(display: 'comp2 name', key: 'key2', value: 'value2')
    @Shared Annotation comp3 = new Annotation(display: 'comp3 name', key: 'key3', value: 'value3')


    void "test serialization to Comp"() {
        when:
        final Annotation comp = objectMapper.readValue(COMP, Annotation.class)
        then:
        assert comp.display == ".05 um"
        assert comp.entity == "assay"
        assert !comp.entityId
        assert comp.source == "cap-context"
        assert comp.id == 7186
        assert comp.contextRef == "Context for percent activity"
        assert comp.key == "screening concentration"
        assert !comp.value
        assert !comp.extValueId
        assert !comp.url
        assert comp.displayOrder == 0
        assert comp.related == "measureRefs:22510"
    }

    void "test splitForColumnLayout: #label"() {
        when:
        List<List<Annotation>> result = Annotation.splitForColumnLayout(contextItems)

        then:
        assert result == expectedResult

        where:
        label                 | contextItems                 | expectedResult
        'one context-item'    | [comp1]               | [[comp1]]
        'two context-items'   | [comp1, comp2]        | [[comp1], [comp2]]
        'three context-items' | [comp1, comp2, comp3] | [[comp1, comp2], [comp3]]
        'zero context-items'  | []                    | []
    }
}

