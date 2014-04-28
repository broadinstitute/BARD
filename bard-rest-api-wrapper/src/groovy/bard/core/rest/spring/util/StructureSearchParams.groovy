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

package bard.core.rest.spring.util

import bard.core.SearchParams

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/16/12
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
class StructureSearchParams extends SearchParams {
   // private static final long serialVersionUID = 0xaeddb6dac8a37ae8l;

    /*
     * Different search types supported at the moment
     */
    public enum Type {
        Substructure,
        Superstructure,
        Exact,
        Similarity;
    }

    /*
     * Actual search or we're only care about count
     */
    public enum Method {
        Search,
        Count
    }


    /*
     * Future we support different metrics
     */
    public enum Metric {
        Tanimoto, // similarity
        Dice, // similarity
        Cosine, // similarity
        Hamming; // distance
    }

    protected Type type = Type.Substructure;
    protected Method method = Method.Search;
    protected Metric metric = Metric.Tanimoto;
    protected Double threshold = 0.7; // for similarity

    public StructureSearchParams() {
       super()
    }
   public StructureSearchParams(final StructureSearchParams structureSearchParams){
       super()
       this.threshold = structureSearchParams.threshold
       this.query = structureSearchParams.query
       this.type = structureSearchParams.type
       this.skip = structureSearchParams.skip
       this.top = structureSearchParams.top
       this.method = structureSearchParams.method
       this.metric = structureSearchParams.metric
       this.filters = structureSearchParams.filters //filters are not cloned
   }
    public StructureSearchParams(String query) {
        this(query, Type.Substructure);
    }

    public StructureSearchParams(String query, Type type) {
        super(query);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public StructureSearchParams setType(Type type) {
        this.type = type;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public StructureSearchParams setMethod(Method method) {
        this.method = method;
        return this;
    }

    public Metric getMetric() {
        return metric;
    }

    public StructureSearchParams setMetric(Metric metric) {
        this.metric = metric;
        return this;
    }

    public Double getThreshold() {
        return threshold;
    }

    public StructureSearchParams setThreshold(Double threshold) {
        this.threshold = threshold;
        return this;
    }
}
