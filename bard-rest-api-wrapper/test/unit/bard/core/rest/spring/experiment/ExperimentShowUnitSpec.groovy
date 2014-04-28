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

package bard.core.rest.spring.experiment

import bard.core.interfaces.ExperimentRole
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExperimentShowUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String EXPERIMENT_SHOW_JSON = '''


    {
       "bardExptId": 197,
           "assayId":
   [
       {
           "aid": 519,
           "bardAssayId": 197,
           "capAssayId": 3172,
           "category": 1,
           "type": 0,
           "summary": 0,
           "assays": 0,
           "classification": 0,
           "name": "Dose Response Assay for Formylpeptide Receptor (FPR) Ligands and Dose Response Counter-Screen Assay for Formylpeptide-Like-1 (FPRL1) Ligands",
           "source": "NMMLSC",
           "grantNo": null,
           "deposited": null,
           "updated": null,
           "documents":
           [
               12401407,
               1910690
           ],
           "targets":
           [
           ],
           "experiments":
           [
               197
           ],
           "projects":
           [
               1,
               4
           ],
           "kegg_disease_names":
           [
           ],
           "kegg_disease_cat":
           [
           ],
           "resourcePath": "/assays/197"
       }
   ],
       "pubchemAid": 519,
       "category": 1,
       "type": 2,
       "summary": 0,
       "assays": 0,
       "classification": 2,
       "substances": 272,
       "compounds": 272,
       "activeCompounds": 272,
       "name": "Dose Response Assay for Formylpeptide Receptor",
       "description": "University of New Mexico Assay Overview",
       "source": "NCGC",
       "grantNo": "TR001",
       "deposited": "deposited",
       "updated": null,
       "hasProbe": false,
       "projectIdList":
       [
           1,
           4
       ],
       "resourcePath": "/experiments/197"
    }
'''

    void "test getId #label"() {
        when:
        ExperimentShow experimentShow = new ExperimentShow()
        then:
        assert experimentShow.getBardExptId() == expectedId

        where:
        label             | expectedId
        "Expt id is null" | 0
    }

    void "test get #label"() {
        when:
        ExperimentShow experimentShow = new ExperimentShow()
        then:
        assert experimentShow.getCapAssayId() == expectedId
        assert experimentShow.getCapExptId() == expectedId
        assert experimentShow.getBardAssayId() == expectedId
        assert experimentShow.getBardExptId() == expectedId

        where:
        label          | expectedId
        "Adid is null" | 0
    }

    void "test serialization to ExperimentShow"() {
        when:
        final ExperimentShow experimentShow = objectMapper.readValue(EXPERIMENT_SHOW_JSON, ExperimentShow.class)
        then:
        assert experimentShow
        assert experimentShow.bardExptId == 197
        assert experimentShow.resourcePath == "/experiments/197"
        assert experimentShow.projectIdList.size() == 2
        assert !experimentShow.hasProbe
        assert !experimentShow.updated
        assert experimentShow.deposited == "deposited"
        assert experimentShow.grantNo == "TR001"
        assert experimentShow.source == "NCGC"
        assert experimentShow.description == "University of New Mexico Assay Overview"
        assert experimentShow.name == "Dose Response Assay for Formylpeptide Receptor"
        assert experimentShow.compounds == 272
        assert experimentShow.activeCompounds == 272
        assert experimentShow.substances == 272
        assert experimentShow.pubchemAid == 519
        assert experimentShow.category == 1
        assert experimentShow.type == 2
        assert experimentShow.summary == 0
        assert experimentShow.assays
        assert experimentShow.classification == 2
        assert experimentShow.assays.size() == 1
        assert experimentShow.getBardExptId() == 197
        assert experimentShow.getProjectCount() == 2
        assert experimentShow.getAdditionalProperties()
        assert !experimentShow.equals("Some String")
        assert experimentShow.hashCode()
        assert experimentShow.toString()
        assert experimentShow.getRole() == ExperimentRole.SecondaryConfirmation
    }

}

