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

package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ServiceParamsUnitSpec extends Specification {


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        final ServiceParams currentServiceParams = serviceParams
        then:
        assert currentServiceParams.depth == expectedDepth
        assert currentServiceParams.skip == expectedSkip
        assert currentServiceParams.top == expectedTop
        assert !currentServiceParams.partial
        assert !currentServiceParams.ordering
        where:
        label                    | serviceParams                               | expectedDepth | expectedSkip | expectedTop
        "Empty Constructor"      | new ServiceParams()                         | 3             | null         | null
        "Single arg Constructor" | new ServiceParams(2)                        | 2             | null         | null
        "Two arg Constructor"    | new ServiceParams(new Long(2), new Long(2)) | 3             | 2            | 2
    }

    void "test all setters #label"() {
        given:
        final ServiceParams currentServiceParams = new ServiceParams()
        final Integer maxDepth = 2
        final String ordering = "ordering"
        final Long skip = 2
        final Long top = 8
        when:
        currentServiceParams.setMaxDepth(maxDepth)
        currentServiceParams.setOrdering(ordering)
        currentServiceParams.setPartial(true)
        currentServiceParams.setSkip(skip)
        currentServiceParams.setTop(top)

        then:
        currentServiceParams.maxDepth == maxDepth
        currentServiceParams.ordering == ordering
        currentServiceParams.skip == skip
        currentServiceParams.top == top
        assert currentServiceParams.partial
    }
}

