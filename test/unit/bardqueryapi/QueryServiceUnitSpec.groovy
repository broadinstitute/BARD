package bardqueryapi

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.RESTAssayService
import bard.core.rest.RESTCompoundService
import bard.core.rest.RESTExperimentService
import bard.core.rest.RESTProjectService
import grails.test.mixin.TestFor
import org.apache.commons.lang.time.StopWatch
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.*

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

    void setup() {
        restCompoundService = Mock(RESTCompoundService.class)
        restProjectService = Mock(RESTProjectService.class)
        restAssayService = Mock(RESTAssayService.class)
        restExperimentService = Mock(RESTExperimentService.class)
        queryServiceWrapper = Mock(QueryServiceWrapper.class)
        queryHelperService = Mock(QueryHelperService.class)
        service.queryHelperService = queryHelperService
        service.queryServiceWrapper = queryServiceWrapper

    }

    void tearDown() {
        // Tear down logic here
    }

    /**
     * {@link QueryService#showCompound(Long)}
     *
     */
    void "test Show Compound #label"() {

        when: "Client enters a CID and the showCompound method is called"
        CompoundAdapter compoundAdapter = service.showCompound(compoundId)
        then: "The CompoundDocument is called"
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        this.queryServiceWrapper.getRestCompoundService().get(_) >> {compound}
        if (compound) {
            assert compoundAdapter
            assert compoundAdapter.compound
        } else {
            assert !compoundAdapter
        }

        where:
        label                       | compoundId       | compound
        "Return a Compound Adapter" | new Integer(872) | new Compound(name: "C1")
        "Unknown Compound"          | new Integer(872) | null
        "Null CompoundId"           | null             | null
    }

    /**
     * {@link QueryService#showProject(Long)}
     *
     */
    void "test Show Project"() {
        when: "Client enters a project ID and the showProject method is called"
        Map foundProjectAdapterMap = service.showProject(projectId)
        then: "The Project document is displayed"
        queryServiceWrapper.getRestProjectService() >> { restProjectService }
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
        label                      | projectId        | project
        "Return a Project Adapter" | new Integer(872) | new Project(name: "C1")
        "Unknown Project"          | new Integer(872) | null
        "Null projectId"           | null             | null
    }
    /**
     * {@link QueryService#showAssay(Long)}
     *
     */
    void "test Show Assay"() {
        when: "Client enters a assay ID and the showAssay method is called"
        Map foundAssayMap = service.showAssay(assayId)
        then: "The Assay document is displayed"
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        restAssayService.get(_) >> {assay}
        if (assay) {
            assert foundAssayMap
            assert foundAssayMap.assayAdapter
            assert foundAssayMap.assayAdapter.assay
        } else {
            assert !foundAssayMap
        }
        where:
        label                     | assayId          | assay
        "Return an Assay Adapter" | new Integer(872) | new Assay(name: "C1")
        "Unknown Assay"           | new Integer(872) | null
        "Null assayId"            | null             | null

    }
    /**
     * {@link QueryService#findCompoundsByCIDs(List, List)}
     *
     */
    void "test findCompoundsByCIDs #label"() {
        when:
        Map responseMap = service.findCompoundsByCIDs(cids)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        expectedNumberOfCalls * restCompoundService.get(_) >> {compound}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.compoundAdapters.size() == expectedNumberOfHits
        where:
        label                    | cids                           | compound                                             | expectedNumberOfCalls | expectedNumberOfHits
        "Multiple Compound Ids"  | [new Long(872), new Long(111)] | [new Compound(name: "C1"), new Compound(name: "C2")] | 1                     | 2
        "Unknown Compound Id"    | [new Long(802)]                | null                                                 | 1                     | 0
        "Single Compound Id"     | [new Long(872)]                | [new Compound(name: "C1")]                           | 1                     | 1
        "Empty Compound Id list" | []                             | null                                                 | 0                     | 0

    }
    /**
     * {@link QueryService#findAssaysByADIDs(List, List)}
     *
     */
    void "test findAssaysByPIDs #label"() {
        when:
        Map responseMap = service.findAssaysByADIDs(assayIds)
        then:
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        queryHelperService.assaysToAdapters(_) >> {assayAdapters}
        expectedNumberOfCalls * restAssayService.get(_) >> {assay}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == expectedNumberOfHits
        where:
        label                 | assayIds                       | assay                                          | expectedNumberOfCalls | expectedNumberOfHits | assayAdapters
        "Multiple Assay Ids"  | [new Long(872), new Long(111)] | [new Assay(name: "C1"), new Assay(name: "C2")] | 1                     | 2                    | [new AssayAdapter(new Assay(name: "C1")), new AssayAdapter(new Assay(name: "C2"))]
        "Unknown Assay Id"    | [new Long(802)]                | null                                           | 1                     | 0                    | null
        "Single Assay Id"     | [new Long(872)]                | [new Assay(name: "C1")]                        | 1                     | 1                    | [new AssayAdapter(new Assay(name: "C1"))]
        "Empty Assay Id list" | []                             | null                                           | 0                     | 0                    | null

    }
    /**
     * {@link QueryService#findProjectsByPIDs(List, List)}
     *
     */
    void "test findProjectsByPIDs #label"() {
        when:
        Map responseMap = service.findProjectsByPIDs(projectIds)
        then:
        queryServiceWrapper.getRestProjectService() >> { restProjectService }
        queryHelperService.projectsToAdapters(_) >> {projectAdapters}
        expectedNumberOfCalls * restProjectService.get(_) >> {project}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == expectedNumberOfHits
        where:
        label                   | projectIds                     | project                                            | expectedNumberOfCalls | expectedNumberOfHits | projectAdapters
        "Multiple Project Ids"  | [new Long(872), new Long(111)] | [new Project(name: "C1"), new Project(name: "C2")] | 1                     | 2                    | [new ProjectAdapter(new Project(name: "C1")), new ProjectAdapter(new Project(name: "C2"))]
        "Unknown Project Id"    | [new Long(802)]                | null                                               | 1                     | 0                    | null
        "Single Project Id"     | [new Long(872)]                | [new Project(name: "C1")]                          | 1                     | 1                    | [new ProjectAdapter(new Project(name: "C1"))]
        "Empty Project Id list" | []                             | null                                               | 0                     | 0                    | null

    }

    /**
     * {@link QueryService#structureSearch(String, StructureSearchParams.Type)}
     *
     */
    void "test Structure Search No Filters #label"() {
        given:
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)
        when:
        service.structureSearch(smiles, structureSearchParamsType)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
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
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)

        when:
        final Map searchResults = service.structureSearch("", StructureSearchParams.Type.Substructure)
        then:
        0*queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        0*restCompoundService.structureSearch(_) >> {iter}
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
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)
        List<SearchFilter> searchFilters = [new SearchFilter(filterName: "a", filterValue: "b")]

        List<String[]> filters = [["a", "b"] as String[]]
        when:
        service.structureSearch(smiles, structureSearchParamsType, searchFilters)
        then:
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
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
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)
        when:
        Map map = service.findCompoundsByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        restCompoundService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter(new Compound("name"))]}
        assert map == foundMap
        where:
        searchString         | foundMap
        "Some Search String" | [compoundAdapters: [], facets: null, nHits: 0]
        ""                   | [compoundAdapters: [], facets: [], nHits: 0]

    }
    /**
     * {@link QueryService#findCompoundsByTextSearch(String)}
     *
     */
    void "test Find Compounds By Text Search with defaults #searchString"() {
        given:
        StopWatch sw = Mock(StopWatch)
        ServiceIterator<Compound> iter = Mock(ServiceIterator.class)
        when:
        Map map = service.findCompoundsByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.getRestCompoundService() >> { restCompoundService }
        restCompoundService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.compoundsToAdapters(_) >> {[new CompoundAdapter(new Compound("name"))]}
        assert map == foundMap
        where:
        searchString         | foundMap
        "Some Search String" | [compoundAdapters: [], facets: null, nHits: 0]
        ""                   | [compoundAdapters: [], facets: [], nHits: 0]

    }
    /**
     * {@link QueryService#findAssaysByTextSearch(String)}
     *
     */
    void "test Find Assays By Text Search with defaults"() {
        given:
        StopWatch sw = Mock(StopWatch)
        ServiceIterator<Assay> iter = Mock(ServiceIterator.class)
        when:
        Map map = service.findAssaysByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        restAssayService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.assaysToAdapters(_) >> {assayAdapter}
        assert map.assayAdapters.size() == foundMap.assayAdapters.size()
        where:
        searchString         | foundMap                                                                     | assayAdapter
        "Some Search String" | [assayAdapters: [new AssayAdapter(new Assay("name"))], facets: [], nHits: 0] | [new AssayAdapter(new Assay("name"))]
        ""                   | [assayAdapters: [], facets: null, nHits: 0]                                  | null

    }
    /**
     * {@link QueryService#findAssaysByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Assays By Text Search"() {
        given:
        StopWatch sw = Mock(StopWatch)
        ServiceIterator<Assay> iter = Mock(ServiceIterator.class)
        when:
        Map map = service.findAssaysByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        queryServiceWrapper.getRestAssayService() >> { restAssayService }
        restAssayService.search(_) >> {iter}
        queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        queryHelperService.assaysToAdapters(_) >> {assayAdapter}
        assert map.assayAdapters.size() == foundMap.assayAdapters.size()
        where:
        searchString         | foundMap                                                                     | assayAdapter
        "Some Search String" | [assayAdapters: [new AssayAdapter(new Assay("name"))], facets: [], nHits: 0] | [new AssayAdapter(new Assay("name"))]
        ""                   | [assayAdapters: [], facets: null, nHits: 0]                                  | null

    }
    /**
     * {@link QueryService#findProjectsByTextSearch(String, Integer, Integer, List)}
     *
     */
    void "test Find Projects By Text Search"() {
        given:
        StopWatch sw = Mock(StopWatch)
        ServiceIterator<Project> iter = Mock(ServiceIterator.class)
        when:
        Map map = service.findProjectsByTextSearch(searchString, 10, 0, [])
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * queryServiceWrapper.getRestProjectService() >> { restProjectService }
        _ * restProjectService.search(_) >> {iter}
        _ * queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        _ * queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        _ * queryHelperService.projectsToAdapters(_) >> {projectAdapter}
        assert map.projectAdapters.size() == foundMap.projectAdapters.size()
        where:
        searchString         | foundMap                                                                             | projectAdapter
        "Some Search String" | [projectAdapters: [new ProjectAdapter(new Project("name"))], facets: null, nHits: 0] | [new ProjectAdapter(new Project("name"))]
        ""                   | [projectAdapters: [], facets: [], nHits: 0]                                          | null
    }

    /**
     * {@link QueryService#findProjectsByTextSearch(String)}
     *
     */
    void "test Find Projects By Text Search with defaults"() {
        given:
        StopWatch sw = Mock(StopWatch)
        ServiceIterator<Project> iter = Mock(ServiceIterator.class)
        when:
        Map map = service.findProjectsByTextSearch(searchString)
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * queryServiceWrapper.getRestProjectService() >> { restProjectService }
        _ * restProjectService.search(_) >> {iter}
        _ * queryHelperService.stripCustomStringFromSearchString(_) >> {"stuff"}
        _ * queryHelperService.constructSearchParams(_, _, _, _) >> { new SearchParams(searchString)}
        _ * queryHelperService.projectsToAdapters(_) >> {projectAdapter}
        assert map.projectAdapters.size() == foundMap.projectAdapters.size()
        where:
        searchString         | foundMap                                                                             | projectAdapter
        "Some Search String" | [projectAdapters: [new ProjectAdapter(new Project("name"))], facets: null, nHits: 0] | [new ProjectAdapter(new Project("name"))]
        ""                   | [projectAdapters: [], facets: [], nHits: 0]                                          | null
    }
    /**
     * {@link QueryService#autoComplete(String)}
     *
     */
    void "test auto Complete"() {
        when:
        List list = service.autoComplete("Some Search String")
        then:
        1 * queryServiceWrapper.getRestAssayService() >> { restAssayService }
        1 * restAssayService.suggest(_) >> {[a: "b"]}
        1 * queryHelperService.autoComplete(_, _) >> {[[a: "b"]]}
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
        1 * queryHelperService.findFiltersInSearchBox(_, _) >> {}
    }
}
