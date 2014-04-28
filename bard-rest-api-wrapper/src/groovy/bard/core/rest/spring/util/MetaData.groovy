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

import bard.core.Value
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/23/12
 * Time: 11:29 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MetaData extends JsonUtil {

    @JsonProperty("nhit")
    private Integer nhit;
    @JsonProperty("facets")
    private List<Facet> facets = new ArrayList<Facet>();
    @JsonProperty("queryTime")
    private Integer queryTime;
    @JsonProperty("elapsedTime")
    private Integer elapsedTime;
    @JsonProperty("matchingFields")
    private MatchingFields matchingFields;

    @JsonProperty("scores")
    private Scores scores;


    @JsonProperty("scores")
    public Scores getScores() {
        return this.scores;
    }

    @JsonProperty("scores")
    public void setCounts(Scores scores) {
        this.scores = scores;
    }

    @JsonProperty("nhit")
    public Integer getNhit() {
        return nhit;
    }

    @JsonProperty("nhit")
    public void setNhit(Integer nhit) {
        this.nhit = nhit;
    }

    @JsonProperty("facets")
    public List<Facet> getFacets() {
        return facets;
    }

    @JsonProperty("facets")
    public void setFacets(List<Facet> facets) {
        this.facets = facets;
    }

    @JsonProperty("queryTime")
    public Integer getQueryTime() {
        return queryTime;
    }

    @JsonProperty("queryTime")
    public void setQueryTime(Integer queryTime) {
        this.queryTime = queryTime;
    }

    @JsonProperty("elapsedTime")
    public Integer getElapsedTime() {
        return elapsedTime;
    }

    @JsonProperty("elapsedTime")
    public void setElapsedTime(Integer elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    @JsonProperty("matchingFields")
    public MatchingFields getMatchingFields() {
        return matchingFields;
    }

    @JsonProperty("matchingFields")
    public void setMatchingFields(MatchingFields matchingFields) {
        this.matchingFields = matchingFields;
    }
    /**
     * Use the id of each response to get the score
     * @param key
     * @return
     */
    public Double getScore(String key) {
        if (this.scores) {
            return this.scores.getScore(key)
        }
        return 0.0
    }
    /**
     * Use the id of each response to get the matching field
     * @param key
     * @return
     */
    public NameDescription getMatchingField(String key) {
        if (this.matchingFields) {
            return matchingFields.getNamedField(key)
        }
        return null
    }

    Collection<Value> facetsToValues() {
        Collection<Value> values = []
        for (Facet facet : this.facets) {
            Value value = facet.toValueWithTranslation()
            if (value) {
                values.add(value)
            }
        }
        //Sort any two values based on the their displayOrder child (Value), if they have one.
        return values.sort { Value lhs, Value rhs ->
            String lhsDisplayOrder = lhs.children.find { Value child -> child.id == 'displayOrder' }?.value.toString()
            String rhsDisplayOrder = rhs.children.find { Value child -> child.id == 'displayOrder' }?.value.toString()
            if (lhsDisplayOrder?.isNumber() && rhsDisplayOrder?.isNumber()) {
                return lhsDisplayOrder.toInteger() <=> rhsDisplayOrder.toInteger()
            } else if (lhsDisplayOrder?.isNumber() || rhsDisplayOrder?.isNumber()) {//if one has a displayOder but the other one doesn't, return the one with the displayOrder as first.
                if (lhsDisplayOrder?.isNumber()) {
                    return -1
                } else {
                    return 1
                }
            }
            //If neither has a displayOrder, sort alphabetically by value's label.
            return lhs.id <=> rhs.id
        }
    }

}
