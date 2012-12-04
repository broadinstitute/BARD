package bardqueryapi

import bard.core.SearchParams
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.AssayRestService
import bard.core.rest.spring.CompoundRestService
import bard.core.rest.spring.ExperimentRestService
import bard.core.rest.spring.ProjectRestService
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.assays.ExpandedAssay
import bard.core.rest.spring.assays.ExpandedAssayResult
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundResult
import bard.core.rest.spring.compounds.PromiscuityScore
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectResult
import bard.core.rest.spring.util.StructureSearchParams
import grails.test.mixin.TestFor
import org.apache.commons.lang.time.StopWatch
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.assays.AssayResult

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Unroll
@TestFor(QueryService)
class QueryServiceUnitSpec extends Specification {
    QueryHelperService queryHelperService
    AssayRestService assayRestService
    CompoundRestService compoundRestService
    ProjectRestService projectRestService
    ExperimentRestService experimentRestService
    @Shared ExpandedAssayResult expandedAssayResult = new ExpandedAssayResult()
    @Shared CompoundResult compoundResult = new CompoundResult()
    @Shared ExpandedAssay expandedAssay1 = new ExpandedAssay()
    @Shared ExpandedAssay expandedAssay2 = new ExpandedAssay()
    @Shared ProjectResult projectResult = new ProjectResult()
    @Shared Assay assay1 = new Assay(name: "A1")
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
        compoundRestService = Mock(CompoundRestService)
        projectRestService = Mock(ProjectRestService)
        assayRestService = Mock(AssayRestService)
        experimentRestService = Mock(ExperimentRestService)
        queryHelperService = Mock(QueryHelperService)

        service.queryHelperService = queryHelperService
        service.assayRestService = assayRestService
        service.compoundRestService = compoundRestService
        service.projectRestService = projectRestService
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
        this.compoundRestService.getCompoundById(_) >> {compound}
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
    void "test Show Project #label"() {

        given:
        final ExperimentSearch experiment = new ExperimentSearch()
        experiment.classification = 0
        List<ExperimentSearch> experiments = [experiment]
        List<Assay> assays = Mock(List)

        when: "Client enters a project ID and the showProject method is called"
        Map foundProjectAdapterMap = service.showProject(projectId)
        then: "The ProjectSearchResult document is displayed"
        projectRestService.getProjectById(_) >> {project}
        projectRestService.findExperimentsByProjectId(_) >> {experiments}
        projectRestService.findAssaysByProjectId(_) >> {assays}

        if (project) {
            assert foundProjectAdapterMap
            ProjectAdapter foundProjectAdapter = foundProjectAdapterMap.projectAdapter
            assert foundProjectAdapter
            assert foundProjectAdapter.project
        } else {
            assert !foundProjectAdapterMap
        }

        where:
        label                                  | projectId | project
        "Return a ProjectSearchResult Adapter" | 872       | project1
        "Unknown ProjectSearchResult"          | 872       | null
        "Null projectId"                       | null      | null
    }
    /**
     * {@link QueryService#showAssay(Long)}
     *
     */
    void "test Show Assay #label"() {
        given:
        ExpandedAssay expandedAssay = new ExpandedAssay()
        expandedAssay.assayId = assayId
        when: "Client enters a assay ID and the showAssay method is called"
        Map foundAssayMap = service.showAssay(assayId)
        then: "The Assay document is displayed"
        assayRestService.getAssayById(_) >> {expandedAssay}
        assert foundAssayMap
        assert foundAssayMap.assayAdapter

        where:
        label                     | assayId | assay
        "Return an Assay Adapter" | 872     | assay1
        "Unknown Assay"           | 872     | null
    }
    void "test findCompoundsByCIDs - Non existing ids"() {
        when:
        Map responseMap = service.findCompoundsByCIDs([2,3])
        then:
        1 * compoundRestService.searchCompoundsByIds(_) >> {null}
        and:
        assert responseMap
        assert responseMap.nHits == 0
        assert !responseMap.facets
        assert responseMap.compoundAdapters.size() == 0
    }
    /**
     * {@link QueryService#findCompoundsByCIDs(List, List)}
     *
     */
    void "test findCompoundsByCIDs #label"() {
        given:
        compoundResult.compounds = compound
        when:
        Map responseMap = service.findCompoundsByCIDs(cids)
        then:
        expectedNumberOfCalls * compoundRestService.searchCompoundsByIds(_) >> {compoundResult}
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
    void "test findAssaysByADIDs #label"() {
        given:
        expandedAssayResult.assays = assay
        when:
        Map responseMap = service.findAssaysByADIDs(assayIds)
        then:
        queryHelperService.assaysToAdapters(_) >> {assayAdapters}
        expectedNumberOfCalls * assayRestService.searchAssaysByIds(_) >> {expandedAssayResult}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == expectedNumberOfHits
        where:
        label                 | assayIds                       | assay                            | expectedNumberOfCalls | expectedNumberOfHits | assayAdapters
        "Multiple Assay Ids"  | [new Long(872), new Long(111)] | [expandedAssay1, expandedAssay2] | 1                     | 2                    | [new AssayAdapter(expandedAssay1), new AssayAdapter(expandedAssay2)]
        "Unknown Assay Id"    | [new Long(802)]                | null                             | 1                     | 0                    | null
        "Single Assay Id"     | [new Long(872)]                | [expandedAssay1]                 | 1                     | 1                    | [new AssayAdapter(expandedAssay1)]
        "Empty Assay Id list" | []                             | null                             | 0                     | 0                    | null

    }
    /**
     * {@link QueryService#findAssaysByADIDs(List, List)}
     *
     */
    void "test findAssaysByADIDs - Non existing ids"() {
        when:
        Map responseMap = service.findAssaysByADIDs([2,3])
        then:
        1 * assayRestService.searchAssaysByIds(_) >> {null}
        and:
        assert responseMap
        assert responseMap.nHits == 0
        assert !responseMap.facets
        assert responseMap.assayAdapters.size() == 0
    }

    /**
     * {@link QueryService#findProjectsByPIDs(List, List)}
     *
     */
    void "test findProjectsByPIDs - Non existing ids"() {
        when:
        Map responseMap = service.findProjectsByPIDs([2,3])
        then:
        1 * projectRestService.searchProjectsByIds(_) >> {null}
        and:
        assert responseMap
        assert responseMap.nHits == 0
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == 0
    }
    /**
     * {@link QueryService#findProjectsByPIDs(List, List)}
     *
     */
    void "test findProjectsByPIDs #label"() {
        given:
        projectResult.projects = project
        when:
        Map responseMap = service.findProjectsByPIDs(projectIds)
        then:
        queryHelperService.projectsToAdapters(_) >> {projectAdapters}
        expectedNumberOfCalls * projectRestService.searchProjectsByIds(_) >> {projectResult}
        and:
        assert responseMap
        assert responseMap.nHits == expectedNumberOfHits
        assert !responseMap.facets
        assert responseMap.projectAdapters.size() == expectedNumberOfHits
        where:
        label                               | projectIds                     | project              | expectedNumberOfCalls | expectedNumberOfHits | projectAdapters
        "Multiple ProjectSearchResult Ids"  | [new Long(872), new Long(111)] | [project1, project2] | 1                     | 2                    | [new ProjectAdapter(project1), new ProjectAdapter(project2)]
        "Unknown ProjectSearchResult Id"    | [new Long(802)]                | null                 | 1                     | 0                    | null
        "Single ProjectSearchResult Id"     | [new Long(872)]                | [project1]           | 1                     | 1                    | [new ProjectAdapter(project2)]
        "Empty ProjectSearchResult Id list" | []                             | null                 | 0                     | 0                    | null

    }

    /**
     * {@link QueryService#structureSearch(String, bard.core.rest.spring.util.StructureSearchParams.Type)}
     *
     */
    void "test Structure Search No Filters #label"() {
        given:
        final CompoundResult expandedCompoundResult = new CompoundResult(compounds: [new Compound(smiles: smiles)])
        when:
        service.structureSearch(smiles, structureSearchParamsType)
        then:
        compoundRestService.structureSearch(_) >> {expandedCompoundResult}

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
        CompoundResult compoundResult = Mock(CompoundResult)

        when:
        final Map searchResults = service.structureSearch("", StructureSearchParams.Type.Substructure)
        then:
        _ * compoundRestService.structureSearch(_, _) >> {compoundResult}
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
        CompoundResult compoundResult = Mock(CompoundResult)
        List<SearchFilter> searchFilters = [new SearchFilter(filterName: "a", filterValue: "b")]

        List<String[]> filters = [["a", "b"] as String[]]
        when:
        service.structureSearch(smiles, structureSearchParamsType, searchFilters)
        then:
        queryHelperService.convertSearchFiltersToFilters(_) >> {filters}
        compoundRestService.structureSearch(_) >> {compoundResult}

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
    void "test Find Compounds By Text Search #label"() {
        given:
        StopWatch sw = Mock(StopWatch)
        final CompoundResult compoundResult = Mock(CompoundResult)
        when:
        Map map = service.findCompoundsByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        compoundRestService.findCompoundsByFreeTextSearch(_) >> {compoundResult}
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
        final CompoundResult compoundResult = Mock(CompoundResult)
        when:
        Map map = service.findCompoundsByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        compoundRestService.findCompoundsByFreeTextSearch(_) >> {compoundResult}
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
        AssayResult assayResult = Mock(AssayResult)
        when:
        Map map = service.findAssaysByTextSearch(searchString)
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        assayRestService.findAssaysByFreeTextSearch(_) >> {assayResult}
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
        AssayResult assayResult = Mock(AssayResult)
        when:
        Map map = service.findAssaysByTextSearch(searchString, 10, 0, [])
        then:
        queryHelperService.startStopWatch() >> {sw}
        queryHelperService.stopStopWatch(_, _) >> {}
        assayRestService.findAssaysByFreeTextSearch(_) >> {assayResult}
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
        ProjectResult projectResult = Mock(ProjectResult)
        when:
        Map map = service.findProjectsByTextSearch(searchString, 10, 0, [])
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * projectRestService.findProjectsByFreeTextSearch(_) >> {projectResult}
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
        ProjectResult projectResult = Mock(ProjectResult)
        when:
        Map map = service.findProjectsByTextSearch(searchString)
        then:
        _ * queryHelperService.startStopWatch() >> {sw}
        _ * queryHelperService.stopStopWatch(_, _) >> {}
        _ * projectRestService.findProjectsByFreeTextSearch(_) >> {projectResult}
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
        assayRestService.suggest(_) >> {map}
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

    void "test findPromiscuityScoreForCID #label"() {
        when:
        final Map promiscuityScoreMap = service.findPromiscuityScoreForCID(cid)
        then:
        compoundRestService.findPromiscuityScoreForCompound(_) >> {promiscuityScore}

        assert promiscuityScoreMap.status == expectedStatus
        assert promiscuityScoreMap.message == expectedMessage
        assert promiscuityScoreMap.promiscuityScore?.cid == promiscuityScore?.cid
        where:
        label                              | cid   | promiscuityScore                | expectedStatus | expectedMessage
        "Returns a Promiscuity Score"      | 1234  | new PromiscuityScore(cid: 1234) | 200            | "Success"
        "Returns a null Promiscuity Score" | 23435 | null                            | 404            | "Error getting Promiscuity Score for 23435"


    }
}
