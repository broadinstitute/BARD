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

package bard.core.rest.spring.etags

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ETagUnitSpec extends Specification {

    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String ETAG = '''
    [
        {
            "etag": "2b63a2bd2eb8280f",
            "type": "assays"
        },
        {
            "etag": "60ec019a66228e35",
            "type": "projects"
        },
        {
            "etag": "aa41d4c605122873",
            "type": "compounds"
        }
    ]
    '''

    void assertETag(final String expectedType, final String expectedEtag,
                    final String foundType, final String foundETag) {
        assert expectedType == foundType
        assert expectedEtag == foundETag

    }

    void "newCompositeETag #label"() {
        when:
        final ETags etags = objectMapper.readValue(ETAG, ETags.class)
        then:
        assert etags
        final List<ETag> foundETags = etags.getByType(type)
        assert foundETags
        assert 1 == foundETags.size()
        assert 3 == etags.buildMap().size()
        assertETag(type, etag, foundETags.get(0).getType(), foundETags.get(0).getEtag())
        where:
        label           | type        | etag
        "Assay Type"    | "assays"    | "2b63a2bd2eb8280f"
        "Project Type"  | "projects"  | "60ec019a66228e35"
        "Compound Type" | "compounds" | "aa41d4c605122873"

    }

}


