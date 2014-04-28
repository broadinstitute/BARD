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

package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ETagCollectionUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    final String ETAG_COLLECTION_JSON = '''


    {
       "collection":
       [
           {
               "accessed": 1348295120000,
               "etag_id": "de6add5339d1af20",
               "count": 7109,
               "status": 1,
               "created": 1348240757000,
               "description": "The NCGC Pharmaceutical Collection",
               "name": "NPC database",
               "type": "gov.nih.ncgc.bard.entity.Compound",
               "url": "http://tripod.nih.gov/npc",
               "modified": 1348240757000
           }
       ],
       "link": "/resources"
    }


   '''

    void "test serialize json to ETagCollection"() {
        when:
        final ETagCollection eTagCollection = objectMapper.readValue(ETAG_COLLECTION_JSON, ETagCollection.class)
        eTagCollection.setAdditionalProperties("key","value")
        then:
        assert eTagCollection
        assert eTagCollection.etags
        final ETag etag = eTagCollection.etags.get(0)
        assert etag
        assert etag.accessed == 1348295120000
        assert etag.etag_id == "de6add5339d1af20"
        assert etag.count == 7109
        assert etag.status == 1
        assert etag.created == 1348240757000
        assert etag.description == "The NCGC Pharmaceutical Collection"
        assert etag.name == "NPC database"
        assert etag.type == "gov.nih.ncgc.bard.entity.Compound"
        assert etag.url == "http://tripod.nih.gov/npc"
        assert etag.modified == 1348240757000
        assert eTagCollection.hashCode()
        assert eTagCollection.getLink()=="/resources"
        assert eTagCollection.toString()
        assert !eTagCollection.equals("String")
        assert eTagCollection.getAdditionalProperties()
        assert (String)eTagCollection.getAdditionalProperties().get("key") == "value"
    }

}

