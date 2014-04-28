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
import bard.core.exceptions.RestApiException
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.AssayResult
import bard.core.rest.spring.util.ETag
import bard.core.rest.spring.util.ETagCollection
import bard.core.rest.spring.util.Facet
import bard.core.rest.spring.util.StructureSearchParams
import grails.test.mixin.TestFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.ExecutorService
import java.util.concurrent.FutureTask

import bard.core.rest.spring.compounds.*
import bard.core.util.ExternalUrlDTO

@Unroll
@TestFor(CompoundRestService)
class CompoundRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate
    ExecutorService executorService
    LoggerService loggerService
    @Shared
    ExternalUrlDTO externalUrlDTO = new ExternalUrlDTO(promiscuityUrl:"badapple", ncgcUrl: "http://ncgc" )

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        this.executorService = Mock(ExecutorService)
        service.executorService = this.executorService
        service.restTemplate = this.restTemplate
        service.externalUrlDTO = externalUrlDTO

        this.loggerService = Mock(LoggerService)
        service.loggerService = this.loggerService
    }

    void "searchCompoundsCapIds #label"() {
        when:
        List<Compound> compounds = service.searchCompoundsByCids(searchParams, etags)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>([new Compound()],HttpStatus.OK)}
        assert (!compounds.isEmpty()) == expected
        where:
        label        | searchParams                       | etags          | expected
        "With ETags" | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | true

    }


    void "searchCompoundsByCids(searchParams, etags) #label"() {
        when:
        List<Compound> compounds = service.searchCompoundsByCids(searchParams, etags)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>([new Compound()],HttpStatus.OK)}
        assert (!compounds.isEmpty()) == expected
        where:
        label           | searchParams                       | etags          | expected
        "With ETags"    | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | true
        "With No ETags" | new SearchParams(skip: 0, top: 10) | [:]            | false

    }

    void "findProbes #label"() {
        when:
        Compound probe = service.findProbe(mlNumber)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>(compounds,HttpStatus.OK)}
        assert (probe != null) == expected
        where:
        label             | mlNumber | compounds        | expected
        "Existing Probe"  | "ML1001" | [new Compound()] | true
        "Not Found Probe" | "ML2002" | []               | false

    }

    void "searchCompoundsByCids(final List<Long> capIds, final SearchParams searchParams) #label"() {
        when:
        CompoundResult compoundResult = service.searchCompoundsByCids(cids, searchParams)
        then:
        assert (compoundResult != null) == expected
        where:
        label          | searchParams                       | cids | expected
        "With No CIDs" | new SearchParams(skip: 0, top: 10) | []   | false

    }

    void "buildQueryForETag #label"() {
        when:
        final String resourceURL = service.buildQueryForETag(searchParams, etag)
        then:
        assert resourceURL == expectedURL
        where:
        label            | searchParams                       | etag  | expectedURL
        "With ETag"      | new SearchParams(skip: 0, top: 10) | "123" | "http://ncgc/compounds/etag/123?skip=0&top=10&expand=true"
        "With Null ETag" | new SearchParams(skip: 0, top: 10) | null  | ""

    }

    void "buildSearchByCIdURLs #label"() {

        when:
        String resourceURL = service.buildSearchByCapIdURLs(capIds, searchParams, "cid:")
        then:
        assert resourceURL == expectedURL
        where:
        label        | searchParams                       | capIds | expectedURL
        "Two cids"   | new SearchParams(skip: 0, top: 10) | [1, 2] | "http://ncgc/search/compounds/?q=cid%3A1+or+cid%3A2&skip=0&top=10&expand=true"
        "Single cid" | new SearchParams(skip: 0, top: 10) | [1]    | "http://ncgc/search/compounds/?q=cid%3A1&skip=0&top=10&expand=true"
        "No CID"     | new SearchParams(skip: 0, top: 10) | []     | "http://ncgc/search/compounds/?q=&skip=0&top=10&expand=true"

    }

    void "validateSimilarityThreshold"() {
        when:
        service.validateSimilarityThreshold(null)
        then:
        thrown(IllegalArgumentException)
    }

    void "getResourceContext"() {
        when:
        String resourceContext = service.getResourceContext()

        then:
        assert resourceContext == RestApiConstants.COMPOUNDS_RESOURCE
    }

    void "getSearchResource"() {
        when:
        String searchResource = service.getSearchResource()
        then:
        assert searchResource == "http://ncgc/search/compounds/?"
    }

    void "getResource"() {
        when:
        String resource = service.getResource()
        then:
        assert resource == "http://ncgc/compounds/"

    }

    void "buildPromiscuityScoreURL"() {
        when:
        String promiscuityScoreURL = service.buildPromiscuityScoreURL()
        then:
        assert promiscuityScoreURL == "badapple{cid}?expand={expand}&repr={mediaType}"
    }

    void "buildQueryForTestedAssays #label"() {
        when:
        String constructedURL = service.buildQueryForTestedAssays(cid, isActiveOnly)
        then:
        assert constructedURL == expectedURL

        where:
        label                 | cid | isActiveOnly | expectedURL
        "With CID + Active"   | 22  | true         | "http://ncgc/compounds/22/assays?expand=true&filter=active"
        "With CID + InActive" | 22  | false        | "http://ncgc/compounds/22/assays?expand=true"
    }

    void "buildStructureSearchURL #label"() {
        given:
        final StructureSearchParams structureSearchParam = new StructureSearchParams("query", structureSearchType)
        if (withSkip) {
            structureSearchParam.setSkip(2)
        }
        when:
        final String url = service.buildStructureSearchURL(structureSearchParam, false)
        then:
        assert url == expectedURL
        where:
        label             | withSkip | structureSearchType                       | expectedURL
        "Sub Structure"   | true     | StructureSearchParams.Type.Substructure   | "http://ncgc/compounds/?filter=query[structure]&type=sub&expand=true&top=100&skip=2"
        "Super Structure" | false    | StructureSearchParams.Type.Superstructure | "http://ncgc/compounds/?filter=query[structure]&type=sup&expand=true&top=100&skip=0"
        "Exact"           | false    | StructureSearchParams.Type.Exact          | "http://ncgc/compounds/?filter=query[structure]&type=exact&expand=true&top=100&skip=0"
        "Similarity"      | false    | StructureSearchParams.Type.Similarity     | "http://ncgc/compounds/?filter=query[structure]&type=sim&cutoff=0.700&expand=true&top=100&skip=0"
    }

    void "structureSearch #label"() {
        given:
        FutureTask<CompoundResult> compoundResultTask = Mock(FutureTask)
        FutureTask<Long> numberOfHitsTask = Mock(FutureTask)
        final List<FutureTask<Object>> results = [numberOfHitsTask, compoundResultTask]
        final StructureSearchParams structureSearchParam = new StructureSearchParams("query", StructureSearchParams.Type.Substructure)
        final Map<String, Long> eTags = [:]
        when:
        CompoundResult compoundSearchResult = service.structureSearch(structureSearchParam, eTags, nhits)
        then:
        results.get(0) >> {numberOfHitsTask}
        results.get(1) >> {compoundResultTask}
        numberOfHitsTask.get() >> {new Long("2")}
        compoundResultTask.get() >> {new CompoundResult()}
        executorService.invokeAll(_, _, _) >> {results}
        restTemplate.getForObject(_, _, [:]) >> {"2"}
        restTemplate.postExchange(_, _, _, _) >> {new ResponseEntity(HttpStatus.ACCEPTED)}
        assert compoundSearchResult
        assert compoundSearchResult.numberOfHits == 2
        where:
        label                  | nhits
        "Number of hits == -1" | -1
        "Number of hits == 0"  | 0
        //"Number of hits == 10" | 10

    }

    void "handleCompoundByIdFutures #label"() {
        given:
        FutureTask<CompoundAnnotations> compoundAnnotationsTask = Mock(FutureTask)
        FutureTask<Compound> compoundTask = Mock(FutureTask)
        when:
        Compound compound = service.handleCompoundByIdFutures([compoundAnnotationsTask, compoundTask])
        then:
        compoundTask.get() >> {null}
        compoundAnnotationsTask.get() >> {null}
        assert !compound


    }

    void "structureSearch nhits #label"() {
        given:
        FutureTask<CompoundResult> compoundResultTask = Mock(FutureTask)

        final List<FutureTask<Object>> results = [compoundResultTask]
        final StructureSearchParams structureSearchParam = new StructureSearchParams("query", StructureSearchParams.Type.Substructure)
        final Map<String, Long> eTags = [:]
        when:
        CompoundResult compoundSearchResult = service.structureSearch(structureSearchParam, eTags, nhits)
        then:
        results.get(0) >> {compoundResultTask}
        compoundResultTask.get() >> {new CompoundResult()}
        executorService.invokeAll(_, _, _) >> {results}
        restTemplate.postExchange(_, _, _, _) >> {new ResponseEntity(HttpStatus.ACCEPTED)}
        assert compoundSearchResult
        assert compoundSearchResult.numberOfHits == 10
        where:
        label                  | nhits
        "Number of hits == 10" | 10

    }

    void "doStructureSearch"() {
        given:
        final StructureSearchParams structureSearchParam = new StructureSearchParams("query", StructureSearchParams.Type.Substructure)
        final Map<String, Long> eTags = [:]
        when:
        CompoundResult compoundSearchResult = service.doStructureSearch(structureSearchParam, eTags)
        then:
        restTemplate.exchange(_, _, _, _) >> {new ResponseEntity(HttpStatus.ACCEPTED)}
        assert compoundSearchResult

    }

    void "handleStructureSearchFutures with Exception"() {
        when:
        final CompoundResult futures = service.handleStructureSearchFutures([1, 2])
        then:
        thrown(RestApiException)

    }

    void "handleStructureSearchFutures #label"() {
        given:
        FutureTask<Long> numberHitsFutureTask = Mock(FutureTask)
        FutureTask<CompoundResult> compoundResultFutureTask = Mock(FutureTask)

        List<FutureTask<Object>> futures = [numberHitsFutureTask, compoundResultFutureTask]
        when:
        final CompoundResult compoundResult = service.handleStructureSearchFutures(futures, numberHits)
        then:
        numberTimesExecuted * numberHitsFutureTask.get() >> {expectedNumberOFHits}
        compoundResultFutureTask.get() >> {new CompoundResult()}
        assert compoundResult
        assert compoundResult.numberOfHits == expectedNumberOFHits
        where:
        label                       | numberHits | expectedNumberOFHits | numberTimesExecuted
        "nHits == -1"               | -1         | 3                    | 1
        "nHits == 0"                | 0          | 3                    | 1
        "expectedNumberOFHits == 0" | 0          | 0                    | 1
    }

    void "handleStructureSearchFutures - #label"() {
        given:
        FutureTask<Long> numberHitsFutureTask = Mock(FutureTask)
        FutureTask<CompoundResult> compoundResultFutureTask = Mock(FutureTask)

        List<FutureTask<Object>> futures = [compoundResultFutureTask]
        when:
        final CompoundResult compoundResult = service.handleStructureSearchFutures(futures, numberHits)
        then:
        compoundResultFutureTask.get() >> {new CompoundResult(compounds: compounds)}
        assert compoundResult
        assert compoundResult.numberOfHits == expectedNumberOFHits
        where:
        label        | numberHits | expectedNumberOFHits | numberTimesExecuted | compounds
        "nHits == 1" | 1          | 1                    | 0                   | []
        "nHits == 1" | 1          | 1                    | 0                   | null

    }

    void "buildQueryForCompoundSummary"() {
        when:
        String query = service.buildQueryForCompoundSummary(2L)
        then:
        assert query == "http://ncgc/compounds/2/summary?expand=true"

    }

    void "Get summary for compound"() {
        given:
        Long cid = 2
        when:
        CompoundSummary compoundSummary = service.getSummaryForCompound(cid)
        then:
        restTemplate.getForEntity(_ as URI, CompoundSummary.class) >> {new ResponseEntity<CompoundSummary>(new CompoundSummary(),HttpStatus.OK)}
        assert compoundSummary
    }

    void "getTestedAssays #label"() {
        when:
        List<Assay> assays = service.getTestedAssays(cid, activeOnly)
        then:
        restTemplate.getForEntity(_ as URI, AssayResult.class) >> {new ResponseEntity<AssayResult>(new AssayResult(assays: [new Assay()]),HttpStatus.OK)}
        assert assays
        where:
        label            | cid | activeOnly
        "CID + InActive" | 22  | false
        "CID + Active"   | 22  | true

    }



    void "findAnnotations"() {
        given:
        Long cid = 200
        when:
        final CompoundAnnotations annotations = service.findAnnotations(cid)
        then:
        restTemplate.getForEntity(_ as URI, CompoundAnnotations.class) >> {new ResponseEntity<CompoundAnnotations>(new CompoundAnnotations(),HttpStatus.OK)}
        assert annotations
    }

    void "getCompoundById - Exception #label"() {

        when:
        service.getCompoundById(cid)
        then:
        restTemplate.getForObject(_, _, _) >> {compounds}
        thrown(RestApiException)
        where:
        label                   | cid | compounds | noErrors
        "Non_Existing Compound" | -1  | null      | false
    }

    void "getCompoundById #label"() {
        given:
        FutureTask<CompoundAnnotations> compoundAnnotationsTask = Mock(FutureTask)
        FutureTask<Compound> compoundTask = Mock(FutureTask)
        when:
        Compound foundCompound = service.getCompoundById(cid)
        then:
        restTemplate.getForObject(_, _, _) >> {compounds}
        compoundAnnotationsTask.get() >> {null}
        compoundTask.get() >> {compounds.get(0)}
        executorService.invokeAll(_, _, _) >> {[compoundAnnotationsTask, compoundTask]}
        assert (foundCompound != null) == noErrors
        where:
        label               | cid | compounds        | noErrors
        "Existing Compound" | 179 | [new Compound()] | true
    }

    void "searchCompoundsByIds #label"() {
        when:
        final CompoundResult compoundResult = service.searchCompoundsByIds(cids)
        then:
        0 * restTemplate.postExchange(_, _, _, _)
        assert compoundResult == null
        where:
        label                        | cids
        "With null cids"             | null
        "With an empty list of cids" | []
    }

    void "findCompoundsByFreeTextSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair")
        when:
        final CompoundResult compoundResult =
            service.findCompoundsByFreeTextSearch(searchParams)
        then:
        restTemplate.getForEntity(_ as URI, CompoundResult.class) >> {new ResponseEntity<CompoundResult>(new CompoundResult(),HttpStatus.OK)}
        assert compoundResult != null
    }

    void "findPromiscuityScoreForCompound"() {
        given:
        final Long cid = 200
        when:
        final PromiscuityScore promiscuityScore = service.findPromiscuityScoreForCompound(cid)
        then:
        restTemplate.getForEntity(_ as URI, PromiscuityScore.class) >> {new ResponseEntity<PromiscuityScore>(new PromiscuityScore(),HttpStatus.OK)}
        assert promiscuityScore != null
    }

    void "findPromiscuityForCompound"() {
        given:
        final Long cid = 200
        when:
        final Promiscuity promiscuity = service.findPromiscuityForCompound(cid)
        then:
        restTemplate.getForEntity(_ as URI, Promiscuity.class) >> {new ResponseEntity<Promiscuity>(new Promiscuity(),HttpStatus.OK)}
        assert promiscuity != null
    }

    void "getSynonymsForCompound"() {
        when:
        List<String> synonyms = service.getSynonymsForCompound(200)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>(["String"],HttpStatus.OK)}
        assert synonyms
    }

    void "getETags"() {

        when:
        final List<ETag> eTags = service.getETags(10, 10)
        then:
        restTemplate.getForEntity(_ as URI, ETagCollection.class) >> {new ResponseEntity<ETagCollection>(new ETagCollection(etags: [new ETag()]),HttpStatus.OK)}
        assert eTags
    }

    void "getFacetsByETag"() {
        when:
        List<Facet> facets = service.getFacetsByETag("etag")
        then:
        restTemplate.getForEntity(_ as URI, Facet[].class) >> {new ResponseEntity<Facet[]>([new Facet()],HttpStatus.OK)}
        assert facets
    }

    void "getResourceCount"() {
        when:
        int count = service.getResourceCount()
        then:
        restTemplate.getForEntity(_ as URI, String.class) >> {new ResponseEntity<String>("1",HttpStatus.OK)}
        assert count == 1
    }

    void "test findCompoundsByETag #label"() {
        when:
        CompoundResult returnedCompoundResult = service.findCompoundsByETag(eTagId)

        then:
        1 * restTemplate.getForEntity(_ as URI, Compound[].class) >> {new ResponseEntity<Compound[]>(compounds,HttpStatus.OK)}
        assert returnedCompoundResult.compounds*.name == expextedCompoundNames

        where:
        label                | eTagId | compounds                         | expextedCompoundNames
        'compounds found'    | '1a'   | [new Compound(name: 'compound1')] | ['compound1']
        'no compounds found' | '1a'   | []                                | []
    }

    void "test findCompoundById #label"() {
        when:
        final Compound returnedCompound = service.findCompoundById(cid)

        then:
        1 * restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>(compounds,HttpStatus.OK)}
        assert (returnedCompound == null) == expectedResults

        where:
        label             | cid | compounds                         | expectedResults
        'Return compound' | 123 | [new Compound(name: 'compound1')] | false
        'Return null'     | 345 | []                                | true
    }
}


