package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SuggestParamsUnitSpec extends Specification {


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        final SuggestParams currentSuggestParams = suggestParams
        then:
        assert currentSuggestParams.getNumSuggestion() == expectedSuggestions
        assert currentSuggestParams.getQuery() == expectedQuery
        where:
        label                 | suggestParams                 | expectedSuggestions | expectedQuery
        "Empty Constructor"   | new SuggestParams()           | 10                   | null
        "Two arg Constructor" | new SuggestParams("query", 2) | 2                   | "query"
    }

    void "test all setters #label"() {
        given:
        final SuggestParams currentSuggestParams =  new SuggestParams()

        when:
        currentSuggestParams.setNumSuggestion(1)
        then:
        assert currentSuggestParams.getNumSuggestion() == 1
    }
}

