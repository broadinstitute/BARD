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

package bard.core.rest.spring.project

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ProjectExperimentUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()


    public static final String BARD_EXPT = '''
    {
        "bardExptId": 12666,
        "capExptId": 2617,
        "bardAssayId": 7540,
        "capAssayId": 2622,
        "pubchemAid": 2572,
        "category": -1,
        "type": -1,
        "summary": 0,
        "assays": 0,
        "classification": -1,
        "substances": 748,
        "compounds": 745,
        "name": "Confirmation qHTS Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
        "description": null,
        "source": null,
        "grantNo": null,
        "deposited": null,
        "updated": "2013-01-26",
        "hasProbe": false,
        "projectIdList": [
            1703
        ],
        "resourcePath": "/experiments/12666"
    } '''



    void "test serialization to BardExpt"() {
        when:
        final ProjectExperiment bardExpt = objectMapper.readValue(BARD_EXPT, ProjectExperiment.class)
        then:
        assert 12666 ==bardExpt.bardExptId
        assert 2617== bardExpt.capExptId
        assert 7540 == bardExpt.bardAssayId
        assert 2622==bardExpt.capAssayId
        assert 2572==bardExpt.pubchemAid
        assert -1==bardExpt.category
        assert -1== bardExpt.type
        assert 0== bardExpt.summary
        assert 0==bardExpt.assays
        assert -1==bardExpt.classification
        assert 748==bardExpt.substances
        assert 745==bardExpt.compounds
        assert "Confirmation qHTS Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)"==bardExpt.name
        assert !bardExpt.source
        assert !bardExpt.grantNo
        assert !bardExpt.isHasProbe()
        assert [1703] == bardExpt.projectIdList
        assert !bardExpt.precedingProjectSteps
        assert !bardExpt.followingProjectSteps
        assert  "/experiments/12666"==bardExpt.resourcePath
     }

}

