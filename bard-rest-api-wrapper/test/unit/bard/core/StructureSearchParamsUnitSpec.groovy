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
import bard.core.rest.spring.util.StructureSearchParams

@Unroll
class StructureSearchParamsUnitSpec extends Specification {


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        final StructureSearchParams currentStructureSearchParams = structureSearchParams
        then:
        currentStructureSearchParams.type == structureSearchParams.type
        currentStructureSearchParams.query == structureSearchParams.query
        currentStructureSearchParams.threshold == 0.7
        currentStructureSearchParams.method == StructureSearchParams.Method.Search;
        currentStructureSearchParams.metric == StructureSearchParams.Metric.Tanimoto;

        where:
        label                    | structureSearchParams
        "Empty Constructor"      | new StructureSearchParams()
        "Single arg Constructor" | new StructureSearchParams("query")
        "Two arg Constructor"    | new StructureSearchParams("query", StructureSearchParams.Type.Exact)
    }

    void "test all setters #label"() {
        given:
        final StructureSearchParams currentStructureSearchParams = new StructureSearchParams()
        final StructureSearchParams.Method method = StructureSearchParams.Method.Count
        final StructureSearchParams.Metric metric = StructureSearchParams.Metric.Cosine
        final Double threshold = 2.0
        final StructureSearchParams.Type type = StructureSearchParams.Type.Similarity
        when:
        currentStructureSearchParams.setMethod(method)
        currentStructureSearchParams.setMetric(metric)
        currentStructureSearchParams.setThreshold(threshold)
        currentStructureSearchParams.setType(type)
        then:

        currentStructureSearchParams.threshold == threshold
        currentStructureSearchParams.method == method
        currentStructureSearchParams.metric == metric
        currentStructureSearchParams.type == type


    }


}

