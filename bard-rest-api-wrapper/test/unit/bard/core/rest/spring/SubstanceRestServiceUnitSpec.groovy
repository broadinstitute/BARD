package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.substances.Substance
import bard.core.rest.spring.substances.SubstanceResult
import bard.core.rest.spring.util.SubstanceSearchType
import grails.test.mixin.TestFor
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(SubstanceRestService)
class SubstanceRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.promiscuityUrl = "badapple"
        service.baseUrl = "http://ncgc"
    }

    void "getSubstanceById #label"() {
        when:
        final Substance foundSubstance = service.getSubstanceById(sid)

        then:
        restTemplate.getForObject(_, _, _) >> {substance}
        assert (foundSubstance != null) == noErrors
        where:
        label                    | sid | substance       | noErrors
        "Existing Substance"     | 179 | new Substance() | true
        "Non-Existing Substance" | -1  | null            | false
    }

    void "getSubstanceById with Exception"() {
        when:
        final Substance foundSubstance = service.getSubstanceById(sid)

        then:
        restTemplate.getForObject(_, _, _) >> {new RestClientException()}
        assert foundSubstance == null
        where:
        label                    | sid | substance | noErrors
        "Non-Existing Substance" | -1  | null      | false
    }

    void "getResourceContext"() {

        when:
        final String resourceContext = service.getResourceContext()
        then:
        assert resourceContext == RestApiConstants.SUBSTANCES_RESOURCE
    }

    void "findExperimentData #label"() {
        when:
        final List<Activity> activities = service.findExperimentData(sids, bardExperimentIds)
        then:
        0 * restTemplate.postForObject(_, _, _)
        assert !activities
        where:
        label                        | sids | bardExperimentIds
        "With null sids"             | null | []
        "With an empty list of sids" | []   | null
        "With null eids"             | null | []
        "With an empty list of eids" | []   | null
    }

    void "findExperimentData - Should return activities"() {
        when:
        final List<Activity> activities = service.findExperimentData(sids, bardExperimentIds)
        then:
        1 * restTemplate.postForObject(_, _, _) >> {[new Activity()]}
        assert activities
        where:
        label                        | sids      | bardExperimentIds
        "With sids and exptdata ids" | [1, 2, 3] | [1, 2, 3]

    }

    void "buildExperimentQuery"() {
        when:
        final String url =
            service.buildExperimentQuery()
        then:
        assert url == "http://ncgc/exptdata"
    }

    void "getSearchResource"() {
        when:
        String searchResource = service.getSearchResource()
        then:
        assert searchResource == "http://ncgc/substances/?"
    }

    void "getResource"() {
        when:
        String searchResource = service.getResource()
        then:
        assert searchResource == "http://ncgc/substances/"
    }

    void "findSubstancesByCid"() {
        given:
        Long cid = 123
        when:
        List<Substance> substances = service.findSubstancesByCid(cid)
        then:
        restTemplate.getForObject(_, _) >> {[new Substance()]}
        assert substances
    }

    void "findExperimentDataBySid"() {
        given:
        final Long sid = 123
        when:
        ExperimentData experimentData = service.findExperimentDataBySid(sid)
        then:
        restTemplate.getForObject(_, _) >> {new ExperimentData()}
        assert experimentData
    }

    void "findExperimentsBySid"() {
        given:
        final Long sid = 123
        when:
        ExperimentSearchResult experimentSearchResult = service.findExperimentsBySid(sid)
        then:
        restTemplate.getForObject(_, _) >> {new ExperimentSearchResult()}
        assert experimentSearchResult
    }

    void "buildSearchURL #label"() {
        when:
        String url = service.buildURLForSearch(substanceType, searchParams)
        then:
        assert url == expectedURL
        where:
        label                               | searchParams                       | expectedURL                                                      | substanceType
        "With Search Params"                | new SearchParams(skip: 0, top: 10) | "http://ncgc/substances/?skip=0&top=10&expand=true&filter=MLSMR" | SubstanceSearchType.MLSMR
        "With Search Params No skip or Top" | new SearchParams()                 | "http://ncgc/substances/?expand=true&filter=MLSMR"               | SubstanceSearchType.MLSMR
        "Without Search Params"             | null                               | "http://ncgc/substances/?expand=true"                            | null
    }

    void "findSubstances"() {
        given:
        final SearchParams searchParam = new SearchParams(skip: 0, top: 10)

        when:
        SubstanceResult substanceResult = service.findSubstances(SubstanceSearchType.MLSMR, searchParam)
        then:
        restTemplate.getForObject(_, _) >> {new SubstanceResult()}
        assert substanceResult

    }

}

