package bard.core

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class SearchParamsUnitSpec extends Specification {


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructors #label"() {
        when:
        final SearchParams currentSearchParams = searchParams
        then:
        assert currentSearchParams.getQuery() == expectedQuery
        where:
        label                    | searchParams              | expectedQuery
        "Empty Constructor"      | new SearchParams()        | null
        "Single arg Constructor" | new SearchParams("query") | "query"
    }

    void "test all setters #label"() {
        given:
        final SearchParams currentSearchParams = new SearchParams()

        when:
        currentSearchParams.setQuery("query")

        then:
        assert currentSearchParams.getQuery() == "query"

    }
}

