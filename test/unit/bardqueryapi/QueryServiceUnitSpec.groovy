package bardqueryapi

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import grails.test.mixin.TestFor
import org.apache.commons.lang.time.StopWatch
import bard.core.*
import bard.core.interfaces.SearchResult

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryService)
class QueryServiceUnitSpec extends Specification {
    RESTCompoundService restCompoundService
    RESTProjectService restProjectService
    RESTAssayService restAssayService
    RESTExperimentService restExperimentService
    QueryHelperService queryHelperService
    QueryServiceWrapper queryServiceWrapper

    @Shared Assay assay1 = new Assay(name: "A1")
    @Shared Assay assay2 = new Assay(name: "A2")
    @Shared Compound compound1 = new Compound(name: "C1")
    @Shared Compound compound2 = new Compound(name: "C2")
    @Shared Project project1 = new Project(name: "P1")
    @Shared Project project2 = new Project(name: "P2")
    @Shared Map compoundAdapterMap1 = [compoundAdapters: [], facets: null, nHits: 0]
    @Shared Map compoundAdapterMap2 = [compoundAdapters: [], facets: [], nHits: 0]
    @Shared Map assayAdapterMap1 = [assayAdapters: [new AssayAdapter(assay1)], facets: [], nHits: 0]
    @Shared Map assayAdapterMap2 = [assayAdapters: [], facets: null, nHits: 0]
    @Shared Map projectAdapterMap1 = [projectAdapters: [new ProjectAdapter(project1)], facets: null, nHits: 0]
    @Shared Map projectAdapterMap2 = [projectAdapters: [], facets: [], nHits: 0]

    void setup() {
        restCompoundService = Mock(RESTCompoundService)
        restProjectService = Mock(RESTProjectService)
        restAssayService = Mock(RESTAssayService)
        restExperimentService = Mock(RESTExperimentService)
        queryServiceWrapper = Mock(QueryServiceWrapper)
        queryHelperService = Mock(QueryHelperService)
        service.queryHelperService = queryHelperService
        service.queryServiceWrapper = queryServiceWrapper

    }

    void tearDown() {
        // Tear down logic here
    }
    /**
     * {@link QueryService#getNumberTestedAssays(Long, boolean)}
     *
     */
    void "test get Number Tested Assays #labels"() {

        when:
        int foundNumber = service.getNumberTestedAssays(compoundId, active)
        then:
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        this.queryServiceWrapper.restCompoundService.get(_) >> {compound}
        this.queryServiceWrapper.restCompoundService.getTestedAssays(_, _) >> {assays}

        assert assays.size() == foundNumber

        where:
        label                | compoundId | active | compound  | assays
        "Active Assays Only" | 872        | true   | compound1 | [assay1, assay2]
        "Tested Assays Only" | 872        | false  | compound2 | [assay1, assay2]
    }
    /**
     * {@link QueryService#showCompound(Long)}
     *
     */
    void "test Show Compound #label"() {

        when: "Client enters a CID and the showCompound method is called"
        CompoundAdapter compoundAdapter = service.showCompound(compoundId)
        then: "The CompoundDocument is called"
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        this.queryServiceWrapper.restCompoundService.get(_) >> {compound}
        if (compound) {
            assert compoundAdapter
            assert compoundAdapter.compound
        } else {
            assert !compoundAdapter
        }

        where:
        label                       | compoundId | compound
        "Return a Compound Adapter" | 872        | compound1
        "Unknown Compound"          | 872        | null
        "Null CompoundId"           | null       | null
    }

    /**
     * {@link QueryService#showProject(Long)}
     *
     */
    void "test Show Project"() {
        when: "Client enters a project ID and the showProject method is called"
        Map foundProjectAdapterMap = service.showProject(projectId)
        then: "The Project document is displayed"
        queryServiceWrapper.restProjectService >> { restProjectService }
        restProjectService.get(_) >> {project}
        if (project) {
            assert foundProjectAdapterMap
            ProjectAdapter foundProjectAdapter = foundProjectAdapterMap.projectAdapter
            assert foundProjectAdapter
            assert foundProjectAdapter.project
        } else {
            assert !foundProjectAdapterMap
        }

        where:
        label                      | projectId | project
        "Return a Project Adapter" | 872       | project1
        "Unknown Project"          | 872       | null
        "Null projectId"           | null      | null
    }
    /**
     * {@link QueryService#showAssay(Long)}
     *
     */
    void "test Show Assay"() {
        when: "Client enters a assay ID and the showAssay method is called"
        Map foundAssayMap = service.showAssay(assayId)
        then: "The Assay document is displayed"
        queryServiceWrapper.restAssayService >> { restAssayService }
        restAssayService.get(_) >> {assay}
        if (assay) {
            assert foundAssayMap
            assert foundAssayMap.assayAdapter
            assert foundAssayMap.assayAdapter.assay
        } else {
            assert !foundAssayMap
        }
        where:
        label                     | assayId | assay
        "Return an Assay Adapter" | 872     | assay1
        "Unknown Assay"           | 872     | null
        "Null assayId"            | null    | null

    }
    /**
     * {@link QueryService#findCompoundsByCIDs(List, List)}
     *
     */
    void "test findCompoundsByCIDs #label"() {
        when:
        Map responseMap = service.findCompoundsByCIDs(cids)
        then:
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        expectedNumberOfCalls * restCompoundService.get(_) >> {compound}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.compoundAdapters.size() == expectedNumberOfHits
        where:
        label                    | cids                           | compound               | expectedNumberOfCalls | expectedNumberOfHits
        "Multiple Compound Ids"  | [new Long(872), new Long(111)] | [compound1, compound2] | 1                     | 2
        "Unknown Compound Id"    | [new Long(802)]                | null                   | 1                     | 0
        "Single Compound Id"     | [new Long(872)]                | [compound1]            | 1                     | 1
        "Empty Compound Id list" | []                             | null                   | 0                     | 0

    }
    /**
     * {@link QueryService#findAssaysByADIDs(List, List)}
     *
     */
    void "test findAssaysByPIDs #label"() {
        when:
        Map responseMap = service.findAssaysByADIDs(assayIds)
        then:
        queryServiceWrapper.restAssayService >> { restAssayService }
        queryHelperService.assaysToAdapters(_) >> {assayAdapters}
        expectedNumberOfCalls * restAssayService.get(_) >> {assay}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == expectedNumberOfHits
        where:
        label                 | assayIds                       | assay            | expectedNumberOfCalls | expectedNumberOfHits | assayAdapters
        "Multiple Assay Ids"  | [new Long(872), new Long(111)] | [assay1, assay2] | 1                     | 2                    | [new AssayAdapter(assay1), new AssayAdapter(assay2)]
        "Unknown Assay Id"    | [new Long(802)]                | null             | 1                     | 0                    | null
        "Single Assay Id"     | [new Long(872)]                | [assay1]         | 1                     | 1                    | [new AssayAdapter(assay1)]
        "Empty Assay Id list" | []                             | null             | 0                     | 0                    | null

    }
    /**
     * {@link QueryService#findProjectsByPIDs(List, List)}
     *
     */
    void "test findProjectsByPIDs #label"() {
        when:
        Map responseMap = service.findProjectsByPIDs(projectIds)
        then:
        queryServiceWrapper.restProjectService >> { restProjectService }
        queryHelperService.projectsToAdapters(_) >> {projectAdapters}
        expectedNumberOfCalls * restProjectService.get(_) >> {project}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == expectedNumberOfHits
        where:
        label                   | projectIds                     | project              | expectedNumberOfCalls | expectedNumberOfHits | projectAdapters
        "Multiple Project Ids"  | [new Long(872), new Long(111)] | [project1, project2] | 1                     | 2                    | [new ProjectAdapter(project1), new ProjectAdapter(project2)]
        "Unknown Project Id"    | [new Long(802)]                | null                 | 1                     | 0                    | null
        "Single Project Id"     | [new Long(872)]                | [project1]           | 1                     | 1                    | [new ProjectAdapter(project2)]
        "Empty Project Id list" | []                             | null                 | 0                     | 0                    | null

    }

    /**
     * {@link QueryService#structureSearch(String, StructureSearchParams.Type)}
     *
     */
    void "test Structure Search No Filters #label"() {
        given:
        SearchResult<Compound> iter = Mock(SearchResult)
        when:
        service.structureSearch(smiles, structureSearchParamsType)
        then:
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        restCompoundService.structureSearch(_) >> {iter}

        where:
        label                    | structureSearchParamsType                 | smiles
        "Sub structure Search"   | StructureSearchParams.Type.Substructure   | "CC"
        "Exact match Search"     | StructureSearchParams.Type.Exact          | "CC"
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | "CC"
        "Super structure search" | StructureSearchParams.Type.Superstructure | "CC"
    }
    /**
     * {@link QueryService#structureSearch(String, StructureSearchParams.Type)}
     *
     */
    void "test Structure Search Empty Smiles"() {
        given:
        SearchResult<Compound> iter = Mock(SearchResult)

        when:
        final Map searchResults = service.structureSearch("", StructureSearchParams.Type.Substructure)
        then:
        _ * queryServiceWrapper.restCompoundService >> { restCompoundService }
        _ * restCompoundService.structureSearch(_) >> {iter}
        assert searchResults.nHits == 0
        assert searchResults.compoundAdapters.isEmpty()
        assert searchResults.facets.isEmpty()
    }
    /**
     * {@link QueryService#structureSearch(String, StructureSearchParams.Type, List, int, int)}
     *
     */
    void "test Structure Search #label"() {
        given:
        SearchResult<Compound> iter = Mock(SearchResult)
        List<SearchFilter> searchFilters = [new SearchFilter(filterName: "a", filterValue: "b")]

        List<String[]> filters = [["a", "b"] as String[]]
        when:
        service.structureSearch(smiles, structureSearchParamsType, searchFilters)
        then:
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        queryHelperService.convertSearchFiltersToFilters(_) >> {filters}
        restCompoundService.structureSearch(_) >> {iter}

        where:
        label                    | structureSearchParamsType                 | smiles
        "Sub structure Search"   | StructureSearchParams.Type.Substructure   | "CC"
        "Exact match Search"     | StructureSearchParams.Type.Exact          | "CC"
        "Similarity Search"      | StructureSearchParams.Type.Similarity     | "CC"
        "Super structure search" | StructureSearchParams.Type.Superstructure | "CC"
    }

    /**
     * {@link QueryService#findCompoundsByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Compounds By Text Search"() {
        given:
        StopWatch sw = Mock(StopWatch)
        SearchResult<Compound> iter = Mock(SearchResult)
        when:
        Map map = service.findCompoundsByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        restCompoundService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter(compound1)]}
        assert map == foundMap
        where:
        searchString         | foundMap
        "Some Search String" | compoundAdapterMap1
        ""                   | compoundAdapterMap2

    }
    /**
     * {@link QueryService#findCompoundsByTextSearch(String)}
     *
     */
    void "test Find Compounds By Text Search with defaults #searchString"() {
        given:
        StopWatch sw = Mock(StopWatch)
        SearchResult<Compound> iter = Mock(SearchResult)
        when:
        Map map = service.findCompoundsByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.restCompoundService >> { restCompoundService }
        restCompoundService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter(compound1)]}
        assert map == foundMap
        where:
        searchString         | foundMap
        "Some Search String" | compoundAdapterMap1
        ""                   | compoundAdapterMap2

    }
    /**
     * {@link QueryService#findAssaysByTextSearch(String)}
     *
     */
    void "test Find Assays By Text Search with defaults"() {
        given:
        StopWatch sw = Mock(StopWatch)
        SearchResult<Assay> iter = Mock(SearchResult)
        when:
        Map map = service.findAssaysByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.restAssayService >> { restAssayService }
        restAssayService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.assaysToAdapters(_) >> {assayAdapter}
        assert map.assayAdapters.size() == foundMap.assayAdapters.size()
        where:
        searchString         | foundMap         | assayAdapter
        "Some Search String" | assayAdapterMap1 | [new AssayAdapter(assay1)]
        ""                   | assayAdapterMap2 | null

    }
    /**
     * {@link QueryService#findAssaysByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Assays By Text Search"() {
        given:
        StopWatch sw = Mock(StopWatch)
        SearchResult<Assay> iter = Mock(SearchResult)
        when:
        Map map = service.findAssaysByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.restAssayService >> { restAssayService }
        restAssayService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.assaysToAdapters(_) >> {assayAdapter}
        assert map.assayAdapters.size() == foundMap.assayAdapters.size()
        where:
        searchString         | foundMap         | assayAdapter
        "Some Search String" | assayAdapterMap1 | [new AssayAdapter(assay1)]
        ""                   | assayAdapterMap2 | null

    }
    /**
     * {@link QueryService#findProjectsByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Projects By Text Search"() {
        given:
        StopWatch sw = Mock(StopWatch)
        SearchResult<Project> iter = Mock(SearchResult)
        when:
        Map map = service.findProjectsByTextSearch(searchString, 10, 0, [])
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * queryServiceWrapper.restProjectService >> { restProjectService }
        _ * restProjectService.search(_) >> {iter}
        _ * queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        _ * queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        _ * queryHelperService.projectsToAdapters(_) >> {projectAdapter}
        assert map.projectAdapters.size() == foundMap.projectAdapters.size()
        where:
        searchString         | foundMap           | projectAdapter
        "Some Search String" | projectAdapterMap1 | [new ProjectAdapter(project1)]
        ""                   | projectAdapterMap2 | null
    }

    /**
     * {@link QueryService#findProjectsByTextSearch(String)}
     *
     */
    void "test Find Projects By Text Search with defaults"() {
        given:
        StopWatch sw = Mock(StopWatch)
        SearchResult<Project> iter = Mock(SearchResult)
        when:
        Map map = service.findProjectsByTextSearch(searchString)
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * queryServiceWrapper.restProjectService >> { restProjectService }
        _ * restProjectService.search(_) >> {iter}
        _ * queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        _ * queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        _ * queryHelperService.projectsToAdapters(_) >> {projectAdapter}
        assert map.projectAdapters.size() == foundMap.projectAdapters.size()
        where:
        searchString         | foundMap           | projectAdapter
        "Some Search String" | projectAdapterMap1 | [new ProjectAdapter(project1)]
        ""                   | projectAdapterMap2 | null
    }
    /**
     * {@link QueryService#autoComplete(String)}
     *
     */
    void "test auto Complete"() {
        given:
        Map map = [a: "b"]
        when:
        List list = service.autoComplete("Some Search String")
        then:
        queryServiceWrapper.restAssayService >> { restAssayService }
        restAssayService.suggest(_) >> {map}
        queryHelperService.autoComplete(_, _) >> {[map]}
        assert list
    }
    /**
     * {@link QueryService#findFiltersInSearchBox(List, String)}
     */
    void "test find Filters in search box"() {
        when:
        List<SearchFilter> searchFilters = []
        service.findFiltersInSearchBox(searchFilters, "gobp_term:DNA Repair")
        then:
        queryHelperService.findFiltersInSearchBox(_, _) >> {}
    }
}
