package bard.core.rest

import bard.core.adapter.ProjectAdapter
import bard.core.interfaces.SearchResult
import bard.core.rest.helper.RESTTestHelper
import spock.lang.Unroll
import bard.core.*

/**
 * Tests for RESTProjectService in JDO
 */
@Mixin(RESTTestHelper)
@Unroll
class RESTProjectServiceIntegrationSpec extends AbstractRESTServiceSpec {
    void "test size"() {
        when:
        long size = restProjectService.size()
        then:
        assert size > 0
    }

    void "test project with probe #label"() {
        when: "The get method is called with the given PID: #pid"
        final Project project = restProjectService.get(pid)
        then: "A Project is returned with the expected information"
        assert project
        assert pid == project.id
        final ProjectAdapter projectAdapter = new ProjectAdapter(project)
        assert projectAdapter.getProbes();
        assert !projectAdapter.getProbes().isEmpty()
        Probe probe = projectAdapter.getProbes().get(0)
        assert probe.cid == "9795907"
        assert probe.probeId == "ML103"
        assert probe.url == "https://mli.nih.gov/mli/?dl_id=976"
        where:
        label                                   | pid
        "Find an existing Project with a Probe" | new Integer(17)
    }
    /**
     *
     */
    void "test projects with assays #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = restProjectService.get(pid)
        then: "A Project is returned with the expected information"
        assert project
        assert pid == project.id
        final ProjectAdapter projectAdapter = new ProjectAdapter(project)
        assert projectAdapter.numberOfExperiments

        final SearchResult<Assay> searchResults = restProjectService.searchResult(project, Assay.class)
        final List<Assay> assays = searchResults.searchResults
        assert assays
        assert !assays.isEmpty()
        where:
        label                      | pid
        "Find an existing Project" | new Integer(179)
    }
    /**
     *
     */
    void "test projects with experiments #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = restProjectService.get(pid)
        then: "A Project is returned with the expected information"
        assert project
        assert pid == project.id
        final ProjectAdapter projectAdapter = new ProjectAdapter(project)
        assert projectAdapter.numberOfExperiments

        final SearchResult<Experiment> searchResults = restProjectService.searchResult(project, Experiment.class)
        final List<Experiment> experiments = searchResults.searchResults
        assert experiments
        for (Experiment experiment : experiments) {
            assert experiment
        }
        where:
        label                      | pid
        "Find an existing Project" | new Integer(179)
    }

    void "use auto-suggest 'zinc ion binding as GO molecular function term' has problems bug: https://www.pivotaltracker.com/story/show/36709121"() {
        given:
        final SearchParams searchParams = new SearchParams("zinc ion binding")
        searchParams.setSkip(0)
        searchParams.setTop(10);
        List<String[]> filters = []
        filters.add(["gomf_term", "zinc ion binding"] as String[])
        searchParams.filters = filters
        when: "We execute the search"
        final SearchResult<Project> searchResults = this.restProjectService.search(searchParams)
        then: "We expect the following"
        assert searchResults.count > 0
    }
    /**
     *
     */
    void "test Get a Single Project #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = restProjectService.get(pid)
        then: "An Project is returned with the expected information"
        assert project
        assert pid == project.id
        assertProjectAdapter(new ProjectAdapter(project))
        where:
        label                      | pid
        "Find an existing Project" | new Integer(129)
    }
    /**
     *
     */
    void "test Fail project Id does not exist #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = restProjectService.get(pid)
        then: "No Project is returned"
        assert project == null
        where:
        label                         | pid
        "Find a non-existing Project" | new Integer(-1)
    }
    /**
     */
    void "test Get Projects #label"() {
        when: "We call the get method of the the RESTProjectService"
        final Collection<Project> projects = restProjectService.get(pids)
        then: "We expect to get back a list of 3 results"
        assertProjects(projects, false)
        assert pids.size() == projects.size()
        where:
        label                               | pids
        "Search with a list of project ids" | [129, 102, 100]
        "Search with a single project id"   | [129]
    }
    /**
     * Get the list of projects
     */
    void "test REST Project Service #label #seachString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RestProjectService"
        final SearchResult<Project> searchResults = restProjectService.search(params)
        then: "We expected to get back a list of 10 results"
        final Collection<Project> projects = searchResults.searchResults
        assert projects != null
        assert !projects.isEmpty()
        assertProjects(projects, true)
        assertFacets(searchResults)
        where:
        label    | searchString | skip | top | expectedNumberOfProjects
        "Search" | "dna repair" | 0    | 10  | 10

    }
    /**
     * Get the a list of projects from a free text search using 'dna repair"
     * then confirm that you can get the details of the first project returned
     */
    void "test Project Service , Get Project with Id after free text search"() {
        given: "A search string, 'dna repair', and asking to retrieve the first 10 search results"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(10);
        when: "We call search method of the the RestProjectService"
        final SearchResult<Project> searchResults = restProjectService.search(params)
        and: "then we get the first project "
        assert searchResults.searchResults
        final Collection<Project> projects = searchResults.searchResults
        final Project projectAfterFreeTextSearch = projects.iterator().next()

        and: "call the restProjectService.get() on it "
        Project projectAfterGet = restProjectService.get(projectAfterFreeTextSearch.getId())
        then: "We expected to get the same project back and we expect that it is not null"

        assert projectAfterFreeTextSearch != null
        assert projectAfterGet != null

        assert projectAfterFreeTextSearch.id == projectAfterGet.id

    }
    /**
     *
     */
    void "test Facet keys (ids) are unique"() {
        given: "That we have created a valid search params object"
        final SearchParams params = new SearchParams("dna repair")
        params.setSkip(0)
        params.setTop(1);
        when: "We we call search method of the the RESTProjectService"
        final SearchResult<Project> searchResults = restProjectService.search(params)
        then: "We expected to get back unique facets"
        assertFacetIdsAreUnique(searchResults)
    }
    /**
     *
     */
    void "test REST Project Service #label #seachString with paging"() {
        given: "A search string of 'dna repair' and two Search Params, Params 1 has skip=0 and top=10, params2 has skip=10 and top=10"
        final String searchString = "dna repair"
        SearchParams params1 = new SearchParams(searchString)
        params1.setSkip(0)
        params1.setTop(10);

        SearchParams params2 = new SearchParams(searchString)
        params2.setSkip(10)
        params2.setTop(10);

        when: "We call the search method of RestProjectService with params1"
        final SearchResult<Project> searchResults1 = restProjectService.search(params1)
        and: "params2"
        final SearchResult<Project> searchResults2 = restProjectService.search(params2)


        then: "We expect that the list of ids returned for each search, would be different"
        Collection<Long> projectIds2 = searchResults2.searchResults.collect { Project project -> project.id as Long }
        Collection<Long> projectIds1 = searchResults1.searchResults.collect { Project project -> project.id as Long }
        assert projectIds1 != projectIds2
    }
    /**
     *
     */
    void "test Get Projects with facets, #label"() {
        given:
        final Object etag = restProjectService.newETag("My awesome project collection", pids);
        when: "We call the getFacets matehod"
        final Collection<Value> facets = restProjectService.getFacets(etag)
        and: "A list of assays"
        final Collection<Project> projects = restProjectService.get(pids)
        then: "We expect to get back a list of facets"
        assert !facets.isEmpty()
        assertFacetIdsAreUnique(facets)
        assertProjects(projects, false)
        where:
        label                               | pids
        "Search with a list of project ids" | [129, 102, 100]
    }
    /**
     *
     * @param assays
     * @param isStringSearch - Means that this is a result of a string search and not an id search
     * string searches do not have sids, but they do have highlights and annotations
     */
    void assertProjects(Collection<Project> projects, boolean isStringSearch = false) {
        for (Project project : projects) {
            assertProjectAdapter(new ProjectAdapter(project), isStringSearch)
        }
    }
    /**
     *
     * @param assayAdapter
     */
    void assertProjectAdapter(final ProjectAdapter projectAdapter, boolean isStringSearch = false) {
        assert projectAdapter.project != null
        assert projectAdapter.id != null
        assert projectAdapter.name != null
        assert projectAdapter.description != null
        if (isStringSearch) {
            final Collection<Value> annotations = projectAdapter.getAnnotations()
            assert annotations != null
            assert projectAdapter.searchHighlight
        }
    }
}