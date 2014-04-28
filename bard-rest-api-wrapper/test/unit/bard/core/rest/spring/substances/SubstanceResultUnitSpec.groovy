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

package bard.core.rest.spring.substances

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SubstanceResultUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String SUBSTANCE_RESULT = '''
    {
       "collection":
       [
           {
               "sid": 842121,
               "cid": 6603008,
               "depRegId": "MLS000076160",
               "sourceName": "MLSMR",
               "url": null,
               "patentIds": null,
               "smiles": "CCOCCCNCC(=O)NC1=CC=C(C=C1)OC(F)(F)F.Cl",
               "deposited": "2005-06-04",
               "updated": "2012-03-01",
               "resourcePath": "/substances/842121"
           },
           {
               "sid": 842122,
               "cid": 6602571,
               "depRegId": "MLS000033655",
               "sourceName": "MLSMR",
               "url": null,
               "patentIds": null,
               "smiles": "COCCN1C(=NN=N1)CN2CCC(CC2)CC3=CC=CC=C3.Cl",
               "deposited": "2005-06-04",
               "updated": "2012-03-01",
               "resourcePath": "/substances/842122"
           }
       ],
       "link": "/substances?skip=10&top=10&expand=true&filter=MLSMR"
    }
   '''

    void "test serialization to SubstanceResult"() {
        when:
        final SubstanceResult substanceResult = objectMapper.readValue(SUBSTANCE_RESULT, SubstanceResult.class)
        then:
        assert substanceResult
        assert substanceResult.substances
        assert substanceResult.substances.size() == 2
        Substance substance = substanceResult.substances.get(0)
        assert substance.getSid() == 842121
        assert substance.getId() == substance.getSid()
        assert substance.getCid() == 6603008
        assert substance.getDepRegId() == "MLS000076160"
        assert substance.getSourceName() == "MLSMR"
        assert !substance.getUrl()
        assert !substance.getPatentIds()
        assert substance.getSmiles() == "CCOCCCNCC(=O)NC1=CC=C(C=C1)OC(F)(F)F.Cl"
        assert substance.getDeposited() == "2005-06-04"
        assert substance.getUpdated() == "2012-03-01"
        assert substance.getResourcePath() == "/substances/842121"
        assert substanceResult.link == "/substances?skip=10&top=10&expand=true&filter=MLSMR"
    }
}

