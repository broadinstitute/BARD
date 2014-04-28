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
import bard.core.SuggestParams
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.*
import bard.core.util.ExternalUrlDTO
import grails.test.mixin.TestFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(AssayRestService)
class AssayRestServiceUnitSpec extends Specification {
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

    void "searchAssaysByCapIds #label"() {
        when:
        List<Assay> assays = service.searchAssaysByCapIds(searchParams, etags)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>([new Assay()],HttpStatus.OK)}
        assert (!assays.isEmpty()) == expected
        where:
        label        | searchParams                       | etags          | expected
        "With ETags" | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | true

    }


    void "searchAssaysByCapIds(searchParams, etags) #label"() {
        when:
        List<Assay> assays = service.searchAssaysByCapIds(searchParams, etags)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>([new Assay()],HttpStatus.OK)}
        assert (!assays.isEmpty()) == expected
        where:
        label           | searchParams                       | etags          | expected
        "With ETags"    | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | true
        "With No ETags" | new SearchParams(skip: 0, top: 10) | [:]            | false

    }

    void "searchAssaysByCapIds(final List<Long> capIds, final SearchParams searchParams) #label"() {
        when:
        AssayResult assayResult = service.searchAssaysByCapIds(capIds, searchParams)
        then:
        assert (assayResult != null) == expected
        where:
        label             | searchParams                       | capIds | expected
        "With No Cap IDs" | new SearchParams(skip: 0, top: 10) | []     | false

    }

    void "buildQueryForETag #label"() {
        when:
        final String resourceURL = service.buildQueryForETag(searchParams, etag)
        then:
        assert resourceURL == expectedURL
        where:
        label            | searchParams                       | etag  | expectedURL
        "With ETag"      | new SearchParams(skip: 0, top: 10) | "123" | "http://ncgc/assays/etag/123?skip=0&top=10&expand=true"
        "With Null ETag" | new SearchParams(skip: 0, top: 10) | null  | ""

    }

    void "buildSearchByCapIdURLs #label"() {

        when:
        String resourceURL = service.buildSearchByCapIdURLs(capIds, searchParams, "capAssayId:")
        then:
        assert resourceURL == expectedURL
        where:
        label           | searchParams                       | capIds | expectedURL
        "Two CAP Ids"   | new SearchParams(skip: 0, top: 10) | [1, 2] | "http://ncgc/search/assays/?q=capAssayId%3A1+or+capAssayId%3A2&skip=0&top=10&expand=true"
        "Single CAP ID" | new SearchParams(skip: 0, top: 10) | [1]    | "http://ncgc/search/assays/?q=capAssayId%3A1&skip=0&top=10&expand=true"
        "No CAP ID"     | new SearchParams(skip: 0, top: 10) | []     | "http://ncgc/search/assays/?q=&skip=0&top=10&expand=true"

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
        BardAnnotation annotations = service.findAnnotations(adid)
        then:
        restTemplate.getForEntity(_ as URI, BardAnnotation.class) >> {new ResponseEntity<BardAnnotation>(new BardAnnotation(),HttpStatus.OK)}
        assert annotations
    }

    void "getExperimentById #label"() {
        when:
        ExpandedAssay expandedAssay = service.getAssayById(adid)
        then:
        restTemplate.getForEntity(_ as URI, ExpandedAssay.class) >> {new ResponseEntity<ExpandedAssay>(assay,HttpStatus.OK)}
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
        0 * restTemplate.postExchange(_, _, _, _)
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
        restTemplate.getForEntity(_ as URI, AssayResult.class) >> {new ResponseEntity<AssayResult>(new AssayResult(),HttpStatus.OK)}
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
        restTemplate.getForEntity(_ as URI, Map.class) >> {new ResponseEntity<Map>(["a": ["b"]],HttpStatus.OK)}
        assert suggest == expectedMap
    }
}


