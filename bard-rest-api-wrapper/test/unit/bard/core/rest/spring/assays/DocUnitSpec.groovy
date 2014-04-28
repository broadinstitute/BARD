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
class DocUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String DOC = '''
    {
        "id": 18225,
        "name": "External URL",
        "comps":
        [
            {
                "entityId": null,
                "entity": "assay",
                "source": "cap-doc",
                "id": 18225,
                "display": "External Database Link",
                "contextRef": "External URL",
                "key": "doc",
                "value": "http://www.southernresearch.org",
                "extValueId": "http://www.southernresearch.org",
                "url": null,
                "displayOrder": 0,
                "related": null
            }
        ]
    }
    '''

    void "test serialization to Doc"() {
        when:
        final Doc doc = objectMapper.readValue(DOC, Doc.class)
        then:
        assert doc.id==18225
        assert doc.name== "External URL"
        List<Annotation> comps = doc.comps
        assert comps
        assert comps.size() == 1
        Annotation comp = comps.get(0)
        assert comp.display=="External Database Link"
        assert comp.entity=="assay"
        assert !comp.entityId
        assert comp.source== "cap-doc"
        assert comp.id==18225
        assert comp.contextRef=="External URL"
        assert comp.key== "doc"
        assert comp.value=="http://www.southernresearch.org"
        assert comp.extValueId=="http://www.southernresearch.org"
        assert !comp.url
        assert comp.displayOrder==0
        assert !comp.related
    }


}

