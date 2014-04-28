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

package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.experiment.Activity
import bard.core.rest.spring.experiment.ExperimentData
import bard.core.rest.spring.experiment.ExperimentSearchResult
import bard.core.rest.spring.substances.Substance
import bard.core.rest.spring.substances.SubstanceResult
import bard.core.rest.spring.util.SubstanceSearchType
import bard.core.util.ExternalUrlDTO
import grails.test.mixin.TestFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(SubstanceRestService)
class SubstanceRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    LoggerService loggerService
    @Shared
    ExternalUrlDTO externalUrlDTO = new ExternalUrlDTO(promiscuityUrl:"badapple", ncgcUrl: "http://ncgc" )

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.externalUrlDTO = externalUrlDTO

        this.loggerService = Mock(LoggerService)
        service.loggerService = this.loggerService
    }

    void "getSubstanceById #label"() {
        when:
        final Substance foundSubstance = service.getSubstanceById(sid)

        then:
        restTemplate.getForEntity(_ as URI, Substance.class) >> {new ResponseEntity<Substance>(substance,HttpStatus.OK)}


        assert (foundSubstance != null) == noErrors
        where:
        label                    | sid | substance       | noErrors
        "Existing Substance"     | 179 | new Substance() | true
        "Non-Existing Substance" | -1  | null            | false
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
        0 * restTemplate.postForEntity(_ as URI, _ as Map, _ as Class)
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
        1 * restTemplate.postForEntity(_ as URI, _ as Map, _ as Class) >> {new ResponseEntity<Activity[]>([new Activity()],HttpStatus.OK)}
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

    void "findSubstancesByCidExpandedSearch"() {
        given:
        Long cid = 123
        when:
        List<Substance> substances = service.findSubstancesByCidExpandedSearch(cid)
        then:
        restTemplate.getForEntity(_ as URI, Substance[].class) >> {new ResponseEntity<Substance[]>([new Substance()],HttpStatus.OK)}
        assert substances


    }

    void "findSubstancesByCid #label"() {
        when:
        List<Long> substances = service.findSubstancesByCid(cid)
        then:
        restTemplate.getForEntity(_ as URI, String[].class) >> {new ResponseEntity<String[]>(expectedSubstances,HttpStatus.OK)}
        assert substances.isEmpty() == expectedResults

        where:
        label                               | cid | expectedSubstances   | expectedResults
        "Return a list of substance"        | 233 | ["/a/123", "/b/234"] | false
        "Return an empty list of substance" | 233 | []                   | true
    }

    void "findExperimentDataBySid"() {
        given:
        final Long sid = 123
        when:
        ExperimentData experimentData = service.findExperimentDataBySid(sid)
        then:
        restTemplate.getForEntity(_ as URI, ExperimentData.class) >> {new ResponseEntity<ExperimentData>(new ExperimentData(),HttpStatus.OK)}
        assert experimentData
    }

    void "findExperimentsBySid"() {
        given:
        final Long sid = 123
        when:
        ExperimentSearchResult experimentSearchResult = service.findExperimentsBySid(sid)
        then:
        restTemplate.getForEntity(_ as URI, ExperimentSearchResult.class) >> {new ResponseEntity<ExperimentSearchResult>(new ExperimentSearchResult(),HttpStatus.OK)}
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
        restTemplate.getForEntity(_ as URI, SubstanceResult.class) >> {new ResponseEntity<SubstanceResult>(new SubstanceResult(),HttpStatus.OK)}
        assert substanceResult

    }

}

