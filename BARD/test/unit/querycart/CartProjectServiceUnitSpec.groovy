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

package querycart

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll
import bardqueryapi.IQueryService
import bardqueryapi.QueryService
import bard.core.adapter.ProjectAdapter

import spock.lang.Shared
import bard.core.rest.spring.project.Project
/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartProjectService)
@Unroll
class CartProjectServiceUnitSpec extends Specification {

    IQueryService queryService
    @Shared ProjectAdapter projectAdapter = new ProjectAdapter(new Project(name: "name1", bardProjectId: 1L))

    void setup() {
        queryService = Mock(QueryService)
        service.queryService = this.queryService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test createCartProjectFromPID #label"() {
        when:
        CartProject returnedCartProject = service.createCartProjectFromPID(pid)

        then:
        1 * queryService.findProjectsByPIDs([pid]) >> {[projectAdapters: projectAdapters]}
        assert returnedCartProject?.name == expectedCartProjectName
        assert returnedCartProject?.externalId == expectedCartProjectId

        where:
        label                     | pid  | projectAdapters       | expectedCartProjectName | expectedCartProjectId
        "found a projectAdapter"  | 123L | [this.projectAdapter] | 'name1'                 | 1
        "no projectAdapter found" | 123L | []                    | null                    | null
    }
}
