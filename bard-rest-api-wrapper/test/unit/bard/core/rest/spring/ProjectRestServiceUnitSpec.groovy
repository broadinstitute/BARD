package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.helper.LoggerService
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.project.ProjectStep
import grails.test.mixin.TestFor
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(ProjectRestService)
class ProjectRestServiceUnitSpec extends Specification {
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

    void "searchProjectsByCapIds #label"() {
        when:
        List<Project> projectResult = service.searchProjectsByCapIds(searchParams, etags)
        then:
        restTemplate.getForObject(_, _) >> {[new Project()]}
        assert (projectResult != null) == expected
        where:
        label        | searchParams                       | etags          | capIds | expected
        "With ETags" | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | []     | true

    }

    void "find project Steps"() {
        when:
        List<ProjectStep> projectSteps = service.findProjectSteps(123)
        then:
        restTemplate.getForObject(_, _) >> {[new ProjectStep()]}
        assert projectSteps
    }

    void "searchProjectsByCapIds(searchParams, etags) #label"() {
        when:
        List<Project> projectResult  = service.searchProjectsByCapIds(searchParams, etags)
        then:
        restTemplate.getForObject(_, _) >> {[new Project()]}
        assert (projectResult != null) == expected
        where:
        label           | searchParams                       | etags          | expected
        "With ETags"    | new SearchParams(skip: 0, top: 10) | ["e1233": 123] | true
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
        restTemplate.getForObject(_, _) >> {new BardAnnotation()}
        assert foundBardAnnotation


    }

    void "getProjectById #label"() {
        when:
        final ProjectExpanded resultProject = service.getProjectById(pid)

        then:
        restTemplate.getForObject(_, _, _) >> {project}
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
        restTemplate.getForObject(_, _) >> {new ProjectResult()}
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

