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

import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

@Unroll
class ETagsServiceIntegrationSpec extends IntegrationSpec {

    ETagsService eTagsService

    void "test createCompositeETags #label"() {
        given:

        when:
        String compositeETag = eTagsService.createCompositeETags(cids, pids, adids)
        then:
        assert compositeETag
        where:
        label             | adids              | pids            | cids
        "Composite ETags" | [5155, 5158, 5157] | [129, 102, 100] | [3235555, 3235556]
    }
     void "test createETag #label"() {
        when:
        String compositeETag = eTagsService.createETag(entityType, ids, [])
        then:
        assert compositeETag
        where:
        label           | ids                | entityType
        "Assay etag"    | [5155, 5158, 5157] | EntityType.ASSAY
        "Project etag"  | [129, 102, 100]    | EntityType.PROJECT
        "Compound eTag" | [3235555, 3235556] | EntityType.COMPOUND
    }

    void "test createCompositeETag #label"() {
        given:
        String compoundETag = eTagsService.createETag(EntityType.COMPOUND, cids, [])
        String assayETag = eTagsService.createETag(EntityType.ASSAY, adids, [])
        String projectETag = eTagsService.createETag(EntityType.PROJECT, pids, [])
        when:
        String compositeETag = eTagsService.createETag(EntityType.COMPOSITE, [], [compoundETag, assayETag, projectETag])
        then:
        assert compositeETag
        where:
        label             | adids              | pids            | cids
        "Composite ETags" | [5155, 5158, 5157] | [129, 102, 100] | [3235555, 3235556]


    }
}
