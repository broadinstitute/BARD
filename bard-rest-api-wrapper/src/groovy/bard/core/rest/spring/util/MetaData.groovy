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
            Integer lhsDisplayOrder = lhs.children.find { Value child -> child.id == 'displayOrder' }?.value
            Integer rhsDisplayOrder = rhs.children.find { Value child -> child.id == 'displayOrder' }?.value
            if (lhsDisplayOrder && rhsDisplayOrder) {
                return lhsDisplayOrder <=> rhsDisplayOrder
            } else if (lhsDisplayOrder || rhsDisplayOrder) {//if one has a displayOder but the other one doesn't, return the one with the displayOrder as first.
                if (lhsDisplayOrder) {
                    return 1
                } else {
                    return -1
                }
            }
            //If neither has a displayOrder, sort alphabetically by value's label.
            return lhs.id <=> rhs.id
        }
    }

}
