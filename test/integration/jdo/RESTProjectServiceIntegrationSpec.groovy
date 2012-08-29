package jdo

import bard.core.rest.RESTEntityServiceManager
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import bard.core.*

/**
 * Tests for RESTProjectService in JDO
 */
@Mixin(RESTTestHelper)
class RESTProjectServiceIntegrationSpec extends IntegrationSpec implements RESTServiceInterface {
    EntityServiceManager esm
    ProjectService projectService

    @Before
    void setup() {
        this.esm = new RESTEntityServiceManager(baseURL);
        this.projectService = esm.getService(Project.class);
    }

    @After
    void tearDown() {
        this.esm.shutdown()
    }

    void assertProject(final Project project) {
        assert project.id
        assert project.name
        assert project.description
    }
    /**
     *
     */
    void "test Get a Single Project #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = this.projectService.get(pid)
        then: "An Project is returned with the expected information"
        assert project
        assert pid == project.id
        assertProject(project)
        where:
        label                      | pid
        "Find an existing Project" | new Integer(1772)
    }
    /**
     *
     */
    void "test Fail project Id does not exist #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = this.projectService.get(pid)
        then: "An No Project is returned"
        assert !project
        where:
        label                         | pid
        "Find a non-existing Project" | new Integer(-1)
    }
    /**
     * TODO: Ask Steve, do we need facet information, paging information etc?
     */
    void "test Get Projects #label"() {
        when: "We call the get method of the the RESTProjectService"
        final Collection<Project> projects = this.projectService.get(pids)
        then: "We expect to get back a list of 3 results"
        for (Project project : projects) {
            assertProject(project)
        }
        assert pids.size() == projects.size()
        where:
        label                               | pids
        "Search with a list of project ids" | [1772, 805, 1074]
        "Search with a single project id"   | [1772]
    }
    /**
     */
    void "test REST Project Service #label #seachString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RestProjectService"
        final ServiceIterator<Project> searchIterator = this.projectService.search(params)
        then: "We expected to get back a list of 10 results"
        int numberOfProjects = 0
        while (searchIterator.hasNext()) {
            final Project project = searchIterator.next();
            assertProject(project)
            ++numberOfProjects
        }
        assert searchIterator.count >= 10
        assert expectedNumberOfProjects == numberOfProjects
        assertFacets(searchIterator)
        searchIterator.done();
        where:
        label    | searchString | skip | top | expectedNumberOfProjects
        "Search" | "dna repair" | 0    | 10  | 10

    }

    /**
     *
     */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(1);
        when: "We we call search method of the the RESTCompoundService"
        final ServiceIterator<Project> searchIterator = this.projectService.search(params)
        then: "We expected to get back unique facets"
        assertFacetIdsAreUnique(searchIterator)
        searchIterator.done();
    }
}