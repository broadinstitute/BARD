package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.AssayAnnotation
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.assays.ExpandedAssayResult
import bard.core.rest.spring.assays.AssayResult
import grails.test.mixin.TestFor
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(AssayRestService)
class AssayRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate


    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.promiscuityUrl = "badapple"
        service.baseUrl = "http://ncgc"
    }

    void "firstETagFromMap #label"() {
        when:
        final String etag = service.firstETagFromMap(etags)
        then:
        assert etag == expectedETag

        where:
        label                | etags               | expectedETag
        "Etags is empty"     | [:]                 | null
        "Etags is not empty" | ["e22002": "value"] | "e22002"
    }

    void "validatePutETag #label"() {
        when:
        service.validatePutETag(etag, ids)
        then:
        thrown(IllegalArgumentException)

        where:
        label                          | etag     | ids
        "ETag is null"                 | null     | [123]
        "Ids is null"                  | "e22002" | []
        "Ids is null and ETag is null" | ""       | null
    }

    void "findAnnotations"() {
        given:
        Long adid = 200
        when:
        List<AssayAnnotation> annotations = service.findAnnotations(adid)
        then:
        this.restTemplate.getForObject(_, _) >> {[new AssayAnnotation()]}
        assert annotations
    }

    void "getExperimentById #label"() {
        when:
        ExpandedAssay expandedAssay = service.getAssayById(adid)
        then:
        restTemplate.getForObject(_, _, _) >> {assay}
        assert (expandedAssay != null) == noErrors
        where:
        label                | adid | assay               | noErrors
        "Existing Assay"     | 179  | new ExpandedAssay() | true
        "Non_Existing Assay" | -1   | null                | false
    }

    void "searchAssaysByIds #label"() {
        when:
        final ExpandedAssayResult expandedAssayResult = service.searchAssaysByIds(adids)
        then:
        0 * restTemplate.exchange(_, _, _, _)
        assert expandedAssayResult == null
        where:
        label                         | adids
        "With null adids"             | null
        "With an empty list of adids" | []
    }

    void "findAssaysByFreeTextSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair")
        when:
        final AssayResult assayResult =
            service.findAssaysByFreeTextSearch(searchParams)
        then:
        restTemplate.getForObject(_, _) >> {new AssayResult()}
        assert assayResult != null
    }

    void "getResourceContext"() {
        when:
        String resourceContext = service.getResourceContext()

        then:
        assert resourceContext == RestApiConstants.ASSAYS_RESOURCE
    }


    void "getSearchResource"() {
        when:
        String searchResource = service.getSearchResource()
        then:
        assert searchResource == "http://ncgc/search/assays/?"
    }

    void "getResource"() {
        when:
        String resource = service.getResource()
        then:
        assert resource == "http://ncgc/assays/"

    }

    void "buildSuggestQuery"() {
        given:
        SuggestParams params = new SuggestParams("query", 2)
        when:
        String query = service.buildSuggestQuery(params)
        then:
        assert query == "http://ncgc/search/assays/suggest?q=query&top=2"
    }

    void "suggest"() {
        given:
        Map expectedMap = ["a": ["b"]]
        SuggestParams suggestParams = new SuggestParams(query: "Stuff", numSuggestion: 10)
        when:
        final Map<String, List<String>> suggest = service.suggest(suggestParams)
        then:
        restTemplate.getForObject(_, _) >> {["a": ["b"]]}
        assert suggest == expectedMap
    }
}


