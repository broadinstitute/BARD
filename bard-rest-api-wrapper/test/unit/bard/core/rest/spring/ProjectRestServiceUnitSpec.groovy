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
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.project.ProjectStep
import bard.core.util.ExternalUrlDTO
import grails.test.mixin.TestFor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(ProjectRestService)
class ProjectRestServiceUnitSpec extends Specification {
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

    void "searchProjectsByCapIds #label"() {
        when:
        List<Project> projectResult = service.searchProjectsByCapIds(searchParams, etags)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>([new Project()],HttpStatus.OK)}
        assert projectResult.isEmpty() == expected
        where:
        label              | searchParams                       | etags          | expected
        "With ETags"       | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | false
        "With Empty ETags" | new SearchParams(skip: 0, top: 10) | [:]            | true

    }

    void "find project Steps"() {
        when:
        List<ProjectStep> projectSteps = service.findProjectSteps(123)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>([new ProjectStep()],HttpStatus.OK)}
        assert projectSteps
    }

    void "searchProjectsByCapIds(searchParams, etags) #label"() {
        when:
        List<Project> projectResult = service.searchProjectsByCapIds(searchParams, etags)
        then:
        restTemplate.getForEntity(_ as URI, List.class) >> {new ResponseEntity<List>([new Project()],HttpStatus.OK)}
        assert (projectResult != null) == expected
        where:
        label        | searchParams                       | etags          | expected
        "With ETags" | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | true
    }

    void "searchProjectsByCapIds(final List<Long> capIds, final SearchParams searchParams) #label"() {
        when:
        ProjectResult projectResult = service.searchProjectsByCapIds(capIds, searchParams)
        then:
        assert (projectResult != null) == expected
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
        "With ETag"      | new SearchParams(skip: 0, top: 10) | "123" | "http://ncgc/projects/etag/123?skip=0&top=10&expand=true"
        "With Null ETag" | new SearchParams(skip: 0, top: 10) | null  | ""

    }

    void "buildSearchByCapIdURLs #label"() {

        when:
        String resourceURL = service.buildSearchByCapIdURLs(capIds, searchParams, "capProjectId:")
        then:
        assert resourceURL == expectedURL
        where:
        label           | searchParams                       | capIds | expectedURL
        "Two CAP Ids"   | new SearchParams(skip: 0, top: 10) | [1, 2] | "http://ncgc/search/projects/?q=capProjectId%3A1+or+capProjectId%3A2&skip=0&top=10&expand=true"
        "Single CAP ID" | new SearchParams(skip: 0, top: 10) | [1]    | "http://ncgc/search/projects/?q=capProjectId%3A1&skip=0&top=10&expand=true"
        "No CAP ID"     | new SearchParams(skip: 0, top: 10) | []     | "http://ncgc/search/projects/?q=&skip=0&top=10&expand=true"

    }

    void "findAnnotations"() {
        given:
        Long pid = 222L
        when:
        BardAnnotation foundBardAnnotation = service.findAnnotations(pid)
        then:
        restTemplate.getForEntity(_ as URI, BardAnnotation.class) >> {new ResponseEntity<BardAnnotation>(new BardAnnotation(),HttpStatus.OK)}

        assert foundBardAnnotation


    }

    void "getProjectById #label"() {
        when:
        final ProjectExpanded resultProject = service.getProjectById(pid)

        then:
        restTemplate.getForEntity(_ as URI, ProjectExpanded.class) >> {new ResponseEntity<ProjectExpanded>(project,HttpStatus.OK)}
        assert (resultProject != null) == noErrors
        where:
        label                  | pid | project               | noErrors
        "Existing Project"     | 179 | new ProjectExpanded() | true
        "Non_Existing Project" | -1  | null                  | false
    }

    void "getResourceContext"() {

        when:
        final String resourceContext = service.getResourceContext()
        then:
        assert resourceContext == RestApiConstants.PROJECTS_RESOURCE
    }

    void "searchProjectsByIds #label"() {
        when:
        final ProjectResult projectResult = service.searchProjectsByIds(pids)
        then:
        0 * restTemplate.postExchange(_, _, _, _)
        assert projectResult == null
        where:
        label                        | pids
        "With null pids"             | null
        "With an empty list of pids" | []
    }

    void "findProjectsByFreeTextSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair")
        when:
        final ProjectResult projectResult =
            service.findProjectsByFreeTextSearch(searchParams)
        then:
        restTemplate.getForEntity(_ as URI, ProjectResult.class) >> {new ResponseEntity<ProjectResult>(new ProjectResult(),HttpStatus.OK)}
        assert projectResult != null
    }

    void "getSearchResource"() {
        when:
        String searchResource = service.getSearchResource()
        then:
        assert searchResource == "http://ncgc/search/projects/?"
    }

    void "getResource"() {
        when:
        String searchResource = service.getResource()
        then:
        assert searchResource == "http://ncgc/projects/"
    }

    void "buildEntityURL"() {
        when:
        String url = service.buildEntityURL()
        then:
        assert url == "http://ncgc/projects/{id}/"
    }

    void "buildURLToCreateETag Project Entity"() {
        when:
        String url = service.buildURLToCreateETag()
        then:
        assert url == "http://ncgc/projects/etag"
    }

    void "buildURLToPutETag"() {
        when:
        String url = service.buildURLToPutETag()
        then:
        assert url == "http://ncgc/projects/etag/"

    }

    void "buildURLToPostIds"() {
        when:
        String url = service.buildURLToPostIds()
        then:
        assert url == "http://ncgc/projects/?expand=true"
    }

    void "getResource(String)"() {
        when:
        String url = service.getResource("resource")
        then:
        assert url == "http://ncgc/projects/resource"
    }

    void "findNextTopValue #label"() {
        when:
        int nextValue = service.findNextTopValue(skip, ratio)
        then:
        assert nextValue == expectedNextValue
        where:
        label                   | skip | ratio | expectedNextValue
        "skip=10 and ratio=10"  | 10   | 10    | 10
        "Edge case skip > 1000" | 1001 | 10    | 1000
        "Edge case skip=1000"   | 1000 | 10    | 10

    }
}

