package bard.core.rest.spring.util

import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.experiment.ExperimentSearchResult

@Unroll
class SearchResultUnitSpec extends Specification {

    void "test defaults"() {
        when:
        ExperimentSearchResult experimentSearchResult = new ExperimentSearchResult()
        then:
        assert !experimentSearchResult.etag
        assert experimentSearchResult.numberOfHits == 0
        assert !experimentSearchResult.facets
        assert !experimentSearchResult.getExpes()
        assert !experimentSearchResult.getExperiments()
        assert !experimentSearchResult.getFacetsToValues()
    }
    void "test defaults with etag"() {
        when:
        ExperimentSearchResult experimentSearchResult = new ExperimentSearchResult(etag: "some etag")
        then:
        assert experimentSearchResult.etag
        assert experimentSearchResult.numberOfHits == 0
        assert !experimentSearchResult.facets
        assert !experimentSearchResult.getExpes()
        assert !experimentSearchResult.getExperiments()
    }
}

