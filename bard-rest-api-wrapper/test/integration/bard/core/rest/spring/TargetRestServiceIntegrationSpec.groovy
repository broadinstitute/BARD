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

package bard.core.rest.spring

import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.util.Target
import bard.core.rest.spring.util.TargetClassification
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import bard.core.rest.spring.compounds.TargetClassInfo

@Unroll
class TargetRestServiceIntegrationSpec extends IntegrationSpec {
    TargetRestService targetRestService
    /**
     * http://bard.nih.gov/api/v15/targets/accession/P20393
     */
    void "getTargetByAccessionNumber acc - P20393"() {
        given:
        String accessionNumber = "P20393"
        when:
        final Target target = targetRestService.getTargetByAccessionNumber(accessionNumber)
        then:
        assert target
        assert "P20393" == target.acc
        assert "Nuclear receptor subfamily 1 group D member 1" == target.name
        assert "Reviewed" == target.status
        assert "http://www.uniprot.org/uniprot/P20393" == target.url
        assert 9572 == target.geneId
        assert 9606 == target.taxId
        assert "/targets/accession/P20393" == target.resourcePath
        assert 4 <= target.targetClassifications.size()
        for (TargetClassification targetClassification : target.getTargetClassifications()) {
            assert targetClassification
        }
    }
    void "test construct Target information"(){
        given:
        String accessionNumber = "P20393"
        final Target target = targetRestService.getTargetByAccessionNumber(accessionNumber)
        when:
        List<TargetClassInfo> list= Target.constructTargetInformation(target)
        then:
        assert list

    }
    /**
     *  Example http://bard.nih.gov/api/v15/targets/accession/P20393/classification/panther
     */
    void "getClassificationsFromSourceWithTarget acc - P20393"() {
        given:
        final String source = "panther"
        final String targetAccessionNumber = "P20393"
        when:
        final List<TargetClassification> targetClassifications = targetRestService.getClassificationsFromSourceWithTarget(source, targetAccessionNumber)
        then:
        assert targetClassifications
        assert 4 <= targetClassifications.size()
        for (TargetClassification targetClassification : targetClassifications) {
            assert targetClassification
        }
    }
    /**
     * http://bard.nih.gov/api/v15/targets/classification/panther/PC00169
     */
    void "getTargetsFromClassificationId  - PC00169"() {
        given:
        final String source = "panther"
        final String targetClassificationId = "PC00169"
        when:
        final List<Target> targets = targetRestService.getTargetsFromClassificationId(source, targetClassificationId)
        then:
        assert targets
        assert 30 <= targets.size()
        for (Target target : targets) {
            assert target
        }
    }

    void "getResourceContext"() {

        when:
        final String resourceContext = targetRestService.getResourceContext()
        then:
        assert RestApiConstants.TARGETS_RESOURCE == resourceContext
    }

    void "getResource"() {
        when:
        final String resourceContext = targetRestService.getResource()
        then:
        assert "http://bard.nih.gov/api/v17.3/targets/" == resourceContext
    }

    void "getSearchResource"() {
        when:
        final String resourceContext = targetRestService.getSearchResource()
        then:
        assert !resourceContext
    }

}

