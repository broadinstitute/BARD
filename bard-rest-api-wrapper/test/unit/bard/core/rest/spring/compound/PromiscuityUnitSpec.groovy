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

package bard.core.rest.spring.compound

import bard.core.rest.spring.compounds.PromiscuityScaffold
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.compounds.Promiscuity

@Unroll
class PromiscuityUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String PROMISCUITY = '''
    {
        "hscafs":
        [
           {
               "wTested": 17064,
                "sActive": 51,
                "wActive": 1876,
                "aTested": 479,
                "sTested": 51,
                "pScore": 2445,
                "scafid": 46705,
                "aActive": 222,
                "inDrug": true,
                "smiles": "O=C1C=CC(=N)c2ccccc12"
            }
        ],
        "cid": 752424
     }
     '''

    void "test serialization to Promiscuity"() {
        when:
        final Promiscuity promiscuity = objectMapper.readValue(PROMISCUITY, Promiscuity.class)
        then:
        assert promiscuity.cid == 752424
        final List<PromiscuityScaffold> scaffolds = promiscuity.getPromiscuityScaffolds()
        assert scaffolds
        PromiscuityScaffold promiscuityScaffold = scaffolds.get(0)
        assert 17064 == promiscuityScaffold.testedWells
        assert 51 == promiscuityScaffold.activeSubstances
        assert 1876 == promiscuityScaffold.activeWells
        assert 479 == promiscuityScaffold.testedAssays
        assert 51 == promiscuityScaffold.testedSubstances
        assert 2445 == promiscuityScaffold.promiscuityScore
        assert 46705 == promiscuityScaffold.scaffoldId
        assert 222 == promiscuityScaffold.activeAssays
        assert promiscuityScaffold.inDrug
        assert "O=C1C=CC(=N)c2ccccc12" == promiscuityScaffold.smiles
    }


}

