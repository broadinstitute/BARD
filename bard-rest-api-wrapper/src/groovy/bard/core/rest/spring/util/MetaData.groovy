package bard.core.rest.spring.util

import bard.core.Value
import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import org.apache.commons.lang.builder.ToStringBuilder

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
        this.scores= scores;
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
    Collection<Value> facetsToValues() {
        Collection<Value> values = []
        for (Facet facet : this.facets) {
            Value value = facet.toValue()
            if (value) {
                values.add(value)
            }
        }
        return values;
    }

}
