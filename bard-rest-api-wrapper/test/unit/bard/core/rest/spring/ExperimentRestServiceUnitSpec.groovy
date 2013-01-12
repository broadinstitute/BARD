package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import grails.test.mixin.TestFor
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.experiment.*
import bard.core.helper.LoggerService

@Unroll
@TestFor(ExperimentRestService)
class ExperimentRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    LoggerService loggerService

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.promiscuityUrl = "badapple"
        service.baseUrl = "http://ncgc"
        this.loggerService = Mock(LoggerService)
        service.loggerService = this.loggerService
    }

    void "activities with no ETag"() {
        given:
        final Long experimentId = new Long("2")
        final String etag = null
        final Integer top = 10
        final Integer skip = 0
        when:
        final ExperimentData experimentData = service.activities(experimentId, etag, top, skip)
        then:
        this.restTemplate.getForObject(_, _) >> {new ExperimentData()}
        assert experimentData

    }

    void "activities with ETag"() {
        given:
        final Long experimentId = new Long("2")
        final String etag = "etag"
        final Integer top = 10
        final Integer skip = 0
        when:
        final ExperimentData experimentData = service.activities(experimentId, etag, top, skip)
        then:
        this.restTemplate.getForObject(_, _) >> {[new Activity()]}
        assert experimentData

    }

    void "getResourceContext"() {
        when:
        String resourceContext = service.getResourceContext()

        then:
        assert resourceContext == RestApiConstants.EXPERIMENTS_RESOURCE
    }

    void "compoundsForExperiment #label"() {
        when:
        List<Long> ids = service.compoundsForExperiment(eid)
        then:
        restTemplate.getForObject(_, _) >> {expectedMap}
        assert ids == expectedIds
        where:
        label                   | eid | expectedMap                                                               | expectedIds
        "With 'collection' key" | 200 | ["collection": ["/compounds/2123", "/compounds/4781", "/compounds/5342"]] | [2123, 4781, 5342]
        "No collection key"     | 200 | ["someOtherKey": ["/compounds/2123"]]                                     | []


    }

    void "getExperimentById #label"() {
        when:
        ExperimentShow experimentShow = service.getExperimentById(eid)
        then:
        restTemplate.getForObject(_, _, _) >> {experiment}
        assert (experimentShow != null) == noErrors
        where:
        label                     | eid | experiment           | noErrors
        "Existing Experiment"     | 179 | new ExperimentShow() | true
        "Non_Existing Experiment" | -1  | null                 | false
    }

    void "searchExperimentsByIds #label"() {
        when:
        final ExperimentSearchResult experimentSearchResult = service.searchExperimentsByIds(eids)
        then:
        0 * restTemplate.postExchange(_, _, _, _)
        assert experimentSearchResult == null
        where:
        label                        | getExperiments
        "With null eids"             | null
        "With an empty list of eids" | []
    }

    void "activities with experimentId and etag"() {
        given:
        final Long experimentId = 200
        final String etag = "etag"
        when:
        ExperimentData experimentData = service.activities(experimentId, etag)
        then:
        this.restTemplate.getForObject(_, _) >> {[new Activity()]}
        assert experimentData
        assert experimentData.activities
        assert experimentData.activities.size() == 1

    }

    void "activities with experimentId only"() {
        given:
        final Long experimentId = 200
        when:
        ExperimentData experimentData = service.activities(experimentId)
        then:
        this.restTemplate.getForObject(_, _) >> {new ExperimentData(activities: [new Activity()])}
        assert experimentData
        assert experimentData.activities
        assert experimentData.activities.size() == 1
    }

    void "activities with experiment only"() {
        given:
        final Long experimentId = 200
        final ExperimentSearch experimentSearch = new ExperimentSearch(exptId: experimentId)
        when:
        ExperimentData experimentData = service.activities(experimentSearch.id)
        then:
        this.restTemplate.getForObject(_, _) >> {new ExperimentData(activities: [new Activity()])}
        assert experimentData
        assert experimentData.activities
        assert experimentData.activities.size() == 1
    }

    void "getSearchResource"() {
        when:
        String searchResource = service.getSearchResource()
        then:
        assert searchResource == "http://ncgc/search/experiments/?"
    }

    void "getResource"() {
        when:
        String resource = service.getResource()
        then:
        assert resource == "http://ncgc/experiments/"

    }

    void "buildExperimentQuery #label"() {

        when:
        String url = service.buildExperimentQuery(experimentId, etag, top, skip)
        then:
        assert url == expectedURL
        where:
        label                  | experimentId | skip | top | etag   | expectedURL
        "With ETag"            | 2            | 0    | 10  | "etag" | "http://ncgc/experiments/2/etag/etag/exptdata?skip=0&top=10&expand=true"
        "No ETag"              | 2            | 0    | 10  | null   | "http://ncgc/experiments/2/exptdata?skip=0&top=10&expand=true"
        "No ETag, Top is zero" | 2            | 0    | 0   | null   | "http://ncgc/experiments/2/exptdata?expand=true"
    }

    void "getResourceCount with SearchParams #label"() {
        when:
        int count = service.getResourceCount(searchParams)
        then:
        this.restTemplate.getForObject(_, _) >> {"2"}
        assert count == 2
        where:
        label       | searchParams
        "Top == 10" | new SearchParams(top: 10, skip: 0)
        "Top == 0"  | new SearchParams()

    }

    void "buildQueryForCollectionOfETags"() {
        when:
        String url = service.buildQueryForCollectionOfETags(10, 10)
        then:
        assert url == "http://ncgc/experiments/etag?skip=10&top=10&expand=true"
    }

    void "buildETagQuery"() {
        given:
        String etag = "AB100ET"
        when:
        String url = service.buildETagQuery(etag)
        then:
        assert url == "etag/AB100ET"
    }

    void "addTopAndSkip default skip and top #label"() {
        when:
        String url = service.addTopAndSkip("resource", expand)
        then:
        assert url == expectedURL
        where:
        label             | expand | expectedURL
        "Expand is true"  | true   | "resource?skip=0&top=10&expand=true"
        "Expand is false" | false  | "resource?skip=0&top=10"
    }

    void "addTopAndSkip #label"() {
        when:
        String url = service.addTopAndSkip("resource", expand, top, skip)
        then:
        assert url == expectedURL
        where:
        label                          | expand | top | skip | expectedURL
        "expand=true, skip =10,top=10" | true   | 10  | 10   | "resource?skip=10&top=10&expand=true"
        "expand=false, skip=10,top=10" | false  | 10  | 10   | "resource?skip=10&top=10"
    }

    void "getParentETag #label"() {
        when:
        String minVal = AbstractRestService.getParentETag(map)
        then:
        assert minVal == expectedMinVal
        where:
        label           | map                      | expectedMinVal
        "Empty Map"     | [:]                      | ""
        "Non-Empty Map" | [key1: 100L, key2: 300L] | "key1"
    }

    void "extractETagFromResponseHeader #label"() {
        given:
        final HttpHeaders headers = new HttpHeaders(headersMap, true)
        when:
        service.extractETagsFromResponseHeader(headers, skip, etags)
        then:
        assert etags.size() == expectedETagMapSize
        where:
        label                                         | headersMap                     | etags      | skip | expectedETagMapSize
        "Has ETag in header, with Empty Etag Map"     | ["ETag": ["someValue"]]        | [:]        | 2l   | 1
        "Has ETag in header, with Non-Empty Etag Map" | ["ETag": ["someValue"]]        | ["a": "b"] | 2l   | 2
        "Has No ETag in header, with Empty Etag Map"  | ["Some Header": ["someValue"]] | [:]        | 2l   | 0
    }

    void "extractETagFromResponseHeader with null map"() {
        given:
        final HttpHeaders headers = new HttpHeaders(headersMap, true)
        when:
        service.extractETagsFromResponseHeader(headers, skip, etags)
        then:
        assert etags == null
        where:
        label              | headersMap                     | etags | skip
        "Etag Map is null" | ["Some Header": ["someValue"]] | null  | 2
    }

    void "addETagsToHTTPHeader #label"() {
        given:
        final HttpHeaders headers = new HttpHeaders()
        when:
        service.addETagsToHTTPHeader(headers, etags)
        then:
        assert headers.getFirst("If-Match") == expectedValue
        where:
        label        | etags                    | expectedValue
        "With Etags" | [key1: 100L, key2: 300L] | "\"key1\""
        "No Etags"   | null                     | null
    }

    void "buildSearchURL #label"() {
        when:
        String url = service.buildSearchURL(params)
        then:
        assert url == expectedURL
        where:
        label                                   | params                                                                                      | expectedURL
        "Params+Skip+Top"                       | new SearchParams(query: "query", skip: 2, top: 5)                                           | "http://ncgc/search/experiments/?q=query&skip=2&top=5&expand=true"
        "Params+Skip+Top+Filters"               | new SearchParams(query: "query", skip: 2, top: 5, filters: [["num_expt", "6"] as String[]]) | "http://ncgc/search/experiments/?q=query&filter=fq(num_expt:6),&skip=2&top=5&expand=true"
        "Params with no Skip and Top"           | new SearchParams(query: "query")                                                            | "http://ncgc/search/experiments/?q=query"
        "Params with no Skip and Top + filters" | new SearchParams(query: "query", filters: [["num_expt", "6"] as String[]])                  | "http://ncgc/search/experiments/?q=query&filter=fq(num_expt:6),"


    }

    void "buildFilters #label"() {
        when:
        String url = service.buildFilters(params)
        then:
        assert url == expectedURL
        where:
        label                 | params                                                                     | expectedURL
        "Params+No Filters"   | new SearchParams(query: "query", skip: 2, top: 5)                          | ""
        "Params with filters" | new SearchParams(query: "query", filters: [["num_expt", "6"] as String[]]) | "&filter=fq(num_expt:6)"
    }


}


