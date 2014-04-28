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

package bardqueryapi

import bard.core.rest.spring.project.ProjectExperiment
import bard.core.rest.spring.project.ProjectStep
import com.fasterxml.jackson.databind.ObjectMapper
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 *
 */
@Unroll
@TestFor(QueryProjectExperimentRenderService)
class QueryProjectExperimentRenderServiceUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()


    public static final String PROJECT_STEP = '''
    {
        "prevBardExpt":
        {
            "bardExptId": 14084,
            "capExptId": 3360,
            "bardAssayId": 8959,
            "capAssayId": 3365,
            "pubchemAid": 1503,
            "category": -1,
            "type": -1,
            "summary": 0,
            "assays": 0,
            "classification": -1,
            "substances": 8,
            "compounds": 8,
            "activeCompounds": 2,
            "confidenceLevel": 1,
            "name": "Minimal Inhibitory Concentration assay in E. Coli for small molecule DnaK Modulators targeting the b-domain.",
            "description": null,
            "source": null,
            "grantNo": null,
            "deposited": null,
            "updated": "2013-03-01",
            "hasProbe": false,
            "projectIdList": [
                1944
            ],
            "resourcePath": "/experiments/14084"
        },
        "nextBardExpt":
        {
            "bardExptId": 14103,
            "capExptId": 3807,
            "bardAssayId": 8978,
            "capAssayId": 3812,
            "pubchemAid": 1505,
            "category": -1,
            "type": -1,
            "summary": 0,
            "assays": 0,
            "classification": -1,
            "substances": 2,
            "compounds": 2,
            "activeCompounds": 1,
            "confidenceLevel": 1,
            "name": "Minimal Inhibitory Concentration assay in Y. pseudotuberculosis for small molecule DnaK Modulators targeting the beta-domain",
            "description": null,
            "source": null,
            "grantNo": null,
            "deposited": null,
            "updated": "2013-03-01",
            "hasProbe": false,
            "projectIdList": [
                1944
            ],
            "resourcePath": "/experiments/14103"
        },
        "bardProjId": 1944,
        "stepId": 5148,
        "edgeName": "Linked by Compound set (Swamidass)",
        "annotations": [
            {
                "entityId": 5148,
                "entity": "project-step",
                "source": "cap-context",
                "id": 5398,
                "display": "2",
                "contextRef": "Compound Overlap",
                "key": "1242",
                "value": null,
                "extValueId": null,
                "url": null,
                "displayOrder": 0,
                "related": "1944"
            }
        ],
        "resourcePath": ""
    }
    '''


    void testBuildNodesAndEdges() {
        given:
        final Set<Edge> edges = [] as Set<Edge>

        final List<Node> nodes = []
        final List<Long> visitedNodes = []
        final List<Node> isolatedNodes = []
        final Map<Long, ProjectExperiment> projectExperimentMap = [:]
        final ProjectStep projectStep = objectMapper.readValue(PROJECT_STEP, ProjectStep.class)
        ProjectExperiment projectExperiment = new ProjectExperiment()
        projectExperiment.addFollowingProjectStep(projectStep)
        final List<Long> queue = []
        queue.add(projectExperiment.capExptId)
        projectExperimentMap.put(projectExperiment.capExptId, projectExperiment)

        when:
        service.buildNodesAndEdges(edges, queue, nodes, visitedNodes, isolatedNodes, projectExperimentMap)
        then:
        assert !queue
        assert nodes
        assert visitedNodes
        assert !isolatedNodes
        assert projectExperimentMap
    }




    void testIsIsolatedNode() {
        given:
        ProjectExperiment projectExperiment = new ProjectExperiment(capAssayId: 111, bardAssayId: 222, capExptId: 333, bardExptId: 444, name: "Some Name")

        when:
        boolean isIsolated = service.isIsolatedNode(projectExperiment)
        then:
        assert isIsolated
    }

    void testConstructNode() {
        given:
        ProjectExperiment projectExperiment = new ProjectExperiment(capAssayId: 111, bardAssayId: 222, capExptId: 333, bardExptId: 444, name: "Some Name")

        when:
        final Node node = service.constructNode(projectExperiment)

        then:
        assert node
        assert '333' == node.id
        assert node.keyValues
    }

    void testConstructEdges() {
        given:
        final List<Long> queue = []
        final ProjectStep projectStep = objectMapper.readValue(PROJECT_STEP, ProjectStep.class)
        ProjectExperiment projectExperiment = new ProjectExperiment()
        projectExperiment.addFollowingProjectStep(projectStep)

        when:
        final Set<Edge> edges = service.constructEdges(projectExperiment, queue)
        then:
        assert edges
        assert 1 == edges.size()
        final Edge edge = edges.iterator().next()
        assert 2 == queue.size()
        assert edge
        assert '3360' == edge.from
        assert "3807" == edge.to
        assert "Linked by Compound set (Swamidass)" == edge.label

    }




    void testConstructEdge() {
        given:
        final List<Long> queue = []
        final ProjectStep projectStep = objectMapper.readValue(PROJECT_STEP, ProjectStep.class)

        when:
        final Edge edge = service.constructEdge(projectStep, queue)
        then:
        assert 2 == queue.size()
        assert edge
        assert '3360' == edge.from
        assert "3807" == edge.to
        assert "Linked by Compound set (Swamidass)" == edge.label
    }
}
