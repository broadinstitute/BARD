package bard.core.rest

import bard.core.Compound
import bard.core.ServiceParams
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RESTSearchResultUnitSpec extends Specification {
    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test findNextTopValue #label"() {
        given:
        final RESTSearchResult<Compound> restSearchResult =
            new RESTSearchResult<Compound>()
        when:
        int top = restSearchResult.findNextTopValue(skip, ratio)
        then:
        assert top == expectedTop
        assert !restSearchResult.getETag()
        where:
        label          | skip | ratio | expectedTop
        "Skip == 10"   | 10   | 10    | 10
        "skip == 1000" | 1000 | 10    | 10
        "skip == 1001" | 1001 | 10    | 1000

    }

    void "next with Illegal Argument Exception #label"() {
        given:
        final RESTSearchResult<Compound> restSearchResult =
            new RESTSearchResult<Compound>()
        when:
        restSearchResult.next(top, skip)
        then:
        thrown(IllegalArgumentException)

        where:
        label          | top | skip
        "Top is zero"  | -1  | 10
        "Skip is zero" | 10  | -3
    }

    void "next #label"() {
        final RESTSearchResult<Compound> restSearchResult =
            new RESTSearchResult<Compound>()
        restSearchResult.count = count
        restSearchResult.searchResults = results
        when:
        final List<Compound> next = restSearchResult.next(top, skip)
        then:
        assert next.size() == resultSize

        where:
        label                       | top | skip | resultSize | count | results
        "Skip > Count"              | 0   | 10   | 0          | 0     | []
        "Skip == Count==resultSize" | 0   | 2    | 0          | 2     | [new Compound("1"), new Compound("2")]
        "Skip == Count< resultSize" | 0   | 1    | 0          | 1     | [new Compound("1"), new Compound("2")]
        "Skip + Top > Count"        | 2   | 3    | 1          | 4     | [new Compound("1"), new Compound("2"), new Compound("3"), new Compound("4")]
    }

    void "build No Params #label"() {
        given:
        RESTAbstractEntityService restAbstractEntityService = Mock(RESTAbstractEntityService.class)
        final RESTSearchResult<Compound> restSearchResult =
            new RESTSearchResult<Compound>()
        restSearchResult.restAbstractEntityService = restAbstractEntityService

        when:
        restSearchResult.build()
        then:
        restAbstractEntityService.get(_, _, _, _, _, _) >> {results}
        assert restSearchResult.searchResults.size() == count
        where:
        label                       | count | results
        "Skip > Count"              | 0     | []
        "Skip == Count==resultSize" | 2     | [new Compound("1"), new Compound("2")]
        "Skip > 500"                | 2     | [new Compound("1"), new Compound("2")]
    }

    void "build with Params #label"() {
        given:
        final RESTAbstractEntityService restAbstractEntityService = Mock()
        final RESTSearchResult<Compound> restSearchResult =
            new RESTSearchResult<Compound>()
        restSearchResult.restAbstractEntityService = restAbstractEntityService
        restSearchResult.params = params

        when:
        restSearchResult.build()
        then:
        restAbstractEntityService.get(_, _, _, _, _, _) >> {results}
        assert restSearchResult.searchResults.size() == count
        where:
        label                               | params                  | count | results
        "Get method return empty list"      | new ServiceParams(2, 2) | 0     | []
        "Get method returns non-empty list" | new ServiceParams(2, 2) | 2     | [new Compound("1"), new Compound("2")]
    }

    void "addETag #label"() {
        given:
        final RESTAbstractEntityService restAbstractEntityService = Mock()
        final RESTSearchResult<Compound> restSearchResult =
            new RESTSearchResult<Compound>()
        restSearchResult.restAbstractEntityService = restAbstractEntityService
        restSearchResult.etag = etag
        when:
        restSearchResult.addETag(etags)

        then:
        assert restSearchResult.etag == expectedETag
        where:
        label                                 | etags                 | etag          | expectedETag
        "ETags is Empty and etag is null"     | []                    | null          | null
        "ETags is empty and etag is non-null" | []                    | "some string" | "some string"
        "ETags is not empty and etag is null" | ["Some Other String"] | null          | "Some Other String"
        "Both are not empty"                  | ["Some Other String"] | "some string" | "some string"
    }
}

