package jdo

import bard.core.adapter.EntityAdapter
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
     */
    void "test Get Projects #label"() {
        when: "We call the get method of the the RESTProjectService"
        final Collection<Project> projects = this.projectService.get(pids)
        then: "We expect to get back a list of 3 results"
        assertProjects(projects,false)
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
        final Collection<Project> projects = searchIterator.collect()
        assertProjects(projects,true)
        assert searchIterator.count >= 10
        assert expectedNumberOfProjects == projects.size()
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
    /**
     *
     */
    void "test Get Projects with facets, #label"() {
        given:
        final Object etag = projectService.newETag("My awesome project collection", pids);
        when: "We call the getFacets matehod"
        final Collection<Value> facets = this.projectService.getFacets(etag)
        and: "A list of assays"
        final Collection<Project> projects = this.projectService.get(pids)
        then: "We expect to get back a list of facets"
        assert facets
        assertFacetIdsAreUnique(facets)
        assertProjects(projects, false)
        where:
        label                               | pids
        "Search with a list of project ids" | [1772, 805, 1074]
    }
    /**
     *
     * @param assays
     * @param isStringSearch - Means that this is a result of a string search and not an id search
     * string searches do not have sids, but they do have highlights and annotations
     */
    void assertProjects(Collection<Project> projects, boolean isStringSearch = false) {
        for (Project project : projects) {
            assertProject(project, isStringSearch)
        }
    }

    void assertProject(final Project project, boolean isStringSearch = false) {
        assert project.id
        assert project.name
        assert project.description
        if (isStringSearch) {
            EntityAdapter<Project> projectAdapter = new EntityAdapter<Project>(project)
            final Collection<Value> annotations = projectAdapter.getAnnotations()
            assert annotations != null
            assert projectAdapter.searchHighlight
        }
    }
}