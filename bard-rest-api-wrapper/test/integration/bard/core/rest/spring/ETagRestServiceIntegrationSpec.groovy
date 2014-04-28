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

import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll
import bard.core.rest.spring.etags.ETags

@Unroll
class ETagRestServiceIntegrationSpec extends IntegrationSpec {
    ETagRestService eTagRestService
    AssayRestService assayRestService
    ProjectRestService projectRestService
    CompoundRestService compoundRestService

    void "test Make Composite ETags #label"() {
        given: "Create an Assay ETag"
        final String assayETag = assayRestService.newETag("My Assay collection", adids);
        and: "Create a Project ETag"
        final String projectETag = projectRestService.newETag("My Project Collection", pids)
        and: "Create a Compound ETag"
        final String compoundETag = compoundRestService.newETag("My Compound Collection", cids)
        when: "We call the composite Etags"
        final String compositeETag = this.eTagRestService.newCompositeETag("My Composite ETags", [assayETag, projectETag, compoundETag])

        then:
        assert compositeETag

        where:
        label             | adids              | pids               | cids
        "Composite ETags" | [5155, 5158, 5157] | [1581, 1563, 1748] | [3235555, 3235556]
    }
    void "test Get Composite ETags #label"() {
        given: "Create an Assay ETag"
        final String assayETag = assayRestService.newETag("My Assay collection", adids);
        and: "Create a Project ETag"
        final String projectETag = projectRestService.newETag("My Project Collection", pids)
        and: "Create a Compound ETag"
        final String compoundETag = compoundRestService.newETag("My Compound Collection", cids)
        and: "Create a composite ETag"
        final String compositeETag = this.eTagRestService.newCompositeETag("My Composite ETags", [assayETag, projectETag, compoundETag])
        when: "We call to get the composite Etags"
        final ETags eTags = this.eTagRestService.getComponentETags(compositeETag)

        then:
        assert eTags
        assert eTags.getByType("assays")
        assert eTags.getByType("compounds")
        assert eTags.getByType("projects")

        where:
        label             | adids              | pids               | cids
        "Composite ETags" | [5155, 5158, 5157] | [1581, 1563, 1748] | [3235555, 3235556]
    }

}

