package bard.core.rest.spring

import bard.core.SearchParams
import bard.core.interfaces.RestApiConstants
import bard.core.rest.spring.project.ExpandedProjectResult
import bard.core.rest.spring.project.Project
import grails.test.mixin.TestFor
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(ProjectRestService)
class ProjectRestServiceUnitSpec extends Specification {
    RestTemplate restTemplate

    void setup() {
        this.restTemplate = Mock(RestTemplate)
        service.restTemplate = this.restTemplate
        service.promiscuityUrl = "badapple"
        service.baseUrl = "http://ncgc"
    }

    void tearDown() {
        // Tear down logic here
    }

    void "getProjectById #label"() {
        when:
        final Project resultProject = service.getProjectById(pid)

        then:
        restTemplate.getForObject(_, _, _) >> {project}
        assert (resultProject != null) == noErrors
        where:
        label                  | pid | project       | noErrors
        "Existing Project"     | 179 | new Project() | true
        "Non_Existing Project" | -1  | null          | false
    }

    void "getResourceContext"() {

        when:
        final String resourceContext = service.getResourceContext()
        then:
        assert resourceContext == RestApiConstants.PROJECTS_RESOURCE
    }

    void "searchProjectsByIds #label"() {
        when:
        final ExpandedProjectResult expandedProjectResult = service.searchProjectsByIds(pids)
        then:
        0 * restTemplate.exchange(_, _, _, _)
        assert expandedProjectResult == null
        where:
        label                        | pids
        "With null pids"             | null
        "With an empty list of pids" | []
    }

    void "findProjectsByFreeTextSearch"() {
        given:
        final SearchParams searchParams = new SearchParams("dna repair")
        when:
        final ExpandedProjectResult expandedProjectResult =
            service.findProjectsByFreeTextSearch(searchParams)
        then:
        restTemplate.getForObject(_, _) >> {new ExpandedProjectResult()}
        assert expandedProjectResult != null
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

