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
class ContextUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String CONTEXT = '''
    {
        "id": 7186,
        "name": "Context for percent activity",
        "comps":
        [
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
            },
            {
                "entityId": null,
                "entity": "assay",
                "source": "cap-context",
                "id": 7186,
                "display": "single parameter",
                "contextRef": "Context for percent activity",
                "key": "assay readout",
                "value": "single parameter",
                "extValueId": null,
                "url": null,
                "displayOrder": 1,
                "related": "measureRefs:22510"
            }
        ]
    }
    '''

    void "test serialization to Context"() {
        when:
        final Context context = objectMapper.readValue(CONTEXT, Context.class)

        then:
        assert context.id == 7186
        assert context.name == "Context for percent activity"
        List<Annotation> comps = context.contextItems
        assert comps
        assert comps.size() == 2
        Annotation comp = comps.get(0)
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
        assert context.parseRelatedMeasureIds() == [22510]
    }

    void "test parseRelatedMeasureIds #label"() {
        when:
        final Context context = objectMapper.readValue(CONTEXT, Context.class)
        context.contextItems.each { it.related = related }

        then:
        assert context.parseRelatedMeasureIds() == expectedRelatedMeasureIds

        where:
        label | related | expectedRelatedMeasureIds
        "empty related field" | "" | []
        "null related field" | null | []
        "one measureRef" | "measureRefs:5" | [5]
        "many measureRefs" | "measureRefs:1,2,3,4,5" | [1,2,3,4,5]
    }

    void "test parseRelatedMeasureIds uneven measureRefs"() {
        when:
        final Context context = objectMapper.readValue(CONTEXT, Context.class)
        context.contextItems.get(0).related = "measureRefs:1,2"
        context.contextItems.get(1).related = "measureRefs:3"

        then:
        assert context.parseRelatedMeasureIds() == [1,2,3]

    }

}

