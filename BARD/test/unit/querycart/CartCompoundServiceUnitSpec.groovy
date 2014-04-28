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

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.compounds.Compound
import bardqueryapi.IQueryService
import bardqueryapi.QueryService
import grails.test.mixin.TestFor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
//@TestMixin(GrailsUnitTestMixin)
@TestFor(CartCompoundService)
@Unroll
class CartCompoundServiceUnitSpec extends Specification {

    IQueryService queryService
    @Shared CompoundAdapter compoundAdapter = new CompoundAdapter(new Compound(name: "name1", cid: 1L, numActiveAssay: 0, numAssay: 0))

    void setup() {
        queryService = Mock(QueryService)
        service.queryService = this.queryService
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test createCartCompoundFromCID #label"() {
        when:
        CartCompound returnedCartCompound = service.createCartCompoundFromCID(cid)

        then:
        1 * queryService.findCompoundsByCIDs([cid]) >> {[compoundAdapters: compoundAdapters]}
        assert returnedCartCompound?.name == expectedCartCompoundName
        assert returnedCartCompound?.externalId == expectedCartCompoundId

        where:
        label                      | cid  | compoundAdapters  | expectedCartCompoundName | expectedCartCompoundId
        "found a compoundAdapter"  | 123L | [compoundAdapter] | 'name1'                  | 1L
        "no compoundAdapter found" | 123L | []                | null                     | null
    }
}
