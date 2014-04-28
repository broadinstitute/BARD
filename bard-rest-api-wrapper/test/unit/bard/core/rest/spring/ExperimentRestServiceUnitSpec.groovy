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
import bard.core.rest.spring.experiment.*
import bard.core.util.ExternalUrlDTO
import bard.core.util.FilterTypes
import grails.test.mixin.TestFor
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(ExperimentRestService)
class ExperimentRestServiceUnitSpec extends Specification {
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


    void "activitiesByEIDs #label"() {

        when:
        final ExperimentData experimentData = service.activitiesByEIDs(eids, new SearchParams())
        then:
        restTemplate.postForEntity(_ as URI, _ as Map, Activity[].class) >> {new ResponseEntity<Activity[]>(activities,HttpStatus.OK)}
        assert expectResults == (experimentData != null)

        where:
        label            | eids  | expectResults | activities
        "Empty EID list" | []    | false         | []
        "Single EID"     | [123] | true          | [new Activity()]
    }

    void "activitiesByADIDs #label"() {
        when:
        final ExperimentData experimentData = service.activitiesByADIDs(adids, new SearchParams())
        then:
        restTemplate.postForEntity(_ as URI, _ as Map, Activity[].class) >> {new ResponseEntity<Activity[]>(activities,HttpStatus.OK)}
        assert expectResults == (experimentData != null)
        where:
        label            | adids | expectResults | activities
        "Empty EID list" | []    | false         | []
        "Single EID"     | [123] | true          | [new Activity()]
    }

    void "activitiesByCIDs #label"() {
        when:
        final ExperimentData experimentData = service.activitiesByCIDs(cids, new SearchParams())
        then:
        restTemplate.postForEntity(_ as URI, _ as Map, Activity[].class) >> {new ResponseEntity<Activity[]>(activities,HttpStatus.OK)}
        assert expectResults == (experimentData != null)
        where:
        label            | cids  | expectResults | activities
        "Empty EID list" | []    | false         | []
        "Single EID"     | [123] | true          | [new Activity()]
    }

    void "activitiesBySIDs #label"() {
        when:
        final ExperimentData experimentData = service.activitiesBySIDs(sids, new SearchParams())
        then:
        restTemplate.postForEntity(_ as URI, _ as Map, Activity[].class) >> {new ResponseEntity<Activity[]>(activities,HttpStatus.OK)}
        assert expectResults == (experimentData != null)
        where:
        label            | sids  | expectResults | activities
        "Empty EID list" | []    | false         | []
        "Single EID"     | [123] | true          | [new Activity()]
    }

    void "buildURLToExperimentData #label"() {
        when:
        final String url = service.buildURLToExperimentData(searchParams)
        then:
        assert expectedURL == url
        where:
        label                      | searchParams                       | expectedURL
        "With search Params"       | new SearchParams(top: 10, skip: 0) | "http://ncgc/exptdata?skip=0&top=10"
        "With Empty Search Params" | new SearchParams()                 | "http://ncgc/exptdata"
    }

    void "activities with no ETag"() {
        given:
        final Long experimentId = new Long("2")
        final String etag = null
        final Integer top = 10
        final Integer skip = 0
        when:
        final ExperimentData experimentData = service.activities(experimentId, etag, top, skip, [FilterTypes.TESTED])
        then:
        restTemplate.getForEntity(_ as URI, ExperimentData.class) >> {new ResponseEntity<ExperimentData>(new ExperimentData(),HttpStatus.OK)}
        assert experimentData

    }

    void "activities with ETag"() {
        given:
        final Long experimentId = new Long("2")
        final String etag = "etag"
        final Integer top = 10
        final Integer skip = 0
        when:
        final ExperimentData experimentData = service.activities(experimentId, etag, top, skip, [FilterTypes.TESTED])
        then:
        restTemplate.getForEntity(_ as URI, Activity[].class) >> {new ResponseEntity<Activity[]>([new Activity()],HttpStatus.OK)}
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
        restTemplate.getForEntity(_ as URI, Map.class) >> {new ResponseEntity<Map>(expectedMap,HttpStatus.OK)}
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
        restTemplate.getForEntity(_ as URI, ExperimentShow.class) >> {new ResponseEntity<ExperimentShow>(experiment,HttpStatus.OK)}
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
        expectedNumberOfInvocations * restTemplate.exchange(_ as String, _ as HttpMethod, _ as HttpEntity, List.class)
        assert experimentSearchResult == null
        where:
        label                        | eids       | expectedNumberOfInvocations
        "With null eids"             | null       | 0
        "With an empty list of eids" | []         | 0
    }

    void "activities with experimentId and etag"() {
        given:
        final Long experimentId = 200
        final String etag = "etag"
        when:
        ExperimentData experimentData = service.activities(experimentId, etag)
        then:
        restTemplate.getForEntity(_ as URI, Activity[].class) >> {new ResponseEntity<Activity[]>([new Activity()],HttpStatus.OK)}
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
        restTemplate.getForEntity(_ as URI, ExperimentData.class) >> {new ResponseEntity<ExperimentData>(new ExperimentData(activities: [new Activity()]),HttpStatus.OK)}
        assert experimentData
        assert experimentData.activities
        assert experimentData.activities.size() == 1
    }

    void "activities with experiment only"() {
        given:
        final Long experimentId = 200
        final ExperimentSearch experimentSearch = new ExperimentSearch(bardExptId: experimentId)
        when:
        ExperimentData experimentData = service.activities(experimentSearch.bardExptId)
        then:
        restTemplate.getForEntity(_ as URI, ExperimentData.class) >> {new ResponseEntity<ExperimentData>(new ExperimentData(activities: [new Activity()]),HttpStatus.OK)}
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
        String url = service.buildExperimentQuery(experimentId, etag, top, skip, filters)
        then:
        assert url == expectedURL
        where:
        label                  | experimentId | skip | top | etag   | filters              | expectedURL
        "With ETag"            | 2            | 0    | 10  | "etag" | [FilterTypes.TESTED] | "http://ncgc/experiments/2/etag/etag/exptdata?skip=0&top=10&expand=true"
        "With ETag and active" | 2            | 0    | 10  | "etag" | []                   | "http://ncgc/experiments/2/etag/etag/exptdata?skip=0&top=10&filter=active&expand=true"
        "No ETag"              | 2            | 0    | 10  | null   | [FilterTypes.TESTED] | "http://ncgc/experiments/2/exptdata?skip=0&top=10&expand=true"
        "No ETag, Top is zero" | 2            | 0    | 0   | null   | [FilterTypes.TESTED] | "http://ncgc/experiments/2/exptdata?expand=true"
    }

    void "getResourceCount with SearchParams #label"() {
        when:
        int count = service.getResourceCount(searchParams)
        then:
        restTemplate.getForEntity(_ as URI, String.class) >> {new ResponseEntity<String>("2",HttpStatus.OK)}
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
        label                                   | params                                                                                                                                  | expectedURL
        "Params+Skip+Top"                       | new SearchParams(query: "query", skip: 2, top: 5)                                                                                       | 'http://ncgc/search/experiments/?q=query&skip=2&top=5&expand=true'
        "Params+Skip+Top+Filters"               | new SearchParams(query: "query", skip: 2, top: 5, filters: [["num_expt", "6"] as String[]])                                             | 'http://ncgc/search/experiments/?q=query&filter=fq(num_expt:6),&skip=2&top=5&expand=true'
        "Params with no Skip and Top"           | new SearchParams(query: "query")                                                                                                        | 'http://ncgc/search/experiments/?q=query'
        "Params with no Skip and Top + filters" | new SearchParams(query: "query", filters: [["num_expt", '"6"'] as String[]])                                                              | 'http://ncgc/search/experiments/?q=query&filter=fq(num_expt:%226%22),'
        "Params with no Skip and Top + filters" | new SearchParams(query: "query", filters: [["kegg_disease_names", '"Glycerol kinase deficiency (GKD),Hyperglycerolemia"'] as String[]]) | 'http://ncgc/search/experiments/?q=query&filter=fq(kegg_disease_names:%22Glycerol+kinase+deficiency+%28GKD%29%2CHyperglycerolemia%22),'
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


