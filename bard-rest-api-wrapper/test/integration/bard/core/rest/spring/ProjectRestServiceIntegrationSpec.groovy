package bard.core.rest.spring


import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.project.ExpandedProjectResult
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.util.Facet
import grails.plugin.spock.IntegrationSpec
import spock.lang.Unroll

/**
 * Tests for ProjectRestService
 */
@Mixin(RESTTestHelper)
@Unroll
class ProjectRestServiceIntegrationSpec extends IntegrationSpec {
    ProjectRestService projectRestService

    void testProjectSuggestions() {
        given:
        //construct Search Params
        final SuggestParams suggestParams = constructSuggestParams();
        when:
        final Map<String, List<String>> ps = projectRestService.suggest(suggestParams);
        then:
        assertSuggestions(ps);
    }
    /**
     * Copied from RESTTestServices#testServices9
     */
    void testFiltersWithProjectService() {
        given:
        final List<String[]> filters = new ArrayList<String[]>();
        filters.add(["num_expt", "6"] as String[])

        //construct Search Params
        final SearchParams searchParams = new SearchParams("dna repair", filters);
        when:
        ExpandedProjectResult projectSearchResult = this.projectRestService.findProjectsByFreeTextSearch(searchParams);
        then:
        assert projectSearchResult, "ProjectService SearchResults must not be null"

        List<Project> projects = projectSearchResult.projects
        assert !projects.isEmpty(), "ProjectService SearchResults must not be empty"
        for (Project project : projects) {
            assert project.name, "ProjectSearchResult Adapter must have a name"
        }
        final List<Facet> facets = projectSearchResult.getFacets();
        assert facets, "List of Facets is not null"
        assert !facets.isEmpty(), "List of Facets is not empty"

    }

    void "test project with probe #label"() {
        when: "The get method is called with the given PID: #pid"
        final Project project = projectRestService.getProjectById(pid)
        then: "A Project is returned with the expected information"
        assert project
        assert pid == project.projectId
        assert project.getProbes();
        final Compound compound = project.getProbes().get(0)
        assert compound.cid == 9795907
        assert compound.probeId == "ML103"
        assert compound.url == "https://mli.nih.gov/mli/?dl_id=976"
        assertProject(project, false)
        where:
        label                                               | pid
        "Find an existing ProjectSearchResult with a Probe" | new Integer(17)
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
        final ExpandedProjectResult projectSearchResult = this.projectRestService.findProjectsByFreeTextSearch(searchParams)
        then: "We expect the following"
        assert projectSearchResult.numberOfHits > 0
        assertProject(projectSearchResult.projects.get(0), true)
    }
    /**
     *
     */
    void "test Get a Single Project #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = projectRestService.getProjectById(pid)
        then: "An ProjectSearchResult is returned with the expected information"
        assert project
        assert pid == project.projectId
        assertProject(project, false)
        where:
        label                                  | pid
        "Find an existing ProjectSearchResult" | new Integer(129)
    }
    /**
     *
     */
    void "test Fail project Id does not exist #label"() {

        when: "The get method is called with the given PID: #pid"
        final Project project = projectRestService.getProjectById(pid)
        then: "No Project is returned"
        assert project == null
        where:
        label                                     | pid
        "Find a non-existing ProjectSearchResult" | new Integer(-1)
    }
    /**
     * Get the list of projects
     */
    void "test REST Project Service #label #searchString question"() {
        given: "A search string, #searchString, and asking to retrieve the first #top search results"
        final SearchParams params = new SearchParams(searchString)
        params.setSkip(skip)
        params.setTop(top);
        when: "We we call search method of the the RestProjectService"
        final ExpandedProjectResult projectSearchResult = projectRestService.findProjectsByFreeTextSearch(params)
        then: "We expected to get back a list of 10 results"
        final List<Project> projects = projectSearchResult.projects
        assert projects != null
        assert !projects.isEmpty()
        assertProjects(projects, true)
        assertFacets(projectSearchResult.facets)
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
        final ExpandedProjectResult projectSearchResult = projectRestService.findProjectsByFreeTextSearch(params)
        and: "then we get the first project "
        assert projectSearchResult.projects
        final List<Project> projects = projectSearchResult.projects
        final Project projectAfterFreeTextSearch = projects.iterator().next()

        and: "call the restProjectService.get() on it "
        Project projectAfterGet = projectRestService.getProjectById(projectAfterFreeTextSearch.getId())
        then: "We expected to get the same project back and we expect that it is not null"

        assert projectAfterFreeTextSearch != null
        assert projectAfterGet != null

        assert projectAfterFreeTextSearch.id == projectAfterGet.id

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
        final ExpandedProjectResult searchResults1 = projectRestService.findProjectsByFreeTextSearch(params1)
        and: "params2"
        final ExpandedProjectResult searchResults2 = projectRestService.findProjectsByFreeTextSearch(params2)


        then: "We expect that the list of ids returned for each search, would be different"
        Collection<Long> projectIds2 = searchResults2.projects.collect { Project project -> project.id as Long }
        Collection<Long> projectIds1 = searchResults1.projects.collect { Project project -> project.id as Long }
        assert projectIds1 != projectIds2
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
        final ExpandedProjectResult projectSearchResult = projectRestService.findProjectsByFreeTextSearch(params)
        then: "We expected to get back unique facets"
        assertFacetIdsAreUnique(projectSearchResult.facets)
    }

    /**
     *
     */
    void "test Get Projects with facets, #label"() {
        given:
        final Object etag = projectRestService.newETag("Some Collection", pids);
        when: "We call the getFacets matehod"
        final List<Facet> facets = projectRestService.getFacetsByETag(etag.toString())
        and: "A list of projects"
        final ExpandedProjectResult projectSearchResult = projectRestService.searchProjectsByIds(pids)
        then: "We expect to get back a list of facets"
        assert !facets.isEmpty()
        assertFacetIdsAreUnique(facets)
        assertProjects(projectSearchResult.projects, false)
        where:
        label                               | pids
        "Search with a list of project ids" | [129, 102, 100]
    }
    /**
     *
     */
    void "test Get Projects #label"() {
        when: "We call the get method of the the ProjectRestService"
        final ExpandedProjectResult projectSearchResult = projectRestService.searchProjectsByIds(pids)
        then: "We expect to get back a list of 3 results"
        assertProjects(projectSearchResult.projects, false)
        assert projectSearchResult.numberOfHits == pids.size()
        where:
        label                               | pids
        "Search with a list of project ids" | [129, 102, 100]
        "Search with a single project id"   | [129]
    }

    void "test count"() {
        when:
        long size = projectRestService.getResourceCount()
        then:
        assert size > 0
    }


}