package jdo

import bard.core.rest.RESTEntityServiceManager
import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import bard.core.*

/**
 * Tests for RESTProjectService in JDO
 */

class RESTProjectServiceIntegrationSpec extends IntegrationSpec {
    EntityServiceManager esm
    ProjectService projectService
    final static String baseURL = "http://bard.nih.gov/api/v1"
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
        "Find an existing Project" | new Integer(644)
    }
    /**
     *
     */
    void "test Fail #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = this.projectService.get(pid)
        then: "An No Project is returned"
        assert !project
        where:
        label                         | pid
        "Find a non-existing Project" | new Integer(-1)
    }
    /**
     * TODO" Not yet implemented. Currently only implemented for CIDs
     * TODO: Ask NCGC that this should include a highlight option even if it is a default String
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
        "Search with a list of project ids" | [600, 644, 666]
    }
    /**
     * TODO: Ask NCGC that this search should return the same thing as the REST API
     */
    void "test REST Project Service #label #seachString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString).setSkip(skip).setTop(top);
        when: "We we call search method of the the RestProjectService"
        final ServiceIterator<Project> searchIterator = this.projectService.search(params)
        then: "We expected to get back a list of 10 results"
        int numberOfProjects = 0
        while (searchIterator.hasNext()) {
            final Project project = searchIterator.next();
            assertProject(project)
            ++numberOfProjects
        }
        assert expectedNumberOfProjects == numberOfProjects

        searchIterator.done();
        where:
        label    | searchString | skip | top | expectedNumberOfProjects
        "Search" | "dna repair" | 0    | 10  | 10

    }
}