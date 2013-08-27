package bard.core.rest.spring
import bard.core.SearchParams
import bard.core.SuggestParams
import bard.core.rest.helper.RESTTestHelper
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectExpanded
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.Facet
import grails.plugin.spock.IntegrationSpec
import org.springframework.web.client.HttpClientErrorException
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Unroll
/**
 * Tests for ProjectRestService
 */
@Mixin(RESTTestHelper)
@Unroll
class ProjectRestServiceIntegrationSpec extends IntegrationSpec {
    ProjectRestService projectRestService
    @Shared
    List<Long> PIDS = [2]
    @Shared
    List<Long> CAP_PIDS = [3]

    void "searchProjectsByCapIds #label"() {
        when:
        ProjectResult projectResult = projectRestService.searchProjectsByCapIds(capIds, searchParams)
        then:
        assert (projectResult != null) == expected
        where:
        label                         | searchParams                       | etags | capIds   | expected
        "With capIds no ETags"        | new SearchParams(skip: 0, top: 10) | [:]   | CAP_PIDS | true
        "With no capIds and no ETags" | new SearchParams(skip: 0, top: 10) | [:]   | []       | false

    }



    void "searchProjectsByCapIds withETags #label"() {
        given: "That we have made a request with some CAP Ids that returns an etag"
        ProjectResult projectResultWithIds = projectRestService.searchProjectsByCapIds(capIds, searchParams)
        when: "We use the returned etags to make another request"
        List<Project> projectResultWithETags = projectRestService.searchProjectsByCapIds(searchParams, projectResultWithIds.etags)
        then: "We get back the expected results"
        assert (projectResultWithETags != null) == expected
        where:
        label        | searchParams                       | capIds   | expected
        "With ETags" | new SearchParams(skip: 0, top: 10) | CAP_PIDS | true
    }

    void testProjectSuggestions() {
        given:
        //construct Search Params
        final SuggestParams suggestParams = constructSuggestParams();
        when:
        final Map<String, List<String>> ps = projectRestService.suggest(suggestParams);
        then:
        assertSuggestions(ps);
    }

    //For v15, the only project we have (PID=2) doesn't have documents.
//    void testProjectsWithPublications() {
//        given:
//        Long pid = PIDS.get(0)
//
//        when:
//        final ProjectExpanded projectExpanded = projectRestService.getProjectById(pid)
//
//        then:
//        final List<Document> publications = projectExpanded.getPublications()
//        assert publications
//        assert publications.size() >= 1
//        Document publication = publications.get(0)
//        assert publication
//
//    }
    /**
     * Copied from RESTTestServices#testServices9
     */
    void testFiltersWithProjectService() {
        given:
        final List<String[]> filters = new ArrayList<String[]>();
//        filters.add(["assay_component_role", "target cell"] as String[])

        //construct Search Params
        final SearchParams searchParams = new SearchParams("Scavenger", filters);
        when:
        ProjectResult projectSearchResult = this.projectRestService.findProjectsByFreeTextSearch(searchParams);
        then:
        assert projectSearchResult, "ProjectSearchResults must not be null"

        List<Project> projects = projectSearchResult.projects
        assert !projects.isEmpty(), "ProjectService SearchResults must not be empty"
        for (Project project : projects) {
            assert project.name, "ProjectSearchResult Adapter must have a name"
        }
        final List<Facet> facets = projectSearchResult.getFacets();
        assert facets, "List of Facets is not null"
        assert !facets.isEmpty(), "List of Facets is not empty"

    }
    //TODO: Commenting out till probes are associated to Projects in warehouse
//    void "test project with probe #label"() {
//        when: "The get method is called with the given PID: #pid"
//        final ProjectExpanded project = projectRestService.getProjectById(pid)
//        then: "A Project is returned with the expected information"
//        assert project
//        assert pid == project.projectId
//        assert project.getProbes();
//        final Compound compound = project.getProbes().get(0)
//        assert compound.cid == 9795907
//        assert compound.probeId == "ML103"
//        assert compound.url == "https://mli.nih.gov/mli/?dl_id=976"
//        assert project.id != null
//        assert project.name
//        assert project.description
//        assert project.experiments
//        assert project.assays
//        assert project.experimentCount
//        where:
//        label                                               | pid
//        "Find an existing ProjectSearchResult with a Probe" | PROJECT_WITH_PROBE
//    }

    @Ignore
    void "use auto-suggest 'zinc ion binding as GO molecular function term' has problems bug: https://www.pivotaltracker.com/story/show/36709121"() {
        given:
        final SearchParams searchParams = new SearchParams("\"zinc ion binding\"")
        searchParams.setSkip(0)
        searchParams.setTop(10);
        List<String[]> filters = []
        filters.add(["gomf_term", "zinc ion binding"] as String[])
        searchParams.filters = filters
        when: "We execute the search"
        final ProjectResult projectSearchResult = this.projectRestService.findProjectsByFreeTextSearch(searchParams)
        then: "We expect the following"
        assert projectSearchResult.numberOfHits > 0
        assertProject(projectSearchResult.projects.get(0), true)
    }
    /**
     *
     */
    void "test Get a Single Project #label"() {

        when: "The get method is called with the given PID: #pid"
        final ProjectExpanded project = projectRestService.getProjectById(pid)
        then: "An ProjectSearchResult is returned with the expected information"
        assert project
        assert pid == project.bardProjectId
        assert project.id != null
        assert project.name
        assert project.description
        assert project.experiments
        assert project.assays
        assert project.experimentCount
        where:
        label                                  | pid
        "Find an existing ProjectSearchResult" | new Integer(2)
    }
    /**
     *
     */
    void "test Fail project Id does not exist #label"() {

        when: "The get method is called with the given PID: #pid"
        projectRestService.getProjectById(pid)
        then: "No Project is returned"
        thrown(HttpClientErrorException)
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
        final ProjectResult projectSearchResult = projectRestService.findProjectsByFreeTextSearch(params)
        then: "We expected to get back a list of 10 results"
        final List<Project> projects = projectSearchResult.projects
        assert projects != null
        assert !projects.isEmpty()
        assertProjects(projects, true)
        assertFacets(projectSearchResult.facets)
        where:
        label    | searchString | skip | top | expectedNumberOfProjects
        "Search" | "Scavenger"  | 0    | 10  | 10

    }
    /**
     * Get the a list of projects from a free text search using 'dna repair"
     * then confirm that you can get the details of the first project returned
     */
    void "test Project Service , Get Project with Id after free text search"() {
        given: "A search string, 'dna repair', and asking to retrieve the first 10 search results"
        final SearchParams params = new SearchParams("Scavenger")
        params.setSkip(0)
        params.setTop(10);
        when: "We call search method of the the RestProjectService"
        final ProjectResult projectSearchResult = projectRestService.findProjectsByFreeTextSearch(params)
        and: "then we get the first project "
        assert projectSearchResult.projects
        final List<Project> projects = projectSearchResult.projects
        final Project projectAfterFreeTextSearch = projects.iterator().next()

        and: "call the restProjectService.get() on it "
        ProjectExpanded projectAfterGet = projectRestService.getProjectById(projectAfterFreeTextSearch.getId())
        then: "We expected to get the same project back and we expect that it is not null"

        assert projectAfterFreeTextSearch != null
        assert projectAfterGet != null

        assert projectAfterFreeTextSearch.id == projectAfterGet.id

    }
    /**
     *
     */
    //we currently only have one project
    @Ignore
    void "test REST Project Service #label #seachString with paging"() {
        given: "A search string of 'Scavenger' and two Search Params, Params 1 has skip=0 and top=10, params2 has skip=10 and top=10"
        final String searchString = "dna repair"
        SearchParams params1 = new SearchParams(searchString)
        params1.setSkip(0)
        params1.setTop(10);

        SearchParams params2 = new SearchParams(searchString)
        params2.setSkip(10)
        params2.setTop(10);

        when: "We call the search method of RestProjectService with params1"
        final ProjectResult searchResults1 = projectRestService.findProjectsByFreeTextSearch(params1)
        and: "params2"
        final ProjectResult searchResults2 = projectRestService.findProjectsByFreeTextSearch(params2)


        then: "We expect that the list of ids returned for each search, would be different"
        Collection<Long> projectIds2 = searchResults2.projects.collect { Project project -> project.id as Long }
        Collection<Long> projectIds1 = searchResults1.projects.collect { Project project -> project.id as Long }
        assert projectIds1 != projectIds2
    }
    /**
     *
     */
    void "test REST Project Service test filters with number ranges"() {
        given:
        String uriWithFilters = projectRestService.getSearchResource() + "q=%22Scavenger%22&filter=fq(num_expt:%5B10+TO+*%5D),&skip=0&top=10&expand=true"
        URI uri = new URI(uriWithFilters)
        when:
        ProjectResult projectResult = (ProjectResult) this.projectRestService.getForObject(uri, ProjectResult)
        then:
        assert projectResult
        final List<Project> projects = projectResult.projects
        assert projects, "Projects must not be null"
        assert !projects.isEmpty(), "ProjectRestService SearchResults must not be empty"
        assert projectResult.numberOfHits > 0, "CompoundService SearchResults must have at least one element"
        final List<Facet> facets = projectResult.getFacets();
        assert facets != null, "List of Facets is not null"
        assert !facets.isEmpty(), "List of Facets is not empty"
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
        final ProjectResult projectSearchResult = projectRestService.findProjectsByFreeTextSearch(params)
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
        final ProjectResult projectSearchResult = projectRestService.searchProjectsByIds(pids)
        then: "We expect to get back a list of facets"
        assert !facets.isEmpty()
        assertFacetIdsAreUnique(facets)
        assertProjects(projectSearchResult.projects, false)
        where:
        label                               | pids
        "Search with a list of project ids" | [907, 120, 121]
    }
    /**
     *
     */
    void "test Get Projects #label"() {
        when: "We call the get method of the the ProjectRestService"
        final ProjectResult projectSearchResult = projectRestService.searchProjectsByIds(pids)
        then: "We expect to get back a list of 3 results"
        assertProjects(projectSearchResult.projects, false)
        assert projectSearchResult.numberOfHits == pids.size()
        where:
        label                               | pids
        "Search with a list of project ids" | PIDS
        "Search with a single project id"   | [PIDS.get(0)]
    }

    void "test count"() {
        when:
        long size = projectRestService.getResourceCount()
        then:
        assert size > 0
    }


}