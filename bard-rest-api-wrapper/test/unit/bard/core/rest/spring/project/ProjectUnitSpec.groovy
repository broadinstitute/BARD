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

import bard.core.rest.spring.compounds.Compound

@Unroll
class ProjectUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()
    public static final String PROJECT_FROM_FREE_TEXT = '''
    {
        "projectId": "172",
        "name": "Probe Development Summary of Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
        "highlight": null
    }
   '''

    public static final String PROJECT = '''
    {
       "bardProjectId":3,
       "capProjectId":72,
       "experimentTypes":{
          "85":"secondary assay",
          "84":"counter-screening assay",
          "86":"secondary assay"
       },
       "category":0,
       "type":0,
       "classification":0,
       "name":"High Throughput Screen for Novel Inhibitors of Platelet Integrin alphaIIb-beta3",
       "description":"The alphaIIbbeta3 receptor plays a vital role in hemostasis and thrombosis, where receptor...",
       "source":"NCGC",
       "score":1.0,
       "grantNo":null,
       "deposited":null,
       "updated":null,
       "probes":[

       ],
       "probeIds":[

       ],
       "eids":[
          84,
          85,
          86
       ],
       "aids":[
          71,
          72,
          73
       ],
       "publications":[
            8
       ],
       "targets":[
            {
                "biology": "PROTEIN",
                "entityId": 2,
                "name": "Platelet Integrin alphaIIb-beta3",
                "entity": "project",
                "extId": "D3Z2V4",
                "extRef": null,
                "dictLabel": "UniProt accession number",
                "dictId": 1398,
                "serial": 240,
                "updated": null,
                "resourcePath": "/biology/240"
            }
       ],
       "resourcePath":"/projects/3",
       "experimentCount":3
    }
    '''

    void "test serialization to Project - Free Text Search"() {
        when:
        final Project project = objectMapper.readValue(PROJECT_FROM_FREE_TEXT, Project.class)
        then:
        assert project.projId == project.bardProjectId
    }

    void "test serialization to Project"() {
        when:
        final Project project = objectMapper.readValue(PROJECT, Project.class)
        then:
        assert project.getId() == 3
        assert project.getBardProjectId() == 3
        assert project.getName() == "High Throughput Screen for Novel Inhibitors of Platelet Integrin alphaIIb-beta3"
        assert project.getType() == 0
        assert project.getCategory() == 0
        assert project.getSource() == "NCGC"
        assert project.getDescription() == "The alphaIIbbeta3 receptor plays a vital role in hemostasis and thrombosis, where receptor..."
        assert !project.getGrantNo()
        assert !project.getDeposited()
        assert !project.getUpdated()
        assert !project.getKegg_disease_names()
        assert !project.getKegg_disease_cat()
        assert !project.getBiology().isEmpty()
        assert project.getAids()
        assert project.getEids()
        assert project.experimentCount == 3
        assert !project.probes
        assert !project.probeIds
        assert project.getResourcePath() == "/projects/3"
        assert !project.getAk_dict_label()
        assert !project.getAv_dict_label()
        assert !project.getGobp_id()
        assert !project.getGobp_term()
        assert !project.getGocc_id()
        assert !project.getGomf_id()
        assert !project.getGomf_term()
        assert !project.getGocc_term()
        assert project.getClassification() == 0
        final List<Long> publications = project.getPublications()
        assert publications
        final Long document = publications.get(0)
        assert document
        assert !project.hasProbes()
    }

    void "test has Probes #label"() {
        given:
        ProjectAbstract projectAbstract = createdProjectAbstract
        when:
        boolean hasProbes = projectAbstract.hasProbes()
        then:
        assert hasProbes == expected
        where:
        label                   | expected | createdProjectAbstract
        "Has list of Probes"    | true     | new ProjectAbstract(probes: [new Compound()])
        "Has list of Probe Ids" | true     | new ProjectAbstract(probeIds: [2, 3])
        "Has no Probes"         | false    | new ProjectAbstract()

    }


}

