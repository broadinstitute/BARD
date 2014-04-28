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
class MeasureUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String MEASURE = '''
      {
         "id":2762,
         "name":null,
         "comps":[
            {
               "entityId":null,
               "entity":"assay",
               "source":"cap-measure",
               "id":2762,
               "display":"AC50",
               "contextRef":null,
               "key":null,
               "value":"AC50",
               "extValueId":null,
               "url":null,
               "displayOrder":0,
               "related":"assayContextRefs:6986,113970|parentMeasure:2763"
            }
         ]
      }
      '''

    void "test serialization to Context"() {
        when:
        final Measure measure = objectMapper.readValue(MEASURE, Measure.class)
        then:
        assert measure.id==2762
        assert !measure.name
        List<Annotation> comps = measure.comps
        assert comps
        assert comps.size() == 1
        Annotation comp = comps.get(0)
        assert comp.display=="AC50"
        assert comp.entity=="assay"
        assert !comp.entityId
        assert comp.source== "cap-measure"
        assert comp.id==2762
        assert !comp.contextRef
        assert !comp.key
        assert comp.value == "AC50"
        assert !comp.extValueId
        assert !comp.url
        assert comp.displayOrder==0
        assert comp.related=="assayContextRefs:6986,113970|parentMeasure:2763"
        assert measure.parseParentMeasureId() == 2763
        assert measure.parseRelatedContextIds() == [6986,113970]
    }

    void "test parseParentMeasureId and parseRelatedContextIds #label"() {
        when:
        final Measure measure = objectMapper.readValue(MEASURE, Measure.class)
        measure.comps.first().related = related

        then:
        assert measure.parseParentMeasureId() == expectedParentMeasureId
        assert measure.parseRelatedContextIds() == expectedRelatedContextIds

        where:
        label | related | expectedParentMeasureId | expectedRelatedContextIds
        "empty related field" | "" | null | []
        "null related field" | null | null | []
        "just parent measure" | "|parentMeasure:5" | 5 | []
        "contextRef and parent measure" | "assayContextRefs:1|parentMeasure:2" | 2 | [1]
        "just multiple contextRefs" | "assayContextRefs:1,2,3,4,5" | null | [1,2,3,4,5]
    }

}

