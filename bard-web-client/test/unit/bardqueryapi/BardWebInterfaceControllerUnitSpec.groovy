package bardqueryapi

import bard.core.DataSource
import bard.core.IntValue
import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.interfaces.ExperimentRole
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.PromiscuityScore
import bard.core.rest.spring.compounds.Scaffold
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import com.metasieve.shoppingcart.ShoppingCartService
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import molspreadsheet.MolecularSpreadSheetService
import molspreadsheet.SpreadSheetActivity
import org.apache.http.HttpException
import org.json.JSONArray
import querycart.CartAssay
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.servlet.http.HttpServletResponse
import grails.test.mixin.Mock
import bardwebquery.CompoundOptionsTagLib

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
@Mock(CompoundOptionsTagLib)
@Unroll
class BardWebInterfaceControllerUnitSpec extends Specification {
    MolecularSpreadSheetService molecularSpreadSheetService
    QueryService queryService
    ShoppingCartService shoppingCartService
    MobileService mobileService

    @Shared List<SearchFilter> searchFilters = [new SearchFilter(filterName: 'group1', filterValue: 'facet1'), new SearchFilter(filterName: 'group2', filterValue: 'facet2')]
    @Shared Value facet1 = new IntValue(source: new DataSource(), id: 'group1', value: null, children: [new IntValue(source: new DataSource(), id: 'facet1', value: 1)])
    @Shared Value facet3 = new IntValue(source: new DataSource(), id: 'group3', value: null, children: [new IntValue(source: new DataSource(), id: 'facet3', value: 1)])
    @Shared List<SearchFilter> searchFilters1 = [new SearchFilter(filterName: "a", filterValue: "b")]
    @Shared String EMPTY_STRING = ''

    void setup() {
        controller.metaClass.mixin(SearchHelper)
        queryService = Mock(QueryService)
        molecularSpreadSheetService = Mock(MolecularSpreadSheetService)
        controller.queryService = this.queryService
        controller.molecularSpreadSheetService = this.molecularSpreadSheetService
        shoppingCartService = Mock(ShoppingCartService)
        controller.shoppingCartService = this.shoppingCartService
        views['/bardWebInterface/_assays.gsp'] = 'mock content'
        views['/bardWebInterface/_projects.gsp'] = 'mock content'
        views['/bardWebInterface/_compounds.gsp'] = 'mock content'
        mobileService = Mock(MobileService)
        controller.mobileService = this.mobileService
    }

    void "test index"() {

        when:
        controller.index()
        then:
        assert response.status == 200
    }

    void "test search"() {
        given:
        final String searchString = "Search String"
        params.searchString = searchString
        when:
        controller.search()
        then:
        assert response.status == 302
        assert response.redirectedUrl == '/bardWebInterface/searchResults'
    }

    void "test searchResults"() {
        when:
        controller.searchResults()
        then:
        assert response.status == 200
    }

    void "test showExperiment #label"() {
        given:
        params.id = "222"
        when:
        controller.showExperiment()
        then:
        assert response.status == 200
        assert view == '/bardWebInterface/showExperimentResult'
        assert model.experimentId == "222"
    }


    void "test find substance Ids #label"() {
        when:
        controller.findSubstanceIds(cid)
        then:
        _ * this.queryService.findSubstancesByCid(_) >> {sids}
        assert response.status == statusCode

        where:
        label              | cid  | statusCode                | sids
        "Null CID"         | null | HttpServletResponse.SC_OK | []
        "Existing CID"     | 567  | HttpServletResponse.SC_OK | [2, 3]
        "CID with no SIDs" | 56   | HttpServletResponse.SC_OK | []

    }

    void "test findSubstanceIds With Exception"() {
        given:
        Long id = 234
        when:
        controller.findSubstanceIds(id)
        then:
        this.queryService.findSubstancesByCid(_) >> {throw new HttpException("Some message")}
        assert response.status == 500

    }

    void "test showExperimentResult #label"() {
        when:
        controller.showExperimentResult(eid)
        then:
        _ * this.molecularSpreadSheetService.findExperimentDataById(_, _, _) >> {experimentData}
        assert response.status == statusCode

        where:
        label                          | eid  | statusCode                         | experimentData
        "Empty Null EID - Bad Request" | null | HttpServletResponse.SC_BAD_REQUEST | null
        "EID- Not Found"               | 234  | HttpServletResponse.SC_NOT_FOUND   | null
        "Success"                      | 567  | HttpServletResponse.SC_OK          | [total: 2, spreadSheetActivities: [
                new SpreadSheetActivity(eid: new Long(567), cid: new Long(1), sid: new Long(20))],
                role: ExperimentRole.Counterscreen, experiment: new ExperimentSearch(name: 'name', assayId: 1)]
    }

    void "test showExperimentResult With Exception"() {
        given:
        Long id = 234
        when:
        controller.showExperimentResult(id)
        then:
        _ * this.molecularSpreadSheetService.findExperimentDataById(_, _, _) >> {throw new HttpException("Some message")}
        assert response.status == 404
        assert flash.message == "Problem finding Experiment ${id}"

    }

    void "test promiscuity action #label"() {
        given:
        Map promiscuityScoreMap = [status: statusCode, promiscuityScore: promiscuityScore]

        when:
        controller.promiscuity(cid)
        then:
        _ * this.queryService.findPromiscuityScoreForCID(_) >> {promiscuityScoreMap}
        assert response.status == statusCode

        where:
        label                          | cid  | statusCode                                   | scaffolds                  | promiscuityScore
        "Empty Null CID - Bad Request" | null | HttpServletResponse.SC_BAD_REQUEST           | null                       | null
        "CID- Internal Server Error"   | 234  | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null                       | null
        "Success"                      | 567  | HttpServletResponse.SC_OK                    | [new Scaffold(pScore: 22)] | new PromiscuityScore(cid: 567, scaffolds: [new Scaffold(pScore: 222)])
    }

    void "test promiscuity action with exception"() {
        given:
        final Long cid = 222
        when:
        controller.promiscuity(cid)
        then:
        _ * this.queryService.findPromiscuityScoreForCID(_) >> {throw new HttpException("Error Message")}
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
    }

    void "test promiscuity action with status code 404"() {
        given:
        Map map = [status: 404, message: 'Success', promiscuityScore: null]
        final Long cid = 222
        when:
        controller.promiscuity(cid)
        then:
        _ * this.queryService.findPromiscuityScoreForCID(_) >> {map}
        assert response.status == HttpServletResponse.SC_NOT_FOUND
    }
    // return [status: resp.status, message: 'Success', promiscuityScore: promiscuityScore]

    void "test handle Assay Searches #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.AssayFacetForm.toString()
        Map paramMap = [formName: FacetFormType.AssayFacetForm.toString(), searchString: searchString, filters: searchFilters1]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        params.skip = "0"
        params.top = "10"
        controller.handleAssaySearches(this.queryService, searchCommand)
        then:
        _ * this.queryService.findAssaysByTextSearch(_, _, _, _) >> {assayAdapterMap}
        assert response.status == statusCode

        where:
        label                                  | searchString  | statusCode                                   | assayAdapterMap
        "Empty Search String - Bad Request"    | ""            | HttpServletResponse.SC_BAD_REQUEST           | null
        "Search String- Internal Server Error" | "Some String" | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"                              | "1234,5678"   | HttpServletResponse.SC_OK                    | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2]


    }

    void "test handle Project Searches #label"() {
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.ProjectFacetForm.toString()
        Map paramMap = [
                formName: FacetFormType.ProjectFacetForm.toString(),
                searchString: searchString,
                filters: searchFilters1
        ]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        params.skip = "0"
        params.top = "10"
        controller.handleProjectSearches(this.queryService, searchCommand)
        then:
        _ * this.queryService.findProjectsByTextSearch(_, _, _, _) >> {projectAdapterMap}
        assert response.status == statusCode

        where:
        label                                  | searchString  | statusCode                                   | projectAdapterMap
        "Empty Search String"                  | ""            | HttpServletResponse.SC_BAD_REQUEST           | null
        "Search String- Internal Server Error" | "Some String" | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"                              | "1234,5678"   | HttpServletResponse.SC_OK                    | [projectAdapters: [buildProjectAdapter(1234, "assay1"), buildProjectAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test handle Compound Searches #label"() {
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.CompoundFacetForm.toString(), searchString: searchString, filters: searchFilters1]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        params.skip = "0"
        params.top = "10"
        controller.handleCompoundSearches(this.queryService, searchCommand)
        then:
        _ * this.queryService.findCompoundsByTextSearch(_, _, _, _) >> {compoundAdapterMap}
        assert response.status == statusCode

        where:
        label                                  | searchString  | statusCode                                   | compoundAdapterMap
        "Empty Search String"                  | ""            | HttpServletResponse.SC_BAD_REQUEST           | null
        "Search String- Internal Server Error" | "Some String" | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | null
        "Success"                              | "1234,5678"   | HttpServletResponse.SC_OK                    | [compoundAdapters: [buildCompoundAdapter(1234), buildCompoundAdapter(4567)], facets: [], nHits: 2]
    }

    void "test apply Filters to projects #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.ProjectFacetForm.toString()
        Map paramMap = [formName: FacetFormType.ProjectFacetForm.toString(), searchString: searchString, filters: searchFilters1]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        controller.searchProjects(searchCommand)
        then:
        _ * this.queryService.findProjectsByTextSearch(_, _, _, _) >> {projectAdapterMap}
        this.mobileService.detect(_) >> {false}
        assert response.status == statusCode
        where:
        label                 | searchString | statusCode                         | projectAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [projectAdapters: [buildProjectAdapter(1234, "assay1"), buildProjectAdapter(1234, "assay2")], facets: [], nHits: 2]
    }

    void "test apply Filters to compounds #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.CompoundFacetForm.toString(), searchString: searchString, filters: searchFilters1]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        controller.searchCompounds(searchCommand)
        then:
        _ * this.queryService.findCompoundsByTextSearch(_, _, _, _) >> {compoundAdapterMap}
        assert response.status == statusCode
        where:
        label                 | searchString | statusCode                         | compoundAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [compoundAdapters: [buildCompoundAdapter(1234), buildCompoundAdapter(4567)], facets: [], nHits: 2]
    }

    void "test apply Filters to assays #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.AssayFacetForm.toString()
        Map paramMap = [formName: FacetFormType.AssayFacetForm.toString(), searchString: searchString, filters: searchFilters1]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        controller.searchAssays(searchCommand)
        then:
        _ * this.queryService.findAssaysByTextSearch(_, _, _, _) >> {assayAdapterMap}
        assert response.status == statusCode
        where:
        label                 | searchString | statusCode                         | assayAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2]
    }

    void "test handle search Params #label"() {
        given:
        params.searchString = searchString
        if (offset) {
            params.offset = offset
        }
        if (max) {
            params.max = max
        }
        when:
        Map map = controller.handleSearchParams()
        then:
        map.skip == expectedSkip
        map.top == expectedTop
        where:
        label                 | searchString | offset | max  | expectedSkip | expectedTop
        "Empty Search String" | ""           | "0"    | "10" | 0            | 10
        "No offset Param"     | "1234,5678"  | ""     | "10" | 0            | 10
        "No max Param"        | "1234,5678"  | "10"   | ""   | 10           | 10
        "All Params"          | "1234,5678"  | "10"   | "10" | 10           | 10

    }

    void "test search String To Id List #label"() {
        when:
        List<Long> results = controller.searchStringToIdList(searchString)
        then:
        results.size() == expectedResults.size()
        //sort both and then compare
        results.sort()
        expectedResults.sort()
        for (int index = 0; index < expectedResults.size(); index++) {
            assert results.get(index) == expectedResults.get(index);
        }
        where:
        label                                                              | searchString                   | expectedResults
        "Single id"                                                        | "1234"                         | ["1234"]
        "Multiple ids, comma delimited with some spaces"                   | "1234, 5678, 898,    445"      | ["1234", "5678", "898", "445"]
        "Two ids, comma delimited with spaces"                             | "1234,        5678"            | ["1234", "5678"]
        "ids delimited by spaces only"                                     | "1234   5678"                  | ["1234", "5678"]
        "ids delimited by commas only"                                     | "1234,5678"                    | ["1234", "5678"]
        "ids delimited with a mixture of spaces and commas"                | "788, 200 300, 1234, 5678 600" | ["788", "200", "300", "1234", "5678", "600"]
        "ids delimited with a mixture of spaces and commas and duplicates" | "788, 200 788"                 | ["788", "200"]


    }


    void "test free text search assays #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchAssays(new SearchCommand(searchString: searchString))
        then:
        _ * this.queryService.findAssaysByTextSearch(_, _, _, _) >> {assayAdapterMap}
        and:
        response.status == statusCode
        where:
        label                 | searchString | statusCode                         | assayAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [assayAdapters: [buildAssayAdapter(1234, "assay1"), buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test free text search projects #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchProjects(new SearchCommand(searchString: searchString))
        then:
        _ * this.queryService.findProjectsByTextSearch(_, _, _, _) >> {projectAdapterMap}
        and:
        response.status == statusCode
        where:
        label                 | searchString | statusCode                         | projectAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [projectAdapters: [buildProjectAdapter(1234, "assay1"), buildProjectAdapter(1234, "assay2")], facets: [], nHits: 2]

    }

    void "test free text search compounds #label"() {
        given:
        params.searchString = searchString
        when:
        request.method = 'GET'
        controller.searchCompounds(new SearchCommand(searchString: searchString))
        then:
        _ * this.queryService.findCompoundsByTextSearch(_, _, _, _) >> {compoundAdapterMap}
        and:
        response.status == statusCode
        where:
        label                 | searchString | statusCode                         | compoundAdapterMap
        "Empty Search String" | ""           | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "1234,5678"  | HttpServletResponse.SC_OK          | [compoundAdapters: [buildCompoundAdapter(1234), buildCompoundAdapter(4567)], facets: [], nHits: 2]

    }

    void "test get Applied Filters Empty Filters"() {
        when:
        final Map filters = controller.getAppliedFilters([], [])
        then:
        assert !filters.searchFilters
        assert !filters.appliedFiltersDisplayedOutsideFacetsGrouped
        assert !filters.appliedFiltersNotInFacetsGrouped
    }

    void "test structure search #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.CompoundFacetForm.toString(), searchString: searchString, filters: filters]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)
        when:
        request.method = 'GET'
        controller.searchStructures(searchCommand)
        then:
        _ * this.queryService.structureSearch(_, _, _, _, _) >> {compoundAdapterMap}
        and:
        flash.message == flashMessage
        response.status == statusCode
        where:
        label                 | searchString | flashMessage                                                                | filters        | statusCode                         | compoundAdapterMap
        "Empty Search String" | ""           | 'Search String is required, must be of the form StructureSearchType:Smiles' | searchFilters1 | HttpServletResponse.SC_BAD_REQUEST | null
        "Throws Exception"    | "1234,5678"  | 'Search String is required, must be of the form StructureSearchType:Smiles' | searchFilters1 | HttpServletResponse.SC_BAD_REQUEST | null
        "Success"             | "Exact:CCC"  | null                                                                        | searchFilters1 | HttpServletResponse.SC_OK          | [compoundAdapters: [buildCompoundAdapter(4567)], facets: [], nHits: 2]
        "Success No Filters"  | "Exact:CCC"  | null                                                                        | []             | HttpServletResponse.SC_OK          | [compoundAdapters: [buildCompoundAdapter(4567)], facets: [], nHits: 2]

    }

    void "test structure search throws Exception"() {
        given:
        mockCommandObject(SearchCommand)
        SearchCommand searchCommand = null
        when:
        request.method = 'GET'
        controller.searchStructures(searchCommand)
        then:
        _ * this.queryService.structureSearch(_, _, _, _, _) >> {new RuntimeException("Error Message")}
        and:
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }
    /**
     *
     * {@link SearchHelper#handleSearchParams()}
     */
    void "test handleSearchParams"() {
        given:
        params.max = max
        params.offset = offset
        when:
        Map results = controller.handleSearchParams()
        then:
        assert results.top == expectedMax
        assert results.skip == expectedOffset

        where:
        label                              | max  | offset | expectedMax | expectedOffset
        "With No max and No offset params" | ""   | ""     | 10          | 0
        "With max params only"             | "20" | ""     | 20          | 0
        "With offset params only"          | ""   | "20"   | 10          | 20
        "With max and offset params"       | "20" | "10"   | 20          | 10
    }

    /**
     * {@link SearchHelper#removeDuplicatesFromSearchString(SearchCommand)}
     */
    void "test removeDuplicatesFromSearchString: #label"() {
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.ProjectFacetForm.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.ProjectFacetForm.toString(), searchString: searchString, filters: []]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when: "We call the removeDuplicates method with the given search string"
        controller.removeDuplicatesFromSearchString(searchCommand)
        then: "We expected to get back a new search string with the duplicates removed"
        assert searchCommand.searchString == expectedResult

        where:
        label                              | searchString                                         | expectedResult
        "String without duplicates"        | "abc,efg"                                            | "abc,efg"
        "String with duplicates"           | "abc,efg,abc"                                        | "abc,efg"
        "String with duplicates in quotes" | "\"abc,efg,abc\""                                    | "\"abc,efg,abc\""
        "GO term with comma in quotes"     | "gobp_term:\"synaptic transmission, glutamatergic\"" | "gobp_term:\"synaptic transmission, glutamatergic\""
    }

    void "test searchProjectsByIDs action"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.ProjectFacetForm.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.ProjectFacetForm.toString(), searchString: searchString, filters: filters]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)
        when:
        request.method = 'GET'
        controller.searchProjectsByIDs(searchCommand)
        then:
        queryService.findProjectsByPIDs(_, _) >> {projectAdapterMap}
        and:
        response.status == statusCode
        where:
        label                                  | searchString     | projectAdapterMap                                          | statusCode                                   | filters
        "Search Projects By Ids"               | "1234, 4567"     | [projectAdapters: [buildProjectAdapter(1234, "project1")]] | HttpServletResponse.SC_OK                    | searchFilters1
        "Search Projects By Non existing Ids"  | "12, 47"         | null                                                       | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | searchFilters1
        "Search Projects By Id No Filters"     | "1234, 4567"     | [projectAdapters: [buildProjectAdapter(1234, "project1")]] | HttpServletResponse.SC_OK                    | []
        "Search Projects By Id with Id syntax" | "PID:1234, 4567" | [projectAdapters: [buildProjectAdapter(1234, "project1")]] | HttpServletResponse.SC_OK                    | []

    }

    void "test searchAssaysByIDs action #label"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.AssayFacetForm.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.AssayFacetForm.toString(), searchString: searchString, filters: filters]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)

        when:
        request.method = 'GET'
        controller.searchAssaysByIDs(searchCommand)
        then:
        queryService.findAssaysByADIDs(_, _) >> {assayAdapterMap}
        and:
        assert response.status == statusCode
        where:
        label                                 | searchString      | assayAdapterMap                                                            | statusCode                                   | filters
        "Search Assays By Ids"                | "1234, 4567"      | [assayAdapters: [buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2] | HttpServletResponse.SC_OK                    | searchFilters1
        "Search Assays By Ids with id syntax" | "ADID:1234, 4567" | [assayAdapters: [buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2] | HttpServletResponse.SC_OK                    | []
        "Search Assays By Non existing Ids"   | "12, 47"          | null                                                                       | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | searchFilters1
        "Search Assays By Id No Filters"      | "1234, 4567"      | [assayAdapters: [buildAssayAdapter(1234, "assay2")], facets: [], nHits: 2] | HttpServletResponse.SC_OK                    | []


    }

    void "test searchCompoundsByIDs action"() {
        given:
        mockCommandObject(SearchCommand)
        params.formName = FacetFormType.CompoundFacetForm.CompoundFacetForm.toString()
        Map paramMap = [formName: FacetFormType.CompoundFacetForm.toString(), searchString: searchString, filters: filters]
        controller.metaClass.getParams {-> paramMap}
        SearchCommand searchCommand = new SearchCommand(paramMap)
        when:
        request.method = 'GET'
        controller.searchCompoundsByIDs(searchCommand)
        then:
        queryService.findCompoundsByCIDs(_, _) >> {compoundAdapterMap}
        and:
        assert response.status == statusCode
        where:
        label                                    | searchString     | compoundAdapterMap                                                     | statusCode                                   | filters
        "Search Compounds By Id"                 | "1234, 4567"     | [compoundAdapters: [buildCompoundAdapter(4567)], facets: [], nHits: 2] | HttpServletResponse.SC_OK                    | searchFilters1
        "Search Compounds Non existing Ids"      | "12, 45"         | null                                                                   | HttpServletResponse.SC_INTERNAL_SERVER_ERROR | searchFilters1
        "Search Compounds By Id No Filters"      | "1234, 4567"     | [compoundAdapters: [buildCompoundAdapter(4567)], facets: [], nHits: 2] | HttpServletResponse.SC_OK                    | []
        "Search Compounds By Id using ID Syntax" | "CID:1234, 4567" | [compoundAdapters: [buildCompoundAdapter(4567)], facets: [], nHits: 2] | HttpServletResponse.SC_OK                    | []


    }

    void "test searchProjectsByIDs Empty Search String"() {
        given:
        mockCommandObject(SearchCommand)
        SearchCommand searchCommand = new SearchCommand(searchString: "")
        when:
        request.method = 'GET'
        controller.searchProjectsByIDs(searchCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST

    }

    void "test searchAssaysByIDs Empty Search String"() {
        given:
        mockCommandObject(SearchCommand)
        SearchCommand searchCommand = new SearchCommand(searchString: EMPTY_STRING)
        when:
        request.method = 'GET'
        controller.searchAssaysByIDs(searchCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST

    }

    void "test searchCompoundsByIDs Empty Search String"() {
        given:
        mockCommandObject(SearchCommand)
        SearchCommand searchCommand = new SearchCommand(searchString: EMPTY_STRING)
        when:
        request.method = 'GET'
        controller.searchCompoundsByIDs(searchCommand)
        then:
        assert response.status == HttpServletResponse.SC_BAD_REQUEST
    }

    void "Demonstrate that there are carts assays"() {
        when:
        assertNotNull shoppingCartService
        CartAssay cartAssay = new CartAssay(name: "foo", id: 3)

        then:
        assertNotNull cartAssay
    }

    void "test showCompound with Exception"() {
        given:
        Integer cid = 872
        when:
        request.method = 'GET'
        controller.showCompound(cid)
        then:
        queryService.showCompound(_) >> { new RuntimeException("ee") }
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    void "test showCompound #label"() {
        when:
        request.method = 'GET'
        controller.showCompound(cid)
        then:
        queryService.showCompound(_) >> { compoundAdapter }
        expectedView == view

        if (cid && compoundAdapter) {
            assert model.compound
            expectedCID == model.compound.getPubChemCID()
        }
        response.status == statusCode

        where:
        label                                             | cid  | compoundAdapter           | expectedCID | expectedView                     | statusCode
        "Render show compound page"                       | 872  | buildCompoundAdapter(872) | 872         | "/bardWebInterface/showCompound" | 200
        "Render not found Compound string, null Adapter"  | null | null                      | null        | null                             | 404
        "Render not found Compound with Compound Adpater" | null | buildCompoundAdapter(872) | null        | "/bardWebInterface/showCompound" | 200
        "Compound does not exist"                         | -1   | null                      | null        | null                             | 404

    }

    void "test showAssay with Exception"() {
        given:
        Integer adid = 872
        when:
        request.method = 'GET'
        controller.showAssay(adid)
        then:
        queryService.showAssay(_) >> { new RuntimeException("ee") }
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    void "test showAssay #label"() {

        when:
        request.method = 'GET'
        controller.showAssay(adid)

        then:
        queryService.showAssay(_) >> { assayAdapter }

        expectedAssayView == view
        if (adid && assayAdapter) {
            assert model.assayAdapter
            adid == model.assayAdapter.assay.id
            name == model.assayAdapter.name
        }
        assert response.status == statusCode
        where:
        label                    | adid   | name   | assayAdapter                                                       | expectedAssayView             | statusCode
        "Return an assayAdapter" | 485349 | "Test" | [assayAdapter: buildAssayAdapter(485349, "Test"), experiments: []] | "/bardWebInterface/showAssay" | 200
        "A null AssayAdapter"    | null   | "Test" | [assayAdapter: null, experiments: []]                              | null                          | 404
        "Assay ADID is null"     | null   | "Test" | [assayAdapter: buildAssayAdapter(485349, "Test"), experiments: []] | "/bardWebInterface/showAssay" | 200
        "Assay Adapter is null"  | null   | "Test" | null                                                               | null                          | 500

    }

    void "test showProject with Exception"() {
        given:
        Integer pid = 872
        when:
        request.method = 'GET'
        controller.showProject(pid)
        then:
        queryService.showProject(_) >> { new RuntimeException("ee") }
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR
    }

    void "test showProject #label"() {

        when:
        request.method = 'GET'
        controller.showProject(pid)

        then:
        queryService.showProject(_) >> {projectAdapter }

        expectedProjectView == view
        if (pid && projectAdapter) {
            assert model.projectAdapter
            pid == model.projectAdapter.project.id
            name == model.projectAdapter.name
        }
        assert response.status == statusCode
        where:
        label                                 | pid    | name   | projectAdapter                                                         | expectedProjectView             | statusCode
        "Return a ProjectSearchResult"        | 485349 | "Test" | [projectAdapter: buildProjectAdapter(485349, "Test"), experiments: []] | "/bardWebInterface/showProject" | 200
        "ProjectSearchResult PID is null"     | null   | "Test" | [projectAdapter: buildProjectAdapter(485349, "Test"), experiments: []] | null                            | 404
        "ProjectSearchResult Adapter is null" | null   | "Test" | null                                                                   | null                            | 404
    }


    void "test autocomplete #label"() {
        when:
        request.method = 'GET'
        controller.params.term = searchString
        controller.autoCompleteAssayNames()
        then:
        queryService.autoComplete(_) >> { expectedList }
        controller.response.json.toString() == new JSONArray(expectedList.toString()).toString()

        where:
        label                | searchString | expectedList
        "Return two strings" | "Bro"        | ["Broad Institute MLPCN Platelet Activation"]

    }

    void "test autocomplete throw exception"() {
        when:
        request.method = 'GET'
        controller.params.term = searchString
        controller.autoCompleteAssayNames()
        then:
        queryService.autoComplete(_) >> { throw new HttpException("Error") }
        response.status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR

        where:
        label                | searchString
        "Return two strings" | ""

    }

    void "test getAppliedFilters #label"() {
        given:

        when:
        Map appliedFilters = controller.getAppliedFilters(testSearchFilters, facets)

        then:
        assert appliedFilters.searchFilters == testSearchFilters
        assert appliedFilters?.appliedFiltersNotInFacetsGrouped?.get(groupNameA) == expectedTestFilters
        assert appliedFilters?.appliedFiltersDisplayedOutsideFacetsGrouped?.get(groupNameB) == expectedTestFilters

        where:
        facets           | testSearchFilters  | groupNameA | groupNameB | expectedTestFilters | label
        [facet1]         | searchFilters      | 'group2'   | 'group2'   | [searchFilters[1]]  | "one filter; two facets; one overlapping"
        [facet1, facet3] | searchFilters      | 'group2'   | 'group2'   | [searchFilters[1]]  | "two filters; two facets; one overlapping"
        [facet1]         | [searchFilters[0]] | null       | null       | null                | "one filter; one facet; no overlapping"
        []               | [searchFilters[0]] | null       | null       | null                | "no (empty) filter; one facet; no overlapping"
        [facet1]         | []                 | null       | null       | null                | "one filter; no (empty) facet; no overlapping"
        []               | []                 | null       | null       | null                | "no (empty) filter; no (empty) facet; no overlapping"
    }

    void "test getAppliedFiltersAlreadyInFacets #label"() {
        given:

        when:
        List<SearchFilter> result = controller.getAppliedFiltersAlreadyInFacets(testSearchFilters, facets)

        then:
        assert result == expectedTestFilters

        where:
        facets           | testSearchFilters  | expectedTestFilters | label
        [facet1]         | searchFilters      | [searchFilters[0]]  | "one filter; two facets; one overlapping"
        [facet1, facet3] | searchFilters      | [searchFilters[0]]  | "two filters; two facets; one overlapping"
        [facet1]         | [searchFilters[0]] | [searchFilters[0]]  | "one filter; one facet; no overlapping"
        []               | [searchFilters[0]] | []                  | "no (empty) filter; one facet; no overlapping"
        [facet1]         | []                 | []                  | "one filter; no (empty) facet; no overlapping"
        []               | []                 | []                  | "no (empty) filter; no (empty) facet; no overlapping"
    }

    void "test getAppliedFiltersNotInFacetsGrouped #label"() {
        given:

        when:
        Map<String, List<SearchFilter>> result = controller.groupSearchFilters(testSearchFilters)

        then:
        assert result.keySet().toList() == expectedKeys

        where:
        testSearchFilters  | expectedKeys         | label
        searchFilters      | ['group1', 'group2'] | "two filters"
        [searchFilters[0]] | ['group1']           | "one filter"
        []                 | []                   | "no filter"
    }

    //This test is very similar to 'test getAppliedFiltersAlreadyInFacets()' because this one tests for parent (group) name while the other one test for child (filter/facet) name.
    //The searchFilter/facets objects were set up to match both on parent and child names.
    void "test getAppliedFiltersDisplayedOutsideFacets #label"() {
        given:

        when:
        List<SearchFilter> result = controller.getAppliedFiltersAlreadyInFacets(testSearchFilters, facets)

        then:
        assert result == expectedTestFilters

        where:
        facets           | testSearchFilters  | expectedTestFilters | label
        [facet1]         | searchFilters      | [searchFilters[0]]  | "one filter; two facets; one overlapping"
        [facet1, facet3] | searchFilters      | [searchFilters[0]]  | "two filters; two facets; one overlapping"
        [facet1]         | [searchFilters[0]] | [searchFilters[0]]  | "one filter; one facet; no overlapping"
        []               | [searchFilters[0]] | []                  | "no (empty) filter; one facet; no overlapping"
        [facet1]         | []                 | []                  | "one filter; no (empty) facet; no overlapping"
        []               | []                 | []                  | "no (empty) filter; no (empty) facet; no overlapping"
    }


    CompoundAdapter buildCompoundAdapter(final Long cid) {
        final Compound compound = new Compound()
        compound.setCid(cid.intValue())
        return new CompoundAdapter(compound)
    }

    AssayAdapter buildAssayAdapter(final Long adid, final String name) {
        final Assay assay = new Assay()
        assay.setName(name)
        assay.setAssayId(adid)
        return new AssayAdapter(assay, 0, null)
    }

    ProjectAdapter buildProjectAdapter(final Long pid, final String name) {
        final Project project = new Project()
        project.setName(name)
        project.setProjectId(pid)
        return new ProjectAdapter(project)
    }
}
